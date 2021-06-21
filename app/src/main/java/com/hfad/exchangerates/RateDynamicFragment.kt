package com.hfad.exchangerates

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.hfad.exchangerates.`interface`.FragmentCommunicator
import com.hfad.exchangerates.databinding.FragmentRateDynamicBinding
import com.hfad.exchangerates.model.RateShort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.time.LocalDate

class RateDynamicFragment : Fragment() {

    private var _binding: FragmentRateDynamicBinding? = null
    private val binding get() = _binding!!
    private lateinit var communicator: FragmentCommunicator
    private var curId: Int = 0

    companion object {

        private const val CUR_ID = "CUR_ID"
        private const val CUR_ABBREVIATION = "CUR_ABBREVIATION"

        @JvmStatic
        fun newInstance(curId: Int, curAbbreviation: String) : RateDynamicFragment {
            val fragment = RateDynamicFragment()
            val args = Bundle()
            args.putInt(CUR_ID, curId)
            args.putString(CUR_ABBREVIATION, curAbbreviation)
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRateDynamicBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarRateDynamic.title = arguments?.getString(CUR_ABBREVIATION) ?: ""

        curId = arguments?.getInt(CUR_ID, 0)!!

        communicator = activity as FragmentCommunicator

        binding.toolbarRateDynamic.setNavigationOnClickListener { onBackPressed() }

        setChart()
    }

    private fun setChart() {
        showProgressBar()
        val today = LocalDate.now()
        val monthAgo = LocalDate.of(today.year, today.month - 1, today.dayOfMonth )

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val rateShortList = communicator.getRatesShortList(curId, monthAgo, today)
                withContext(Dispatchers.Main){
                    createChart(formatRatesListToEntries(rateShortList))

                    binding.chart.animateX(1000)
                    hideProgressBar()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showAlert()
                }
            }}
    }

    private fun showProgressBar() {
        with(binding) {
            chart.visibility = View.GONE
            progressBarDynamicFrg.visibility = View.VISIBLE
        }
    }
    private fun hideProgressBar() {
        with(binding) {
            progressBarDynamicFrg.visibility = View.GONE
            chart.visibility = View.VISIBLE
        }
    }

    private fun createChart(entries: List<Entry>) {
        with(binding) {
            val dataset = LineDataSet(entries, "")
            dataset.color = R.color.black
            dataset.valueTextColor = R.color.design_default_color_primary_dark
            dataset.setDrawCircles(false)

            val lineData = LineData(dataset)
            lineData.setDrawValues(false)
            chart.data = lineData

            chart.legend.form = Legend.LegendForm.NONE

            chart.setBackgroundColor(Color.WHITE)
            chart.description.isEnabled = false
            //chart.setDrawGridBackground(false)

        }
    }

    private fun formatRatesListToEntries(ratesShortList: List<RateShort>) : List<Entry> {
           val entries: MutableList<Entry> = mutableListOf()

           ratesShortList.forEachIndexed { index, rate ->
               entries.add(Entry(index.toFloat(), rate.Cur_OfficialRate!!.toFloat()))
           }

           return entries
       }

    private fun onBackPressed() {
        activity?.supportFragmentManager?.popBackStack()
    }


    private fun showAlert() {
        val builderAlert = AlertDialog.Builder(requireActivity())
        builderAlert.setTitle("ERROR")
        builderAlert.setMessage("Cannot load the data!")
        builderAlert.setPositiveButton("Try again") { _: DialogInterface, _: Int ->
            setChart()
        }
        builderAlert.setNegativeButton("Close app") { _: DialogInterface, _: Int ->
            communicator.closeApp()
        }
        builderAlert.show()
    }

}