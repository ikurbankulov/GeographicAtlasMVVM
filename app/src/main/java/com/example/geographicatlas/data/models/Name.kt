package com.example.geographicatlas.data.models

import com.google.gson.annotations.SerializedName

data class Name(
    @SerializedName("common")
    val name: String
    )