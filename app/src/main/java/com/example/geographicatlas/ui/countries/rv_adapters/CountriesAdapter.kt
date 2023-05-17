package com.example.geographicatlas.ui.countries.rv_adapters

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.geographicatlas.R
import com.example.geographicatlas.data.models.Country
import com.example.geographicatlas.data.models.Currency
import com.example.geographicatlas.databinding.ItemContinentBinding
import com.example.geographicatlas.databinding.ItemDetailCountryBinding
import java.text.NumberFormat

class CountriesAdapter(
    private val onDetailClicked: (country: Country) -> Unit
) : RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    private var onItemClick: ((Country) -> Unit)? = null
    private val visibilityMap: MutableMap<Country, Boolean> = mutableMapOf()
    private val sortedCountriesList: MutableList<Any> = mutableListOf()

    var countryList: Map<String, List<Country>> = emptyMap()
        set(value) {
            field = value
            sortedCountriesList.clear()
            for ((continent, countries) in value) {
                sortedCountriesList.add(continent)
                sortedCountriesList.addAll(countries)
            }
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_COUNTRY -> {
                val binding = ItemDetailCountryBinding.inflate(inflater, parent, false)
                CountryViewHolder(binding)
            }

            VIEW_TYPE_CONTINENT -> {
                val binding = ItemContinentBinding.inflate(inflater, parent, false)
                ContinentViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            when (holder) {
                is CountryViewHolder -> {
                    val country = sortedCountriesList[position] as Country
                    Glide.with(holder.binding.root).load(country.countryFlag.flag)
                        .into(holder.binding.ivFlag)
                    holder.binding.tvCountryName.text = country.CountryName.name
                    holder.binding.tvCapital.text = country.capital?.joinToString() ?: ""

                    val population = formatNumber(country.population)
                    val populationText = "Population: $population"
                    val spannablePopulation = SpannableString(populationText)
                    spannablePopulation.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                holder.binding.root.context, R.color.black
                            )
                        ),
                        populationText.indexOf(':') + 2,
                        populationText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.binding.tvPopulation.text = spannablePopulation

                    val area = formatNumber(country.area.toInt())
                    val areaText = "Area: $area km²"
                    val spannableArea = SpannableString(areaText)
                    spannableArea.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                holder.binding.root.context, R.color.black
                            )
                        ),
                        areaText.indexOf(':') + 2,
                        areaText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.binding.tvArea.text = spannableArea

                    val currenciesText = "Currencies: ${formatCurrencies(country.currency)}"
                    val spannableCurrencies = SpannableString(currenciesText)
                    spannableCurrencies.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                holder.binding.root.context, R.color.black
                            )
                        ),
                        currenciesText.indexOf(':') + 2,
                        currenciesText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.binding.tvCurrencies.text = spannableCurrencies

                    val isVisible = visibilityMap[country] ?: false
                    if (isVisible) {
                        holder.binding.invisibleView.visibility = View.VISIBLE
                        holder.binding.ivArrow.setImageResource(R.drawable.collapse)
                    } else {
                        holder.binding.invisibleView.visibility = View.GONE
                        holder.binding.ivArrow.setImageResource(R.drawable.expand)
                    }
                }

                is ContinentViewHolder -> {
                    val continent = sortedCountriesList[position] as String
                    holder.binding.textViewContinent.text = continent
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return sortedCountriesList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (sortedCountriesList[position] is Country) {
            VIEW_TYPE_COUNTRY
        } else {
            VIEW_TYPE_CONTINENT
        }
    }

    abstract inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class CountryViewHolder(val binding: ItemDetailCountryBinding) :
        ViewHolder(binding.root) {
        init {
            binding.ivArrow.setOnClickListener {
                val country = sortedCountriesList[adapterPosition] as? Country
                country?.let {
                    toggleVisibility(it)
                }
            }

            binding.tvLearnMore.setOnClickListener {
                val country = sortedCountriesList[adapterPosition] as? Country
                country?.let { onDetailClicked(country) }
            }
        }
    }

    inner class ContinentViewHolder(val binding: ItemContinentBinding) : ViewHolder(binding.root)

    private fun formatNumber(number: Int): String {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 3
        numberFormat.minimumFractionDigits = 0
        numberFormat.isGroupingUsed = true

        val formattedNumber = when {
            number >= 1_000_000 -> {
                val millions = number / 1_000_000
                "${numberFormat.format(millions)} mln"
            }

            else -> "${numberFormat.format(number)}"
        }

        return formattedNumber
    }

    private fun formatCurrencies(currencies: Map<String, Currency>): String {
        val formattedCurrencies = StringBuilder()

        for ((currencyCode, currency) in currencies) {
            val currencyName = currency.name
            val currencySymbol = currency.symbol

            val formattedCurrency = "$currencyName ($currencySymbol) ($currencyCode)"
            formattedCurrencies.append(formattedCurrency).append(" ")
        }

        return formattedCurrencies.toString().trim()
    }

    fun setOnItemClickListener(listener: (Country) -> Unit) {
        onItemClick = listener
    }

    fun toggleVisibility(country: Country) {
        val isVisible = visibilityMap[country] ?: false
        visibilityMap[country] = !isVisible
        notifyItemChanged(sortedCountriesList.indexOf(country))
    }

    companion object {
        private const val VIEW_TYPE_COUNTRY = 0
        private const val VIEW_TYPE_CONTINENT = 1
    }
}
