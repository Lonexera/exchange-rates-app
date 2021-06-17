package com.hfad.exchangerates.`interface`

import androidx.recyclerview.widget.RecyclerView
import com.hfad.exchangerates.adapter.RatesAdapter
import java.time.LocalDate

interface FragmentCommunicator {

    fun getAllRates(recycler: RecyclerView, date: LocalDate, withChanges: Boolean)
    fun closeApp()
}