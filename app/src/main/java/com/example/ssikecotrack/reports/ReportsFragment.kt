package com.example.ssikecotrack.reports

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ssikecotrack.R

class ReportsFragment : Fragment(R.layout.fragment_reports) {

    private val viewModel: ReportsViewModel by viewModels()
    private val adapter = ReportsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val summary = view.findViewById<TextView>(R.id.tvSummary)
        val message = view.findViewById<TextView>(R.id.tvMessage)
        val retry = view.findViewById<Button>(R.id.btnRetry)
        val list = view.findViewById<RecyclerView>(R.id.rvReports)

        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter
        retry.setOnClickListener { viewModel.load() }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            progress.isVisible = state is ReportsState.Loading
            list.isVisible = state is ReportsState.Success
            summary.isVisible = state is ReportsState.Success
            message.isVisible = state is ReportsState.Empty || state is ReportsState.Error
            retry.isVisible = state is ReportsState.Error

            when (state) {
                is ReportsState.Success -> {
                    adapter.submitList(state.reports)
                    summary.text = "Total: %.1f kWh • KES %.0f".format(state.totalKwh, state.totalCost)
                }
                is ReportsState.Empty -> message.text = "No energy reports yet"
                is ReportsState.Error -> message.text = state.message
                else -> {}
            }
        }
    }
}