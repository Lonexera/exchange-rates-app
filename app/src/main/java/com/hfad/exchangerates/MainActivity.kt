package com.hfad.exchangerates

import android.app.AlertDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.hfad.exchangerates.`interface`.FragmentCommunicator
import com.hfad.exchangerates.`interface`.RetrofitServices
import com.hfad.exchangerates.adapter.RatesAdapter
import com.hfad.exchangerates.common.Common
import com.hfad.exchangerates.databinding.ActivityMainBinding
import com.hfad.exchangerates.model.Rate
import com.hfad.exchangerates.model.RateShort
import com.yabu.livechart.model.DataPoint
import com.yabu.livechart.model.Dataset
import com.yabu.livechart.view.LiveChart
import dmax.dialog.SpotsDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), FragmentCommunicator {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mService: RetrofitServices
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mService = Common.retrofitService
        showExchangeRatesFragment()
    }

    private fun showExchangeRatesFragment() {
        val fragment = ExchangeRatesFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        //transaction.addToBackStack(null)
        //transaction.setReorderingAllowed(true)
        transaction.commit()
    }

    private fun showProgressDialog() {
        dialog = SpotsDialog.Builder().setCancelable(true).setContext(this).build()
        dialog.show()
    }

    private fun dismissProgressDialog() {
        dialog.dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllRates(recycler: RecyclerView, date: LocalDate, withChanges: Boolean) {
        showProgressDialog()
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

        mService.getRatesList(dateStr,0).enqueue(object : Callback<MutableList<Rate>> {
            override fun onFailure(call: Call<MutableList<Rate>>, t: Throwable) {
                println("FUCK")
                //TODO show info dialog with close button
            }
            override fun onResponse(call: Call<MutableList<Rate>>, response: Response<MutableList<Rate>>) {
                val ratesList = response.body() as MutableList<Rate>
                var oldRatesList: MutableList<Rate> = mutableListOf()

                if (withChanges) {
                    val yesterdayStr = date.minusDays(1)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE)

                    mService.getRatesList(yesterdayStr,0).enqueue(object : Callback<MutableList<Rate>> {
                        override fun onFailure(call: Call<MutableList<Rate>>, t: Throwable) {
                            println("FUCK")
                            TODO("Not yet implemented")
                        }

                        override fun onResponse(call: Call<MutableList<Rate>>,
                            response: Response<MutableList<Rate>>) {
                            oldRatesList = response.body() as MutableList<Rate>
                            updateAdapter(recycler, formListOfPairs(ratesList, oldRatesList),
                                true)
                            dismissProgressDialog()
                        }
                    })
                } else {
                    updateAdapter(recycler, formListOfPairs(ratesList), false)
                    dismissProgressDialog()
                }
            }
        })
    }

    private fun formListOfPairs(todaysList: List<Rate>, yesterdaysList: List<Rate>)
        : List<Pair<Rate, Double>> {
        val newList: MutableList<Pair<Rate, Double>> = mutableListOf()

        todaysList.forEachIndexed {index, rate ->
            newList.add(Pair(rate, (rate.Cur_OfficialRate!! - yesterdaysList[index].Cur_OfficialRate!!)))
        }
        return newList
    }

    private fun formListOfPairs(datesList: List<Rate>) : List<Pair<Rate, Double>> {
        val newList: MutableList<Pair<Rate, Double>> = mutableListOf()

        datesList.forEach { rate ->
            newList.add(Pair(rate, 0.0))
        }
        return newList
    }

    private fun updateAdapter(recycler: RecyclerView, ratesList: List<Pair<Rate, Double>>,
                              withChanges: Boolean) {
        val adapter = RatesAdapter(this@MainActivity,
            ratesList, withChanges)
        recycler.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun closeApp() {
        finish()
    }

    override fun openDynamicFragment(curId: Int, curAbbreviation: String) {
        val fragment = RateDynamicFragment.newInstance(curId, curAbbreviation)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.setReorderingAllowed(true)
        transaction.commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getRatesShortList(curId: Int, startDate: LocalDate, endDate: LocalDate, livechart: LiveChart) {
        //println("$curId + ${startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)} ${endDate.format(DateTimeFormatter.BASIC_ISO_DATE)}")
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
                    val dataPoints = formatRatesListToDataPoints(rateShortList)
                    val dataset = Dataset(dataPoints.toMutableList())

                    livechart.setDataset(dataset)
                        .drawYBounds()
                        .drawBaseline()
                        .drawFill()
                        .drawDataset()
                }
            })
    }

    private fun formatRatesListToDataPoints(ratesShortList: List<RateShort>) : List<DataPoint> {
        val dataPoints: MutableList<DataPoint> = mutableListOf()

        ratesShortList.forEachIndexed { index, rate ->
            dataPoints.add(DataPoint(index.toFloat(), rate.Cur_OfficialRate!!.toFloat()))
        }

        return dataPoints
    }

}