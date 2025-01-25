package com.hfad.skindoc.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.skindoc.article.ArticlesListActivity
import com.hfad.skindoc.chatbot.ChatBotActivity
import com.hfad.skindoc.event.EventsActivity
import com.hfad.skindoc.R
import com.hfad.skindoc.appointment.DoctorDetailAppointmentActivity
import com.hfad.skindoc.hospital.HospitalActivity
import com.hfad.skindoc.pharmacy.pharmacy
import com.hfad.skindoc.userprofile.UserProfileActivity

class Home : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var imageView: ImageView
    private val imageList = arrayOf(
        R.drawable.banner, // Add your images here
        R.drawable.banner2,
    )
    private var currentIndex = 0
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        db = FirebaseFirestore.getInstance()

        fetchHospitalDetails()

        imageView = findViewById(R.id.image_slideshow)


        startSlideshow()


        val btn_user = findViewById<ImageButton>(R.id.user)
        val et_search_text = findViewById<EditText>(R.id.et_search_box)
        val btn_article = findViewById<ImageButton>(R.id.nav_article)
        val btn_home = findViewById<ImageButton>(R.id.nav_home)
        val btn_scanner = findViewById<ImageButton>(R.id.nav_scanner)
        val btn_event = findViewById<ImageButton>(R.id.nav_schedule)
        val btn_chat_bot = findViewById<ImageButton>(R.id.nav_bot)
        val btn_see_more_doc = findViewById<Button>(R.id.doctor_see_more)
        val btn_see_more_hospital = findViewById<Button>(R.id.hospital_see_more)
        val btn_doctor = findViewById<ImageButton>(R.id.doctor_image)
        val btn_pahrmacy = findViewById<ImageButton>(R.id.pharmacy)
        val btn_ambulance = findViewById<ImageButton>(R.id.ambulance)
        val btn_hospital = findViewById<ImageButton>(R.id.hospital)


        val doc_name_1 = findViewById<TextView>(R.id.doc_name_1)
        val contact_1 = findViewById<TextView>(R.id.contact_1)

        val doc_name_2 = findViewById<TextView>(R.id.doc_name_2)
        val contact_2 = findViewById<TextView>(R.id.contact_2)

        val doc_name_3 = findViewById<TextView>(R.id.doc_name_3)
        val contact_3 = findViewById<TextView>(R.id.contact_3)

        val doc_name_4 = findViewById<TextView>(R.id.doc_name_4)
        val contact_4 = findViewById<TextView>(R.id.contact_4)

        fetchDoctorDetails(doc_name_1, contact_1, doc_name_2, contact_2, doc_name_3, contact_3, doc_name_4, contact_4)

        btn_see_more_hospital.setOnClickListener {
            val intent = Intent(this, HospitalActivity::class.java)
            startActivity(intent)
        }

        btn_user.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        btn_pahrmacy.setOnClickListener {
            val intent = Intent(this, pharmacy::class.java)
            startActivity(intent)
        }

        btn_hospital.setOnClickListener {
            val intent = Intent(this, HospitalActivity::class.java)
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

        btn_see_more_hospital.setOnClickListener {
            val intent = Intent(this, HospitalActivity::class.java)
            startActivity(intent)
        }

        btn_doctor.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            startActivity(intent)
        }

        btn_ambulance.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:108")
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

    private fun fetchHospitalDetails() {
        val hospitalRef = db.collection("HOSPITAL").document("Bangalore")

        hospitalRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val hospitals = document.get("hospitals") as? List<Map<String, String>>

                    if (hospitals != null && hospitals.size >= 4) {
                        val hospital1 = hospitals[0]
                        val hospital2 = hospitals[1]
                        val hospital3 = hospitals[2]
                        val hospital4 = hospitals[3]


                        findViewById<TextView>(R.id.hospital_1).text = hospital1["name"]
                        findViewById<TextView>(R.id.hospital_1_content).text = hospital1["contact"]


                        findViewById<TextView>(R.id.hospital_2).text = hospital2["name"]
                        findViewById<TextView>(R.id.hospital_2_content).text = hospital2["contact"]


                        findViewById<TextView>(R.id.hospital_3).text = hospital3["name"]
                        findViewById<TextView>(R.id.hospital_3_content).text = hospital3["contact"]


                        findViewById<TextView>(R.id.hospital_4).text = hospital4["name"]
                        findViewById<TextView>(R.id.hospital_4_content).text = hospital4["contact"]
                    } else {
                        Log.d("HospitalDetails", "Less than 4 hospitals available.")
                    }
                } else {
                    Log.d("HospitalDetails", "Document not found.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HospitalDetails", "Error fetching hospitals: ${exception.message}")
                Toast.makeText(this, "Error fetching hospitals: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun startSlideshow() {
        val updateImageRunnable = object : Runnable {
            override fun run() {
                // Set the current image
                imageView.setImageResource(imageList[currentIndex])


                currentIndex = (currentIndex + 1) % imageList.size


                handler.postDelayed(this, 3000)
            }
        }

        // Start the first image change
        handler.post(updateImageRunnable)
    }




    companion object {
        const val CAMERA_REQUEST_CODE = 101
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}