package com.hfad.skindoc.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.hfad.skindoc.R
import com.hfad.skindoc.home.Home

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)


        auth = FirebaseAuth.getInstance()


        val videoView: VideoView = findViewById(R.id.videoView)


        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.skindoc}")
        videoView.setVideoURI(videoUri)
        videoView.start()


        videoView.setOnCompletionListener {
            if (auth.currentUser != null) {

                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            } else {

                val intent = Intent(this, Start::class.java)
                startActivity(intent)
            }
            finish()
        }
    }
}
