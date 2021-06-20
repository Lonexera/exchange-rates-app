package com.hfad.exchangerates

import android.app.DatePickerDialog
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExchangeRatesFragment : Fragment() {

    private var _binding: FragmentExchangeRatesBinding? = null
    private val binding get() = _binding!!

    private lateinit var communicator: FragmentCommunicator
    private val dateFormatterTV = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private val today = LocalDate.now()

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
        showProgressBar()
        if (date.isAfter(today)) {
            Toast.makeText(activity, "We can't predict rates!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (date.isBefore(today)) {
            enableToolBarNavigationButton()
            val adapter = RatesAdapterOtherDay( requireActivity(),
                communicator.getAllRatesForOtherDay(date))
            binding.recycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (date == today) {
            disableToolBarNavigationButton()
            val adapter = RatesAdapterToday(requireActivity(),
                communicator.getAllRatesForToday(date))
            binding.recycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        binding.toolbar.title = date.format(dateFormatterTV)
        hideProgressBar()
    }

    private fun showProgressBar() {
        with(binding) {
            recycler.visibility = View.GONE
            progressBarRecycler.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        with(binding) {
            progressBarRecycler.visibility = View.GONE
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
        } else communicator.closeApp()
    }


}