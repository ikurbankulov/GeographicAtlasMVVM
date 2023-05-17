package com.example.geographicatlas.data.source

import com.example.geographicatlas.data.models.Country
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("v3.1/all")
    fun loadCountries(): Single<List<Country>>

    @GET("v3.1/alpha/{countryCode}")
    fun loadCountryDetails(@Path("countryCode") countryCode: String): Single<List<Country>>

}