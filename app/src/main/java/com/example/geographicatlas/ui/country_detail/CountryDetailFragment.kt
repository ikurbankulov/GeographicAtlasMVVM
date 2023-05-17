package com.example.geographicatlas.ui.country_detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.geographicatlas.R
import com.example.geographicatlas.data.models.Country
import com.example.geographicatlas.databinding.FragmentDetailBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class CountryDetailFragment : Fragment(R.layout.fragment_detail) {

    private lateinit var viewModel: CountryDetailViewModel
    private var binding: FragmentDetailBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailBinding.bind(view)
        viewModel = ViewModelProvider(this)[CountryDetailViewModel::class.java]
        loadCountryDetailInfo()

        binding?.apply {
            imageViewBack.setOnClickListener { parentFragmentManager.popBackStack() }
        }
    }

    private fun loadCountryDetailInfo() {
        val countryCode = requireArguments().getString("countryCode")
        if (countryCode != null) viewModel.loadCountryDetails(countryCode)
        viewModel.countryDetails.observe(viewLifecycleOwner) { setValues(it) }
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), R.string.some_error_occured, Toast.LENGTH_LONG).show()
        }
    }

    private fun setValues(countryDetails: Country) = binding?.apply {
        val countryNameTextView = pageTitle
        countryNameTextView.text = countryDetails.CountryName.name

        val capitalTextView = capitalName
        capitalTextView.text = countryDetails.capital.joinToString(", ")

        val coordinatesTextView = coordinatesValue
        val latitude = countryDetails.capitalInfo.get(0)
        val longitude = countryDetails.capitalInfo.get(1)
        val latitudeText = latitude.let { formatCoordinate(it) }
        val longitudeText = longitude.let { formatCoordinate(it) }
        coordinatesTextView.text = "$latitudeText, $longitudeText"

        val populationTextView = populationValue
        val population = countryDetails.population
        val populationText = population.let { formatPopulation(it) }
        populationTextView.text = populationText

        val areaTextView = areaValue
        val area = countryDetails.area
        val areaText = area.let { formatArea(it) }
        areaTextView.text = areaText

        val currencyTextView = currencyValue
        val currencyCode = countryDetails.currency.keys.firstOrNull()
        val currencyValue = countryDetails.currency.values.firstOrNull()
        val currencyText = formatCurrency(
            currencyCode ?: "",
            currencyValue?.name ?: "",
            currencyValue?.symbol ?: ""
        )
        currencyTextView.text = currencyText

        val regionTextView = regionName
        regionTextView.text = countryDetails.region

        val timeZonesTextView = timeZonesValue
        timeZonesTextView.text = countryDetails.timezone.joinToString(", ")

        val flagImageView = ivFlag
        Glide.with(root)
            .load(countryDetails.countryFlag.flag)
            .into(flagImageView)
    }

    private fun formatCoordinate(coordinate: Double): String {
        val degrees = coordinate.toInt()
        val minutes = (Math.abs(coordinate) - Math.abs(degrees)) * 60
        val formattedMinutes = String.format("%.0f", minutes)
        return "$degrees°$formattedMinutes'"
    }

    private fun formatPopulation(population: Int): String {
        return when {
            population >= 1_000_000 -> {
                val roundedPopulation = population / 1_000_000
                "$roundedPopulation mln"
            }

            else -> population.toString()
        }
    }

    private fun formatArea(area: Double): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        symbols.groupingSeparator = ' '
        val decimalFormat = DecimalFormat("#,##0", symbols)
        return "${decimalFormat.format(area)} км²"
    }

    private fun formatCurrency(
        currencyCode: String,
        currencyName: String,
        currencySymbol: String
    ): String {
        return "$currencyName ($currencySymbol) ($currencyCode)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val COUNTRY_CODE = "countryCode"
        fun newInstance(countryCode: String): CountryDetailFragment {
            val args = Bundle()
            args.putString(COUNTRY_CODE, countryCode)
            val fragment = CountryDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }
}