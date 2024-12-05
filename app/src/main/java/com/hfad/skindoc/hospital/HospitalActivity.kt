package com.hfad.skindoc.hospital

import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hospital)

        recyclerView = findViewById(R.id.hospital_recycler_view)
        spinner = findViewById(R.id.city_spinner)


        recyclerView.layoutManager = LinearLayoutManager(this)
        hospitalAdapter = HospitalAdapter(hospitals, this)
        recyclerView.adapter = hospitalAdapter
        
        setupNavigation()

        val cities = listOf("Bangalore", "Bhopal", "Chandigarh", "Indore")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCity = parent.getItemAtPosition(position).toString()
                fetchHospitals(selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    private fun fetchHospitals(city: String) {

        hospitals.clear()

        db.collection("HOSPITAL")
            .document(city)
            .get()
            .addOnSuccessListener { documentSnapshot ->

                if (documentSnapshot.exists()) {
                    Log.d("HospitalActivity", "Document data: ${documentSnapshot.data}")

                    val hospitalsList = documentSnapshot.get("hospitals") as? List<Map<String, Any>>
                    Log.d("HospitalActivity", "Hospitals list: $hospitalsList")

                    hospitalsList?.let { hospitalList ->

                        val hospitalObjects = hospitalList.map { hospitalMap ->
                            val name = hospitalMap["name"] as? String ?: "Unknown Name"
                            val address = hospitalMap["address"] as? String ?: "Unknown Address"
                            val contact = hospitalMap["contact"] as? String ?: "Unknown Contact"
                            val specialties = hospitalMap["specialties"] as? String ?: "Not Specified"


                            Hospital(name, address, contact, specialties)
                        }

                        hospitalAdapter.updateHospitalList(hospitalObjects)
                        Log.d("HospitalActivity", "Parsed hospitals: $hospitalObjects")

                    } ?: run {
                        Log.e("HospitalActivity", "No hospitals data found in Firestore for city: $city")
                    }
                } else {
                    Log.e("HospitalActivity", "Document for city $city does not exist.")
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





}

