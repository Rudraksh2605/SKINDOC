package com.hfad.skindoc.ml

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hfad.skindoc.R

class ml : AppCompatActivity() {

    private lateinit var disease_name: TextView
    private lateinit var predicted_score: TextView
    private lateinit var doctor_name: TextView
    private lateinit var doctor_specialty: TextView
    private lateinit var doctor_City: TextView
    private lateinit var disease_image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ml_prediction)

        setupTextView()

        val resultMap = intent.extras?.getSerializable("resultMap") as? HashMap<String, Float>
        val imageBitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")

        if (resultMap != null) {
            disease_image.setImageBitmap(imageBitmap)


            val predictedClass = resultMap["predictedClass"]?.toInt() ?: -1
            val confidenceScore = resultMap["confidenceScore"] ?: 0.0f

            if (confidenceScore >= 0.85f) {

                val diseaseName = getDiseaseName(predictedClass)

                disease_name.text = "$diseaseName"
                predicted_score.text = "${"%.2f".format(confidenceScore * 100)}%"
            } else {

                disease_name.text = "Prediction not reliable"
                predicted_score.text = "${"%.2f".format(confidenceScore * 100)}%"
            }
        } else {
            disease_name.text = "No prediction available"
            predicted_score.text = "No confidence score available"
        }
    }

    private fun setupTextView() {
        disease_name = findViewById(R.id.DiseaseName)
        predicted_score = findViewById(R.id.confidence_score)
        doctor_City = findViewById(R.id.doctorCity)
        doctor_name = findViewById(R.id.doctorName)
        doctor_specialty = findViewById(R.id.doctorSpecialty)
        disease_image = findViewById(R.id.d_image)
    }


    private fun getDiseaseName(classNumber: Int): String {
        return when (classNumber) {
            1 -> "Actinic keratosis"
            2 -> "Basal cell carcinoma"
            3 -> "Benign keratosis"
            4 -> "Dermatofibroma"
            5 -> "Melanocytic nevus"
            6 -> "Melanoma"
            7 -> "Squamous cell carcinoma"
            8 -> "Vascular lesion"
            else -> "Unknown Disease"
        }
    }
}
