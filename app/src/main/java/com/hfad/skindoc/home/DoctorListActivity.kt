package com.hfad.skindoc.home

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.skindoc.R
import com.hfad.skindoc.article.ArticlesListActivity
import com.hfad.skindoc.chatbot.ChatBotActivity
import com.hfad.skindoc.event.EventsActivity
import com.hfad.skindoc.home.Home.Companion.CAMERA_REQUEST_CODE

import android.widget.ArrayAdapter
import android.widget.Spinner

class DoctorListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorAdapter
    private lateinit var doctorList: MutableList<DoctorDataModel>
    private lateinit var db: FirebaseFirestore
    private lateinit var citySpinner: Spinner
    private lateinit var selectedCity: String

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

    private fun fetchDoctors() {
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
                            Log.d("OKKKKK", doctorList.toString())
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
}


