package com.example.ssikecotrack.ui.appliances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.ssikecotrack.R
import com.example.ssikecotrack.data.AppDatabase
import com.example.ssikecotrack.data.ApplianceEntity
import com.example.ssikecotrack.data.ApplianceRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AddEditApplianceDialogFragment : DialogFragment() {

    private lateinit var repository: ApplianceRepository
    private var existingAppliance: ApplianceEntity? = null

    private lateinit var tvFormTitle: TextView
    private lateinit var tilName: TextInputLayout
    private lateinit var tilWattage: TextInputLayout
    private lateinit var tilHoursPerDay: TextInputLayout
    private lateinit var tilQuantity: TextInputLayout

    private lateinit var etName: TextInputEditText
    private lateinit var etWattage: TextInputEditText
    private lateinit var etHoursPerDay: TextInputEditText
    private lateinit var etQuantity: TextInputEditText
    private lateinit var etCategory: TextInputEditText
    private lateinit var etRoom: TextInputEditText

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_add_edit_appliance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = ApplianceRepository(
            AppDatabase.getInstance(requireContext()).applianceDao()
        )

        tvFormTitle = view.findViewById(R.id.tvFormTitle)
        tilName = view.findViewById(R.id.tilName)
        tilWattage = view.findViewById(R.id.tilWattage)
        tilHoursPerDay = view.findViewById(R.id.tilHoursPerDay)
        tilQuantity = view.findViewById(R.id.tilQuantity)

        etName = view.findViewById(R.id.etName)
        etWattage = view.findViewById(R.id.etWattage)
        etHoursPerDay = view.findViewById(R.id.etHoursPerDay)
        etQuantity = view.findViewById(R.id.etQuantity)
        etCategory = view.findViewById(R.id.etCategory)
        etRoom = view.findViewById(R.id.etRoom)

        val btnCancel: MaterialButton = view.findViewById(R.id.btnCancel)
        val btnSave: MaterialButton = view.findViewById(R.id.btnSave)

        val applianceId = arguments?.getInt(ARG_APPLIANCE_ID, NO_ID) ?: NO_ID
        if (applianceId != NO_ID) {
            tvFormTitle.text = "Edit Appliance"
            btnSave.text = "Update"
            loadApplianceForEdit(applianceId)
        } else {
            tvFormTitle.text = "Add Appliance"
            btnSave.text = "Save"
        }

        btnCancel.setOnClickListener { dismiss() }
        btnSave.setOnClickListener { validateAndSave() }
    }

    private fun loadApplianceForEdit(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val appliance = repository.getAppliance(id)
            if (appliance != null) {
                existingAppliance = appliance
                etName.setText(appliance.name)
                etWattage.setText(appliance.wattage.toString())
                etHoursPerDay.setText(appliance.hoursPerDay.toString())
                etQuantity.setText(appliance.quantity.toString())
                etCategory.setText(appliance.category.orEmpty())
                etRoom.setText(appliance.room.orEmpty())
            } else {
                Toast.makeText(requireContext(), "Appliance not found", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun validateAndSave() {
        tilName.error = null
        tilWattage.error = null
        tilHoursPerDay.error = null
        tilQuantity.error = null

        val name = etName.text?.toString()?.trim().orEmpty()
        val wattageText = etWattage.text?.toString()?.trim().orEmpty()
        val hoursText = etHoursPerDay.text?.toString()?.trim().orEmpty()
        val quantityText = etQuantity.text?.toString()?.trim().orEmpty()
        val category = etCategory.text?.toString()?.trim().orEmpty()
        val room = etRoom.text?.toString()?.trim().orEmpty()

        var isValid = true

        if (name.isEmpty()) {
            tilName.error = "Enter appliance name"
            isValid = false
        }

        val wattage = wattageText.toDoubleOrNull()
        if (wattage == null || wattage <= 0.0) {
            tilWattage.error = "Enter a valid wattage greater than 0"
            isValid = false
        }

        val hoursPerDay = hoursText.toDoubleOrNull()
        if (hoursPerDay == null || hoursPerDay <= 0.0 || hoursPerDay > 24.0) {
            tilHoursPerDay.error = "Enter hours between 0.1 and 24"
            isValid = false
        }

        val quantity = if (quantityText.isEmpty()) 1 else quantityText.toIntOrNull()
        if (quantity == null || quantity < 1) {
            tilQuantity.error = "Quantity must be at least 1"
            isValid = false
        }

        if (!isValid) return

        viewLifecycleOwner.lifecycleScope.launch {
            val current = existingAppliance
            if (current == null) {
                repository.addAppliance(
                    ApplianceEntity(
                        name = name,
                        wattage = wattage!!,
                        hoursPerDay = hoursPerDay!!,
                        quantity = quantity!!,
                        category = category,
                        room = room
                    )
                )
                Toast.makeText(requireContext(), "Appliance added", Toast.LENGTH_SHORT).show()
            } else {
                val updated = current.copy(
                    name = name,
                    wattage = wattage!!,
                    hoursPerDay = hoursPerDay!!,
                    quantity = quantity!!,
                    category = category,
                    room = room
                )
                repository.updateAppliance(updated)
                Toast.makeText(requireContext(), "Appliance updated", Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }
    }

    companion object {
        private const val ARG_APPLIANCE_ID = "arg_appliance_id"
        private const val NO_ID = -1

        fun newInstance(applianceId: Int? = null): AddEditApplianceDialogFragment {
            return AddEditApplianceDialogFragment().apply {
                if (applianceId != null) {
                    arguments = Bundle().apply {
                        putInt(ARG_APPLIANCE_ID, applianceId)
                    }
                }
            }
        }
    }
}