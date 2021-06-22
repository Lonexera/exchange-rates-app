package com.hfad.exchangerates.adapter

import android.content.Context
import android.graphics.Color

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.exchangerates.R
import com.hfad.exchangerates.`interface`.FragmentCommunicator
import com.hfad.exchangerates.databinding.ExchangeRatesVhLayoutBinding
import com.hfad.exchangerates.model.Rate

class RatesAdapterToday(private val context: Context, var ratesMap: List<Pair<Rate, Double>>) :
    RecyclerView.Adapter<RatesAdapterToday.RatesViewHolder>() {

    private val communicator: FragmentCommunicator = context as FragmentCommunicator

    inner class RatesViewHolder(val binding: ExchangeRatesVhLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        val binding = ExchangeRatesVhLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return RatesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        val rate = ratesMap[position].first
        with(holder) {
            with(binding) {
                currencyName.text = rate.Cur_Abbreviation
                currencyFullRuName.text = rate.Cur_Name
                currencyPrice.text = rate.Cur_OfficialRate.toString()

                var rateChange = ratesMap[position].second
                if (rateChange < 0) {
                    rateChange *= -1
                    upDownPriceIv.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                    priceChange.setTextColor(Color.GREEN)
                } else if (rateChange > 0) {
                    upDownPriceIv.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
                    priceChange.setTextColor(Color.RED)
                }
                if (rateChange == 0.0) {
                    upDownPriceIv.setImageResource(R.drawable.pause_rotate)
                    priceChange.setTextColor(Color.GRAY)
                }
                priceChange.text = rateChange.format(3)


                holder.binding.cardView.setOnClickListener {
                    communicator.openDynamicFragment(
                        rate.Cur_ID!!,
                        holder.binding.currencyName.text.toString()
                    )
                }

            }
        }

    }

    override fun getItemCount(): Int = ratesMap.size

}

fun Double.format(digits: Int) = "%.${digits}f".format(this)