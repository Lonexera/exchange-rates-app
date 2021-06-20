package com.hfad.exchangerates.`interface`

import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.hfad.exchangerates.model.Rate
import com.hfad.exchangerates.model.RateShort
import java.time.LocalDate

interface FragmentCommunicator {

    fun getAllRatesForToday(date: LocalDate) : List<Pair<Rate, Double>>
    fun getAllRatesForOtherDay(date: LocalDate) : List<Rate>
    fun closeApp()
    fun openDynamicFragment(curId: Int, curAbbreviation: String)
    fun getRatesShortList(curId: Int, startDate: LocalDate, endDate: LocalDate) : List<RateShort>
}