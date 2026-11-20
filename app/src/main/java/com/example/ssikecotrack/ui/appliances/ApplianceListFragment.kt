package com.example.ssikecotrack.ui.appliances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ssikecotrack.R
import com.example.ssikecotrack.data.AppDatabase
import com.example.ssikecotrack.data.ApplianceEntity
import com.example.ssikecotrack.data.ApplianceRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class ApplianceListFragment : Fragment() {

    private lateinit var repository: ApplianceRepository
    private lateinit var adapter: ApplianceAdapter
    private lateinit var rvAppliances: RecyclerView
    private lateinit var tvEmptyState: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_appliance_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = ApplianceRepository(
            AppDatabase.getInstance(requireContext()).applianceDao()
        )

        rvAppliances = view.findViewById(R.id.rvAppliances)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
        val fabAddAppliance: ExtendedFloatingActionButton = view.findViewById(R.id.fabAddAppliance)

        adapter = ApplianceAdapter(
            onEditClick = { appliance ->
                AddEditApplianceDialogFragment.newInstance(appliance.id)
                    .show(childFragmentManager, "EditApplianceDialog")
            },
            onDeleteClick = { appliance ->
                confirmDelete(appliance)
            }
        )

        rvAppliances.layoutManager = LinearLayoutManager(requireContext())
        rvAppliances.adapter = adapter

        fabAddAppliance.setOnClickListener {
            AddEditApplianceDialogFragment.newInstance()
                .show(childFragmentManager, "AddApplianceDialog")
        }

        observeAppliances()
    }

    private fun observeAppliances() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.allAppliances.collect { appliances ->
                    adapter.submitList(appliances)
                    tvEmptyState.visibility = if (appliances.isEmpty()) View.VISIBLE else View.GONE
                    rvAppliances.visibility = if (appliances.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun confirmDelete(appliance: ApplianceEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Appliance")
            .setMessage("Remove ${appliance.name} from your appliance list?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.deleteAppliance(appliance)
                    Toast.makeText(
                        requireContext(),
                        "${appliance.name} deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }
}