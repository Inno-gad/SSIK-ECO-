package com.example.ssikecotrack.ui.appliances

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ssikecotrack.R
import com.example.ssikecotrack.data.ApplianceEntity
import com.google.android.material.button.MaterialButton

class ApplianceAdapter(
    private val onEditClick: (ApplianceEntity) -> Unit,
    private val onDeleteClick: (ApplianceEntity) -> Unit
) : ListAdapter<ApplianceEntity, ApplianceAdapter.ApplianceViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appliance, parent, false)
        return ApplianceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplianceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ApplianceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvApplianceName)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantityBadge)
        private val tvPowerDetails: TextView = itemView.findViewById(R.id.tvPowerDetails)
        private val tvLocationCategory: TextView = itemView.findViewById(R.id.tvLocationCategory)
        private val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)

        fun bind(appliance: ApplianceEntity) {
            tvName.text = appliance.name
            tvQuantity.text = "Qty: ${appliance.quantity}"
            tvPowerDetails.text = "${appliance.wattage} W  •  ${appliance.hoursPerDay} hrs/day"

            val roomText = appliance.room?.takeIf { it.isNotBlank() } ?: "Unassigned"
            val categoryText = appliance.category?.takeIf { it.isNotBlank() } ?: "General"
            tvLocationCategory.text = "Room: $roomText  •  Category: $categoryText"

            btnEdit.setOnClickListener { onEditClick(appliance) }
            btnDelete.setOnClickListener { onDeleteClick(appliance) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ApplianceEntity>() {
        override fun areItemsTheSame(oldItem: ApplianceEntity, newItem: ApplianceEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ApplianceEntity, newItem: ApplianceEntity): Boolean {
            return oldItem == newItem
        }
    }
}