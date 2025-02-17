package com.hfad.skindoc.home

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.skindoc.R
import com.hfad.skindoc.article.ArticlesListActivity
import com.hfad.skindoc.chatbot.ChatBotActivity
import com.hfad.skindoc.event.EventsActivity
import com.hfad.skindoc.home.Home.Companion.CAMERA_REQUEST_CODE
import android.Manifest
import android.content.pm.PackageManager
import android.widget.ArrayAdapter
import androidx.annotation.RequiresPermission

class DoctorListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorAdapter
    private lateinit var doctorList: MutableList<DoctorDataModel>
    private lateinit var db: FirebaseFirestore
    private lateinit var citySpinner: Spinner
    private lateinit var selectedCity: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_layout)

        val btn_article = findViewById<ImageButton>(R.id.nav_article)
        val btn_home = findViewById<ImageButton>(R.id.nav_home)
        val btn_scanner = findViewById<ImageButton>(R.id.nav_scanner)
        val btn_event = findViewById<ImageButton>(R.id.nav_schedule)
        val btn_chat_bot = findViewById<ImageButton>(R.id.nav_bot)

        citySpinner = findViewById(R.id.city_spinner)

        val cities = listOf("Bangalore", "Chandigarh", "Indore", "Bhopal")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter

        // Initialize the fused location client for getting the user's location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getUserLocation() // Automatically get the nearest city if permission is granted
        }

        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedCity = parentView?.getItemAtPosition(position).toString()
                doctorList.clear()
                doctorAdapter.notifyDataSetChanged()
                fetchDoctors()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                selectedCity = "Bangalore"
                doctorList.clear()
                doctorAdapter.notifyDataSetChanged()
                fetchDoctors()
            }
        }

        recyclerView = findViewById(R.id.doctor_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

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

        doctorList = mutableListOf()
        doctorAdapter = DoctorAdapter(doctorList)
        recyclerView.adapter = doctorAdapter

        db = FirebaseFirestore.getInstance()
    }

    // Function to get the user's location
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                val nearestCity = getNearestCity(lat, lon)
                fetchDoctorsForCity(nearestCity) // Fetch doctors for the nearest city
                setSpinnerToCity(nearestCity) // Update spinner with the nearest city
            }
        }
    }

    // Function to calculate the nearest city based on user's location
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

    // Function to set the spinner to the nearest city
    private fun setSpinnerToCity(city: String) {
        val cities = listOf("Bangalore", "Chandigarh", "Indore", "Bhopal")
        val cityPosition = cities.indexOf(city)
        if (cityPosition != -1) {
            citySpinner.setSelection(cityPosition)
        }
    }

    private fun fetchDoctors() {
        doctorList.clear()
        db.collection("Atopic Dermatitis").document(selectedCity)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val doctors = document.get("doctors") as? List<Map<String, String>>
                    if (doctors != null) {
                        for (doctorMap in doctors) {
                            val doctor = DoctorDataModel(
                                name = doctorMap["name"] ?: "Unknown",
                                clinic = doctorMap["clinic"] ?: "Unknown",
                                address = doctorMap["address"] ?: "Unknown",
                                contact = doctorMap["contact"] ?: "Unknown"
                            )
                            doctorList.add(doctor)
                        }
                        doctorAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "No doctors found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun fetchDoctorsForCity(city: String) {
        selectedCity = city
        doctorList.clear()
        doctorAdapter.notifyDataSetChanged()
        fetchDoctors()
    }

    // Handle location permission results
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
