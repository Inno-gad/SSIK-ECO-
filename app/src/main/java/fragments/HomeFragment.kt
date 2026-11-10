package com.example.ssikecotrack.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ssikecotrack.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private var txtTotalConsumption: TextView? = null
    private var txtEstimatedCost: TextView? = null
    private var txtApplianceCount: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_home,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtTotalConsumption = view.findViewById(R.id.txtTotalConsumption)
        txtEstimatedCost = view.findViewById(R.id.txtEstimatedCost)
        txtApplianceCount = view.findViewById(R.id.txtApplianceCount)

        val addAppliance = view.findViewById<LinearLayout>(R.id.actionAddAppliance)
        addAppliance?.setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.selectedItemId = R.id.nav_appliances
        }

        val viewReports = view.findViewById<LinearLayout>(R.id.actionViewReports)
        viewReports?.setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.selectedItemId = R.id.nav_reports
        }
    }

    // Helper functions for dynamic data updating once Sean and Kayla's data layers are connected
    fun updateDashboardSummary(totalKwh: Double, totalCostKes: Double, applianceCount: Int) {
        txtTotalConsumption?.text = String.format("%.1f", totalKwh)
        txtEstimatedCost?.text = String.format("KES %,d", totalCostKes.toInt())
        txtApplianceCount?.text = applianceCount.toString()
    }
}