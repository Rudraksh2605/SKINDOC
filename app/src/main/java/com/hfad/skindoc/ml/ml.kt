package com.hfad.skindoc.ml

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hfad.skindoc.R

class ml : AppCompatActivity() {

    private lateinit var disease_name: TextView
    // Initialize other views if needed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ml_prediction)
        disease_name = findViewById(R.id.DiseaseName)

        val result = intent.getStringExtra("api_result") ?: "No result received"
        disease_name.text = result

        // Setup other views with the result if needed.
    }
}
