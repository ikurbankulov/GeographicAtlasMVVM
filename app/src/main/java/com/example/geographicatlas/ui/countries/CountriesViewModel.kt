package com.example.geographicatlas.ui.countries

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geographicatlas.data.models.Country
import com.example.geographicatlas.data.source.ApiFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class CountriesViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _countries = MutableLiveData<Map<String, List<Country>>>()
    val countries: LiveData<Map<String, List<Country>>> = _countries


    fun loadCountries() {
        val disposable = ApiFactory.apiService.loadCountries()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ countries ->
                val groupedCountries = groupCountriesByContinent(countries)
                _countries.value = groupedCountries
            }, { error ->
                Log.d("TEST", "ERROR: ${error.toString()}")
            })
        compositeDisposable.add(disposable)
    }

    private fun groupCountriesByContinent(countries: List<Country>): Map<String, List<Country>> {
        val groupedCountries = mutableMapOf<String, MutableList<Country>>()
        for (country in countries) {
            val continents = country.continents
            for (continent in continents) {
                if (!continent.isNullOrEmpty()) {
                    if (!groupedCountries.containsKey(continent)) {
                        groupedCountries[continent] = mutableListOf()
                    }
                    groupedCountries[continent]?.add(country)
                }
            }
        }
        return groupedCountries
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
