package com.hfad.exchangerates.presentation.ui.`interface`

import com.hfad.exchangerates.domain.model.Rate
import com.hfad.exchangerates.domain.model.RateShort
import java.time.LocalDate

interface FragmentCommunicator {
    suspend fun getAllRatesForToday(date: LocalDate) : List<Pair<Rate, Double>>
    suspend fun getAllRatesForOtherDay(date: LocalDate) : List<Rate>
    fun closeApp()
    fun openDynamicFragment(curId: Int, curAbbreviation: String)
    suspend fun getRatesShortList(curId: Int, startDate: LocalDate, endDate: LocalDate) : List<RateShort>
}
