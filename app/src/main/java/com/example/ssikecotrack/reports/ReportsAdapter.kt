package com.example.ssikecotrack.reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ssikecotrack.R

class ReportsAdapter : ListAdapter<EnergyReport, ReportsAdapter.VH>(Diff) {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val usage: TextView = view.findViewById(R.id.tvUsage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.name.text = item.applianceName
        holder.usage.text = "%.1f kWh • KES %.0f".format(item.kwhUsed, item.cost)
    }

    object Diff : DiffUtil.ItemCallback<EnergyReport>() {
        override fun areItemsTheSame(a: EnergyReport, b: EnergyReport) =
            a.applianceName == b.applianceName

        override fun areContentsTheSame(a: EnergyReport, b: EnergyReport) = a == b
    }
}