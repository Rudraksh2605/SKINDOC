package com.hfad.skindoc.ml

import com.google.gson.annotations.SerializedName


data class ApiResponse(
    val prediction: List<Prediction>
)

data class Prediction(
    @SerializedName("class") val className: String,
    val probability: Double
)
