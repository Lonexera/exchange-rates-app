package com.hfad.exchangerates.`interface`

import androidx.recyclerview.widget.RecyclerView
import com.hfad.exchangerates.adapter.RatesAdapter

interface FragmentCommunicator {

    fun getAllRates(recycler: RecyclerView, date: String)
    fun closeApp()
}