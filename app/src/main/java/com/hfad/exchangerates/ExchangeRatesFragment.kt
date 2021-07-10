package com.hfad.exchangerates

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.exchangerates.`interface`.FragmentCommunicator
import com.hfad.exchangerates.adapter.RatesAdapterOtherDay
import com.hfad.exchangerates.adapter.RatesAdapterToday
import com.hfad.exchangerates.databinding.FragmentExchangeRatesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExchangeRatesFragment : Fragment() {

    private var _binding: FragmentExchangeRatesBinding? = null
    private val binding get() = _binding!!

    private lateinit var communicator: FragmentCommunicator
    private val dateFormatterTV = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private val today = LocalDate.now()
    private var dateToLook = today

    companion object {

        @JvmStatic
        fun newInstance() : ExchangeRatesFragment {
            val fragment = ExchangeRatesFragment()

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExchangeRatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        communicator = activity as FragmentCommunicator

        disableToolBarNavigationButton()

        with(binding) {
            toolbar.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.action_pick_date -> {
                        val datePickerDialog = DatePickerDialog(view.context, R.style.DatePickerTheme)
                        datePickerDialog.updateDate(dateToLook.year,
                            dateToLook.minusMonths(1).monthValue, dateToLook.dayOfMonth)

                        datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                            dateToLook = LocalDate.of(year, month + 1, dayOfMonth)

                            showRatesForDate(dateToLook)
                            datePickerDialog.dismiss()
                        }
                        datePickerDialog.show()
                        true
                    }
                    else -> false
                }
            }
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }

            recycler.setHasFixedSize(true)
            recycler.layoutManager = LinearLayoutManager(activity)
            recycler.adapter = RatesAdapterToday(requireActivity(), mutableListOf())
            showRatesForDate(today)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun showRatesForDate(date: LocalDate) {
        if (date.isAfter(today)) {
            Toast.makeText(activity, "We can't predict rates!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (date.isBefore(today)) {
            showProgressBar()
            enableToolBarNavigationButton()
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val ratesList = communicator.getAllRatesForOtherDay(date)
                    withContext(Dispatchers.Main){
                        val adapter = RatesAdapterOtherDay(requireActivity(),
                            ratesList)
                        binding.recycler.adapter = adapter
                        adapter.notifyDataSetChanged()
                        hideProgressBar()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showAlert(date)
                    }
                }}
        }
        if (date == today) {
            showProgressBar()
            disableToolBarNavigationButton()
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val ratesList = communicator.getAllRatesForToday(date)
                    withContext(Dispatchers.Main){
                        val adapter = RatesAdapterToday(requireActivity(),
                            ratesList)
                        binding.recycler.adapter = adapter
                        adapter.notifyDataSetChanged()
                        hideProgressBar()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showAlert(date)
                    }
                }}
        }
        binding.toolbar.title = date.format(dateFormatterTV)

    }

    private fun showProgressBar() {
        with(binding) {
            ratesScrollView.visibility = View.GONE
            recycler.visibility = View.GONE
            progressBarRecycler.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        with(binding) {
            progressBarRecycler.visibility = View.GONE
            ratesScrollView.visibility = View.VISIBLE
            recycler.visibility = View.VISIBLE

        }
    }

    private fun disableToolBarNavigationButton() {
        binding.toolbar.navigationIcon = null
    }

    private fun enableToolBarNavigationButton() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_chevron_left_24)
    }

    private fun onBackPressed() {
        if (binding.toolbar.title != today.format(dateFormatterTV)) {
            showRatesForDate(today)
            disableToolBarNavigationButton()
            dateToLook = today
        } else communicator.closeApp()
    }

    private fun showAlert(date: LocalDate) {
        val builderAlert = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        builderAlert.setTitle("ERROR")
        builderAlert.setMessage("Cannot load the data!")
        builderAlert.setPositiveButton("Try again") { _: DialogInterface, _: Int ->
            showRatesForDate(date)
        }
        builderAlert.setNegativeButton("Close app") { _: DialogInterface, _: Int ->
            communicator.closeApp()
        }
        builderAlert.show()
    }


}