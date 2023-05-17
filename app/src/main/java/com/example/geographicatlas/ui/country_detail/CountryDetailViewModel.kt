package com.example.geographicatlas.ui.country_detail

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geographicatlas.data.models.Country
import com.example.geographicatlas.data.source.ApiFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class CountryDetailViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _countryDetails = MutableLiveData<Country>()
    val countryDetails: LiveData<Country> get() = _countryDetails

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error



    fun loadCountryDetails(countryCode: String) {
        val disposable = ApiFactory.apiService.loadCountryDetails(countryCode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ countryDetails ->
                _countryDetails.value = countryDetails[0]
            }, { error ->
                _error.value = error.toString()
            })
        compositeDisposable.add(disposable)
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
