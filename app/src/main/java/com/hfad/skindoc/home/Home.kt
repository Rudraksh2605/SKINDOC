package com.hfad.skindoc.home

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.skindoc.article.ArticlesListActivity
import com.hfad.skindoc.chatbot.ChatBotActivity
import com.hfad.skindoc.event.EventsActivity
import com.hfad.skindoc.R
import com.hfad.skindoc.appointment.DoctorDetailAppointmentActivity
import com.hfad.skindoc.pharmacy.pharmacy
import com.hfad.skindoc.userprofile.UserProfileActivity

class Home : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        db = FirebaseFirestore.getInstance()

        val btn_user = findViewById<ImageButton>(R.id.user)
        val et_search_text = findViewById<EditText>(R.id.et_search_box)
        val btn_article = findViewById<ImageButton>(R.id.nav_article)
        val btn_home = findViewById<ImageButton>(R.id.nav_home)
        val btn_scanner = findViewById<ImageButton>(R.id.nav_scanner)
        val btn_event = findViewById<ImageButton>(R.id.nav_schedule)
        val btn_chat_bot = findViewById<ImageButton>(R.id.nav_bot)
        val btn_see_more_doc = findViewById<Button>(R.id.doctor_see_more)
        val btn_see_more_article = findViewById<Button>(R.id.article_see_more)
        val btn_doctor = findViewById<ImageButton>(R.id.doctor_image)
        val btn_pahrmacy = findViewById<ImageButton>(R.id.pharmacy)

        val doc_name_1 = findViewById<TextView>(R.id.doc_name_1)
        val contact_1 = findViewById<TextView>(R.id.contact_1)

        val doc_name_2 = findViewById<TextView>(R.id.doc_name_2)
        val contact_2 = findViewById<TextView>(R.id.contact_2)

        val doc_name_3 = findViewById<TextView>(R.id.doc_name_3)
        val contact_3 = findViewById<TextView>(R.id.contact_3)

        val doc_name_4 = findViewById<TextView>(R.id.doc_name_4)
        val contact_4 = findViewById<TextView>(R.id.contact_4)

        fetchDoctorDetails(doc_name_1, contact_1, doc_name_2, contact_2, doc_name_3, contact_3, doc_name_4, contact_4)

        btn_user.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        btn_pahrmacy.setOnClickListener {
            val intent = Intent(this, pharmacy::class.java)
            startActivity(intent)
        }

        btn_article.setOnClickListener {
            val intent = Intent(this, ArticlesListActivity::class.java)
            startActivity(intent)
        }

        btn_home.setOnClickListener {
            Toast.makeText(this, "Already on Home Page", Toast.LENGTH_SHORT).show()
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

        btn_see_more_doc.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            startActivity(intent)
        }

        btn_see_more_article.setOnClickListener {
            val intent = Intent(this, ArticlesListActivity::class.java)
            startActivity(intent)
        }

        btn_doctor.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchDoctorDetails(
        docName1: TextView, contact1: TextView,
        docName2: TextView, contact2: TextView,
        docName3: TextView, contact3: TextView,
        docName4: TextView, contact4: TextView
    ) {
        val docRef = db.collection("Atopic Dermatitis").document("Bangalore")

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val doctors = document.get("doctors") as? List<Map<String, String>>

                    if (doctors != null && doctors.size >= 4) {
                        val doctor1 = doctors[0]
                        val doctor2 = doctors[1]
                        val doctor3 = doctors[2]
                        val doctor4 = doctors[3]

                        runOnUiThread {
                            docName1.text = doctor1["name"]
                            contact1.text = doctor1["contact"]

                            docName2.text = doctor2["name"]
                            contact2.text = doctor2["contact"]

                            docName3.text = doctor3["name"]
                            contact3.text = doctor3["contact"]

                            docName4.text = doctor4["name"]
                            contact4.text = doctor4["contact"]


                            docName1.setOnClickListener { openDoctorDetailActivity(doctor1["name"] ?: "") }
                            docName2.setOnClickListener { openDoctorDetailActivity(doctor2["name"] ?: "") }
                            docName3.setOnClickListener { openDoctorDetailActivity(doctor3["name"] ?: "") }
                            docName4.setOnClickListener { openDoctorDetailActivity(doctor4["name"] ?: "") }
                        }
                    } else {
                        Log.d("DoctorDetails", "No doctors found or less than 4 doctors available.")
                    }
                } else {
                    Log.d("DoctorDetails", "Document not found.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DoctorDetails", "Error getting documents: ${exception.message}")
                Toast.makeText(this, "Error getting doctors: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openDoctorDetailActivity(doctorName: String) {
        val intent = Intent(this, DoctorDetailAppointmentActivity::class.java)
        intent.putExtra("doctor_name", doctorName)
        startActivity(intent)
    }



    companion object {
        const val CAMERA_REQUEST_CODE = 101
    }
}