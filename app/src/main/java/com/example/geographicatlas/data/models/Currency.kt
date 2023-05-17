package com.example.geographicatlas.data.models

import com.google.gson.annotations.SerializedName

data class Currency(

    @SerializedName("name")
    val name: String,

    @SerializedName("symbol")
    val symbol: String

)