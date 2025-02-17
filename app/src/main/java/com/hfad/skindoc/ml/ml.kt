package com.hfad.skindoc.ml

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hfad.skindoc.R
import com.bumptech.glide.Glide
import com.hfad.skindoc.article.ArticlesListActivity
import com.hfad.skindoc.chatbot.ChatBotActivity
import com.hfad.skindoc.event.EventsActivity
import com.hfad.skindoc.home.Home
import com.hfad.skindoc.home.Home.Companion.CAMERA_REQUEST_CODE
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import com.google.firebase.firestore.FirebaseFirestore
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hfad.skindoc.appointment.DoctorDetailAppointmentActivity

class ml : AppCompatActivity() {

    private lateinit var disease_name: TextView
    private lateinit var prediction_score: TextView
    private lateinit var disease_image: ImageView
    private lateinit var doctor_name: TextView
    private lateinit var doctor_speciality: TextView
    private lateinit var doctor_city: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var capitalizedDiseaseName: String = ""

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ml_prediction)

        setupNavigation()

        disease_name = findViewById(R.id.DiseaseName)
        prediction_score = findViewById(R.id.confidence_score)
        doctor_name = findViewById(R.id.doctorName)
        doctor_speciality = findViewById(R.id.doctorSpecialty)
        doctor_city = findViewById(R.id.doctorCity)





        disease_name.setText(capitalizedDiseaseName)


        val diseaseName = intent.getStringExtra("disease_name") ?: "No disease name"

        capitalizedDiseaseName = diseaseName.replaceFirstChar { it.uppercase() }
        val predictionScore = intent.getStringExtra("prediction_score") ?: "No prediction score"
        val imageUriString = intent.getStringExtra("image_uri")

        Log.d("capitalizedDiseaseName","$capitalizedDiseaseName")




        imageUriString?.let {
            val imageUri = Uri.parse(it)
            Glide.with(this)
                .load(imageUriString)
                .into(disease_image)
        }


        prediction_score.text = predictionScore
        disease_name.setText(capitalizedDiseaseName)



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getUserLocation()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude

                val nearestCity = getNearestCity(lat, lon)
                fetchDoctorDetails(nearestCity)
            } else {
                Log.d("Location", "Location is null")
            }
        }
    }

    private fun getNearestCity(lat: Double, lon: Double): String {
        val cities = listOf(
            Pair("Bangalore", Location("").apply { latitude = 12.9716; longitude = 77.5946 }),
            Pair("Bhopal", Location("").apply { latitude = 23.2599; longitude = 77.4126 }),
            Pair("Chandigarh", Location("").apply { latitude = 30.7333; longitude = 76.7794 }),
            Pair("Indore", Location("").apply { latitude = 22.7196; longitude = 75.8577 })
        )

        var nearestCity = cities[0]
        var smallestDistance = Float.MAX_VALUE

        for (city in cities) {
            val cityLocation = city.second
            val results = FloatArray(1)
            Location.distanceBetween(lat, lon, cityLocation.latitude, cityLocation.longitude, results)
            val distance = results[0]

            if (distance < smallestDistance) {
                smallestDistance = distance
                nearestCity = city
            }
        }

        return nearestCity.first
    }

    private fun fetchDoctorDetails(cityName: String) {
        val db = FirebaseFirestore.getInstance()
        val diseaseName = if (capitalizedDiseaseName.isNotEmpty()) "Acne" else "Not Available"


        val docRef = db.collection(diseaseName).document(cityName)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val doctors = document.get("doctors") as? List<Map<String, String>>
                if (doctors != null && doctors.isNotEmpty()) {
                    val doctor = doctors[0]

                    doctor_name.text = doctor["name"] ?: "Unknown"
                    doctor_speciality.text = doctor["contact"] ?: "Not available"
                    doctor_city.text = doctor["address"] ?: "Not available"




                    val doctorCard = findViewById<CardView>(R.id.doctorCard)

                    doctorCard.setOnClickListener {
                        val intent = Intent(this, DoctorDetailAppointmentActivity::class.java)

                        intent.putExtra("doctor_name", doctor["name"])
                        intent.putExtra("doctor_clinic", cityName)
                        startActivity(intent)
                    }
                }
            } else {
                Log.d("Fetch Doctor", "No such document")
            }
        }
    }


    private fun setupNavigation() {
        val btn_article = findViewById<ImageButton>(R.id.nav_article)
        val btn_home = findViewById<ImageButton>(R.id.nav_home)
        val btn_scanner = findViewById<ImageButton>(R.id.nav_scanner)
        val btn_event = findViewById<ImageButton>(R.id.nav_schedule)
        val btn_chat_bot = findViewById<ImageButton>(R.id.nav_bot)

        btn_article.setOnClickListener {
            val intent = Intent(this, ArticlesListActivity::class.java)
            startActivity(intent)
        }

        btn_home.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        btn_scanner.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }

        btn_event.setOnClickListener {
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
        }

        btn_chat_bot.setOnClickListener {
            val intent = Intent(this, ChatBotActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            } else {
                Log.d("Permission", "Location permission denied")
            }
        }
    }
}
