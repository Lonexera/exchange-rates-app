package com.hfad.exchangerates.`interface`

import androidx.recyclerview.widget.RecyclerView
import com.hfad.exchangerates.adapter.RatesAdapter
import com.hfad.exchangerates.model.RateShort
import com.yabu.livechart.view.LiveChart
import java.time.LocalDate

interface FragmentCommunicator {

    fun getAllRates(recycler: RecyclerView, date: LocalDate, withChanges: Boolean)
    fun closeApp()
    fun openDynamicFragment(curId: Int, curAbbreviation: String)
    fun getRatesShortList(curId: Int, startDate: LocalDate, endDate: LocalDate, livechart: LiveChart)
}