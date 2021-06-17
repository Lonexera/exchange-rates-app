package com.hfad.exchangerates

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.exchangerates.`interface`.FragmentCommunicator
import com.hfad.exchangerates.adapter.RatesAdapter
import com.hfad.exchangerates.databinding.FragmentExchangeRatesBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class ExchangeRatesFragment : Fragment() {

    companion object {

        private var _binding: FragmentExchangeRatesBinding? = null
        private val binding get() = _binding!!

        @JvmStatic
        fun newInstance() : ExchangeRatesFragment {
            val fragment = ExchangeRatesFragment()

            return fragment
        }
    }

    private lateinit var communicator: FragmentCommunicator
    private val dateFormatterTV = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExchangeRatesBinding.inflate(inflater, container, false)

        communicator = activity as FragmentCommunicator

        with(binding) {
            recycler.setHasFixedSize(true)
            recycler.layoutManager = LinearLayoutManager(activity)
            recycler.adapter = RatesAdapter(inflater.context, mutableListOf(), true)
            showRatesForDate(today)
        }

        disableToolBarNavigationButton()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.action_pick_date -> {
                        val datePickerDialog = DatePickerDialog(view.context)
                        datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                            val date = LocalDate.of(year, month + 1, dayOfMonth)
                            showRatesForDate(date)
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
            enableToolBarNavigationButton()
            communicator.getAllRates(binding.recycler, date, false)
        }
        if (date == today) {
            disableToolBarNavigationButton()
            communicator.getAllRates(binding.recycler, date, true)
        }
        binding.toolbar.title = date.format(dateFormatterTV)
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
        } else communicator.closeApp()
    }


}