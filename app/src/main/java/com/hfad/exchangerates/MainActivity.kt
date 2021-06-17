package com.hfad.exchangerates

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.hfad.exchangerates.`interface`.FragmentCommunicator
import com.hfad.exchangerates.`interface`.RetrofitServices
import com.hfad.exchangerates.adapter.RatesAdapter
import com.hfad.exchangerates.common.Common
import com.hfad.exchangerates.databinding.ActivityMainBinding
import com.hfad.exchangerates.model.CurRate
import dmax.dialog.SpotsDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    override fun getAllRates(recycler: RecyclerView, date: String) {
        showProgressDialog()

        mService.getRatesList(date,0).enqueue(object : Callback<MutableList<CurRate>> {
            override fun onFailure(call: Call<MutableList<CurRate>>, t: Throwable) {
                //TODO show info dialog with close button
                println("FUCK")
            }
            override fun onResponse(call: Call<MutableList<CurRate>>, response: Response<MutableList<CurRate>>) {
                val adapter = RatesAdapter(baseContext, response.body() as MutableList<CurRate>)
                recycler.adapter = adapter
                adapter.notifyDataSetChanged()
                println("OK")
                dismissProgressDialog()
            }
        })
    }

    override fun closeApp() {
        finish()
    }

}