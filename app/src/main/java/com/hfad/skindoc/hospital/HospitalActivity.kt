package com.hfad.skindoc.hospital

import android.content.Intent
import android.location.Location
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.annotation.RequiresPermission
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
import com.hfad.skindoc.home.Home
import com.hfad.skindoc.home.Home.Companion.CAMERA_REQUEST_CODE

class HospitalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var hospitalAdapter: HospitalAdapter
    private val db = FirebaseFirestore.getInstance()
    private val hospitals = mutableListOf<Hospital>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hospital)

        recyclerView = findViewById(R.id.hospital_recycler_view)
        spinner = findViewById(R.id.city_spinner)

        recyclerView.layoutManager = LinearLayoutManager(this)
        hospitalAdapter = HospitalAdapter(hospitals, this)
        recyclerView.adapter = hospitalAdapter

        setupNavigation()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getUserLocation()
        }

        // Set up the spinner with cities
        val cities = listOf("Bangalore", "Bhopal", "Chandigarh", "Indore")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Spinner selection listener to fetch hospitals based on selected city
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCity = parent.getItemAtPosition(position).toString()
                fetchHospitals(selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // Function to get the user's location
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                val nearestCity = getNearestCity(lat, lon)
                fetchHospitals(nearestCity) // Pass the nearest city to Firestore
                setSpinnerToCity(nearestCity) // Set the spinner to the nearest city
            }
        }
    }

    // Function to calculate the nearest city based on user location
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

        return nearestCity.first // Return the name of the nearest city
    }

    // Function to set the spinner to the nearest city
    private fun setSpinnerToCity(city: String) {
        val cities = listOf("Bangalore", "Bhopal", "Chandigarh", "Indore")
        val cityPosition = cities.indexOf(city)
        if (cityPosition != -1) {
            spinner.setSelection(cityPosition)
        }
    }

    private fun fetchHospitals(city: String) {
        hospitals.clear()

        db.collection("HOSPITAL")
            .document(city)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val hospitalsList = documentSnapshot.get("hospitals") as? List<Map<String, Any>>
                    hospitalsList?.let { hospitalList ->
                        val hospitalObjects = hospitalList.map { hospitalMap ->
                            val name = hospitalMap["name"] as? String ?: "Unknown Name"
                            val address = hospitalMap["address"] as? String ?: "Unknown Address"
                            val contact = hospitalMap["contact"] as? String ?: "Unknown Contact"
                            val specialties = hospitalMap["specialties"] as? String ?: "Not Specified"

                            Hospital(name, address, contact, specialties)
                        }

                        hospitalAdapter.updateHospitalList(hospitalObjects)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HospitalActivity", "Error fetching hospitals: ${exception.message}", exception)
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
