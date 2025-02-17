package com.hfad.skindoc.ml

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

interface MyApi {

    @Multipart
    @POST("/")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("desc") desc: RequestBody
    ): Call<UploadResponse>

    companion object {
        fun create(): MyApi {
            // Create OkHttpClient with increased timeouts
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // 30 seconds connection timeout
                .readTimeout(30, TimeUnit.SECONDS)     // 30 seconds read timeout
                .writeTimeout(30, TimeUnit.SECONDS)    // 30 seconds write timeout
                .build()

            return Retrofit.Builder()
                .baseUrl("http://16.16.25.6:8000/")  // Your base URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)  // Set the custom OkHttpClient
                .build()
                .create(MyApi::class.java)
        }
    }
}
