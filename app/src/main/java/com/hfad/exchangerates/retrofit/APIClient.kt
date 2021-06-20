package com.hfad.exchangerates.retrofit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.hfad.exchangerates.R
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

    fun getAllRatesForToday(date: LocalDate, callback: (List<Pair<Rate, Double>>) -> Unit) {
        val todayStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val yesterdayStr = date.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

        mService.getRatesList(todayStr,0).enqueue(object : Callback<MutableList<Rate>> {
            override fun onFailure(call: Call<MutableList<Rate>>, t: Throwable) {
                println("FUCK")
                //TODO show info dialog with close button
            }
            override fun onResponse(call: Call<MutableList<Rate>>, response: Response<MutableList<Rate>>) {
                val todayRatesList = response.body() as MutableList<Rate>
                var yesterdayRatesList: MutableList<Rate>

                    mService.getRatesList(yesterdayStr,0).enqueue(object :
                        Callback<MutableList<Rate>> {
                        override fun onFailure(call: Call<MutableList<Rate>>, t: Throwable) {
                            println("FUCK")
                            //TODO show info dialog with close button
                        }

                        override fun onResponse(call: Call<MutableList<Rate>>,
                                                response: Response<MutableList<Rate>>
                        ) {
                            yesterdayRatesList = response.body() as MutableList<Rate>
                            val ratesList = formListOfPairs(todayRatesList, yesterdayRatesList)
                            callback(ratesList)
                        }
                    })

            }
        })
    }

    fun getAllRatesForOtherDay(date: LocalDate, callback: (List<Rate>) -> Unit) {
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

        mService.getRatesList(dateStr,0).enqueue(object : Callback<MutableList<Rate>> {
            override fun onFailure(call: Call<MutableList<Rate>>, t: Throwable) {
                println("FUCK")
                //TODO show info dialog with close button
            }
            override fun onResponse(call: Call<MutableList<Rate>>, response: Response<MutableList<Rate>>) {
                val ratesList = response.body() as MutableList<Rate>
                callback(ratesList)
            }
        })
    }

    fun getRatesShortList(curId: Int, startDate: LocalDate, endDate: LocalDate,
                          callback: (List<RateShort>) -> Unit) {
        mService.getRatesShortList(curId.toString(),
            startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            endDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .enqueue(object : Callback<MutableList<RateShort>> {
                override fun onFailure(call: Call<MutableList<RateShort>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(
                    call: Call<MutableList<RateShort>>,
                    response: Response<MutableList<RateShort>>
                ) {
                    val rateShortList = response.body() as MutableList<RateShort>
                    callback(rateShortList)
                }
            })
    }

    private fun formListOfPairs(todaysList: List<Rate>, yesterdaysList: List<Rate>)
            : List<Pair<Rate, Double>> {
        val newList: MutableList<Pair<Rate, Double>> = mutableListOf()

        //TODO make a date
        todaysList.forEachIndexed {index, rate ->
            newList.add(Pair(rate, (rate.Cur_OfficialRate!! - yesterdaysList[index].Cur_OfficialRate!!)))
        }
        return newList
    }
}