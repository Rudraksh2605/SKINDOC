package com.hfad.skindoc.chatbot

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.hfad.skindoc.R

class ChatBotActivity : AppCompatActivity() {

    private lateinit var chatBotView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        chatBotView = findViewById(R.id.chatbot_view)

        // Set up WebView settings
        val webSettings: WebSettings = chatBotView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true // Enable localStorage support
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // Fix potential content loading issues

        // Ensure links open within the WebView instead of an external browser
        chatBotView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false // Allow WebView to handle all links
            }
        }

        // Load the Botpress WebChat URL
        chatBotView.loadUrl("https://cdn.botpress.cloud/webchat/v2.3/shareable.html?configUrl=https://files.bpcontent.cloud/2025/02/14/19/20250214192417-HT3U6I8R.json")

        // Handle back press inside WebView
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (chatBotView.canGoBack()) {
                    chatBotView.goBack()
                } else {
                    finish()
                }
            }
        })
    }
}
