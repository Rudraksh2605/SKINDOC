package com.hfad.skindoc.chatbot

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hfad.skindoc.R
import io.kommunicate.KmConversationBuilder
import io.kommunicate.Kommunicate
import io.kommunicate.callbacks.KmCallback // Import the callback interface

class ChatBotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        val sharedPreferences: SharedPreferences = getSharedPreferences("applozic", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()


        Kommunicate.init(this, "2a358c41671e4210b93cac6c6c0b77d7e")

        // Launch the chatbot
        KmConversationBuilder(this)
            .setSingleConversation(true)
            .launchConversation(object : KmCallback {
                override fun onSuccess(message: Any?) {
                    // Chatbot launched successfully
                    println("Chatbot launched successfully: $message")
                }

                override fun onFailure(error: Any?) {

                    println("Failed to launch chatbot: $error")
                }
            })
    }
}
