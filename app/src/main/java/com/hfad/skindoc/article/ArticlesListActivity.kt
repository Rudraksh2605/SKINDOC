package com.hfad.skindoc.article

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.skindoc.R
import com.hfad.skindoc.chatbot.ChatBotActivity
import com.hfad.skindoc.event.EventsActivity
import com.hfad.skindoc.home.Home
import com.hfad.skindoc.home.Home.Companion.CAMERA_REQUEST_CODE


class ArticlesListActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val ArticleList = mutableListOf<ArticleModel>()
    private lateinit var ArticleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article)

        setupNavigation()
        setupRecyclerView()
        fetchDoctors()
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


    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.article)
        ArticleAdapter = ArticleAdapter(this, ArticleList) { article ->

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(article.link)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ArticleAdapter
    }


    private fun fetchDoctors() {
        db.collection("Articles").document("Skincare")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val articles = document.get("articles") as? List<Map<String, String>>
                    if (articles != null) {
                        ArticleList.clear()
                        for (articleMap in articles) {
                            val article = ArticleModel(
                                link = articleMap["link"] ?: "Unknown"
                            )
                            ArticleList.add(article)
                        }
                        ArticleAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "No articles found", Toast.LENGTH_SHORT).show()
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
