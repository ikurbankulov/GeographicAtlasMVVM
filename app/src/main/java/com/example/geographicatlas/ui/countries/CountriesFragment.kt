package com.example.geographicatlas.ui.countries

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.geographicatlas.R
import com.example.geographicatlas.data.models.Country
import com.example.geographicatlas.databinding.FragmentCountriesBinding
import com.example.geographicatlas.ui.countries.rv_adapters.CountriesAdapter
import com.example.geographicatlas.ui.country_detail.CountryDetailFragment

class CountriesFragment : Fragment(R.layout.fragment_countries) {

    private lateinit var adapter: CountriesAdapter
    private lateinit var viewModel: CountriesViewModel
    private var binding: FragmentCountriesBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCountriesBinding.bind(view)
        viewModel = ViewModelProvider(this)[CountriesViewModel::class.java]
        setupRecyclerView()
        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            adapter.countryList = countries
        }
        viewModel.loadCountries()

        adapter.setOnItemClickListener { country ->
            adapter.toggleVisibility(country)
        }

        Log.d("TEST", "onViewCreated: ")

    }

    private fun setupRecyclerView() {
        adapter = CountriesAdapter(
            onDetailClicked = { handleOnDetailClicked(it) }
        )
        binding?.apply {
            recyclerView.adapter = adapter
        }
    }

    private fun handleOnDetailClicked(country: Country) {
        Log.d("TEST", "$country: ")
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.container, CountryDetailFragment.newInstance(country.countryCode))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}
