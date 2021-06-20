package com.hfad.exchangerates

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.hfad.exchangerates.`interface`.FragmentCommunicator
import com.hfad.exchangerates.databinding.FragmentRateDynamicBinding
import java.time.LocalDate

class RateDynamicFragment : Fragment() {

    private var _binding: FragmentRateDynamicBinding? = null
    private val binding get() = _binding!!
    private lateinit var communicator: FragmentCommunicator

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRateDynamicBinding.inflate(inflater, container, false)

        binding.toolbarRateDynamic.title = arguments?.getString(CUR_ABBREVIATION) ?: ""

        val curId = arguments?.getInt(CUR_ID, 0)

        communicator = activity as FragmentCommunicator
        val today = LocalDate.now()
        val monthAgo = LocalDate.of(today.year, today.month - 1, today.dayOfMonth )
        communicator.getRatesShortList(curId!!, monthAgo, today, binding.chart)

        with(binding) {
            chart.setBackgroundColor(Color.WHITE)
            chart.description.isEnabled = false
            chart.setDrawGridBackground(false)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }



}