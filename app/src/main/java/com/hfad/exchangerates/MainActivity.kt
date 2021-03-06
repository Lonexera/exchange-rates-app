package com.hfad.exchangerates

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hfad.exchangerates.`interface`.FragmentCommunicator
import com.hfad.exchangerates.databinding.ActivityMainBinding
import com.hfad.exchangerates.model.Rate
import com.hfad.exchangerates.model.RateShort
import com.hfad.exchangerates.retrofit.APIClient
import java.time.LocalDate


class MainActivity : AppCompatActivity(), FragmentCommunicator {

    private lateinit var binding: ActivityMainBinding
    private lateinit var apiClient: APIClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        apiClient = APIClient()

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

    override suspend fun getAllRatesForToday(date: LocalDate): List<Pair<Rate, Double>> {
        return apiClient.getAllRatesForToday(date)
    }

    override suspend fun getAllRatesForOtherDay(date: LocalDate): List<Rate> {
        //var otherDayRateList = mutableListOf<Rate>()
        return apiClient.getAllRatesForOtherDay(date).toMutableList()
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

    override suspend fun getRatesShortList(
        curId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<RateShort> {
        return apiClient.getRatesShortList(curId, startDate, endDate)
    }


}