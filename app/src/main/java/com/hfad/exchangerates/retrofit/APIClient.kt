package com.hfad.exchangerates.retrofit

import android.os.Build
import androidx.annotation.RequiresApi
import com.hfad.exchangerates.`interface`.RetrofitServices
import com.hfad.exchangerates.common.Common
import com.hfad.exchangerates.model.Rate
import com.hfad.exchangerates.model.RateShort
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class APIClient {

    private val mService: RetrofitServices = Common.retrofitService

    fun getAllRatesForToday(date: LocalDate) : List<Pair<Rate, Double>> {
        val todayStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val yesterdayStr = date.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

        val todayRateResponse = mService.getRatesList(todayStr,0).execute()
        val todayRatesList = todayRateResponse.body() as MutableList<Rate>

        val yesterdayRateResponse = mService.getRatesList(yesterdayStr,0).execute()
        val yesterdayRatesList = yesterdayRateResponse.body() as MutableList<Rate>
        return formListOfPairs(todayRatesList, yesterdayRatesList)
    }

    fun getAllRatesForOtherDay(date: LocalDate) : List<Rate> {
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

        val response = mService.getRatesList(dateStr,0).execute()
        return response.body() as MutableList<Rate>
    }

    fun getRatesShortList(curId: Int, startDate: LocalDate, endDate: LocalDate) : List<RateShort> {
        val response = mService.getRatesShortList(curId.toString(),
            startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            endDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .execute()

        return  response.body() as MutableList<RateShort>
    }

    private fun formListOfPairs(todaysList: List<Rate>, yesterdaysList: List<Rate>)
            : List<Pair<Rate, Double>> {
        val newList: MutableList<Pair<Rate, Double>> = mutableListOf()

        todaysList.forEachIndexed {index, rate ->
            newList.add(Pair(rate, (rate.Cur_OfficialRate!! - yesterdaysList[index].Cur_OfficialRate!!)))
        }
        return newList
    }
}