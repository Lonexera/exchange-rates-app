package com.hfad.exchangerates.adapter

import android.content.Context

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.exchangerates.databinding.ExchangeRatesVhLayoutBinding
import com.hfad.exchangerates.model.CurRate

class RatesAdapter(private val context: Context, var ratesList: MutableList<CurRate>)
    : RecyclerView.Adapter<RatesAdapter.RatesViewHolder>() {

    inner class RatesViewHolder(val binding: ExchangeRatesVhLayoutBinding)
        : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        val binding = ExchangeRatesVhLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return RatesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        val listItem = ratesList[position]

        with(holder) {
            with(binding) {
                currencyName.text = listItem.Cur_Abbreviation
                currencyFullRuName.text = listItem.Cur_Name
                currencyPrice.text = listItem.Cur_OfficialRate.toString()
            }
        }
    }

    override fun getItemCount(): Int = ratesList.size

}