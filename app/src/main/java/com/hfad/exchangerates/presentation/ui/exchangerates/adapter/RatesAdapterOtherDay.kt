package com.hfad.exchangerates.presentation.ui.exchangerates.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.exchangerates.databinding.ExchangeRatesVhLayoutBinding
import com.hfad.exchangerates.domain.model.Rate

class RatesAdapterOtherDay(var rates: List<Rate>)
    : RecyclerView.Adapter<RatesAdapterOtherDay.RatesOtherDayViewHolder>() {

    inner class RatesOtherDayViewHolder(val binding: ExchangeRatesVhLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
            init {
                with(binding) {
                    priceChange.visibility = View.GONE
                    upDownPriceIv.visibility = View.GONE
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        : RatesOtherDayViewHolder {
        val binding = ExchangeRatesVhLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return RatesOtherDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatesAdapterOtherDay.RatesOtherDayViewHolder, position: Int) {
        val rate = rates[position]
        with(holder) {
            with(binding) {
                currencyName.text = rate.Cur_Abbreviation
                currencyFullRuName.text = rate.Cur_Name
                currencyPrice.text = rate.Cur_OfficialRate.toString()
            }
        }

    }

    override fun getItemCount(): Int = rates.size
}
