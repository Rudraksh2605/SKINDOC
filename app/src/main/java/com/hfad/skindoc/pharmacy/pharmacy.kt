package com.hfad.skindoc.pharmacy

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
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
import com.hfad.skindoc.home.Home
import com.hfad.skindoc.home.Home.Companion.CAMERA_REQUEST_CODE

class pharmacy : AppCompatActivity() {

    private lateinit var pharmacyRecycler: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var pharmacyAdapter: PharmacyAdapter
    private lateinit var pharmacyMedList: MutableList<PharmacyDataModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pharmacy)


        db = FirebaseFirestore.getInstance()


        pharmacyMedList = mutableListOf()


        pharmacyRecycler = findViewById<RecyclerView>(R.id.pharmacy)
        pharmacyRecycler.layoutManager = LinearLayoutManager(this)
        pharmacyAdapter = PharmacyAdapter(pharmacyMedList)
        pharmacyRecycler.adapter = pharmacyAdapter


        fetchMedic()

        setupNavigation()
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

    private fun fetchMedic() {
        db.collection("SkincareProducts").document("Products")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val medic = document.get("products") as? List<Map<String, String>>
                    if (medic != null) {
                        for (MedicMap in medic) {
                            val med = PharmacyDataModel(
                                name = MedicMap["name"] ?: "Unknown",
                                price = MedicMap["price"] ?: "Unknown",
                                features = MedicMap["features"] ?: "Unknown",
                                bestFor = MedicMap["bestFor"] ?: "Unknown"
                            )
                            pharmacyMedList.add(med)
                        }
                        // Notify the adapter of data changes
                        pharmacyAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "No medicines found", Toast.LENGTH_SHORT).show()
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
