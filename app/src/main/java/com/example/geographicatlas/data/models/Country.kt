package com.example.geographicatlas.data.models

import com.google.gson.annotations.SerializedName

data class Country(

    @SerializedName("cca2")
    val countryCode: String,

    @SerializedName("flags")
    val countryFlag: Flag,

    @SerializedName("name")
    val CountryName: Name,

    @SerializedName("capital")
    val capital: List<String>,

    @SerializedName("latlng")
    val capitalInfo: List<Double>,

    @SerializedName("subregion")
    val region: String,

    @SerializedName("timezones")
    val timezone: List<String>,

    @SerializedName("population")
    val population: Int,

    @SerializedName("area")
    val area: Double,

    @SerializedName("currencies")
    val currency: Map<String, Currency>,

    @SerializedName("continents")
    val continents: List<String>

    )
