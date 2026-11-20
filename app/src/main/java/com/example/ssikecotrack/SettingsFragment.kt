package com.example.ssikecotrack.auth

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ssikecotrack.MainActivity
import com.example.ssikecotrack.R
import com.google.android.material.button.MaterialButton

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val tvName = view.findViewById<TextView>(R.id.tvSettingsUserName)
        val tvEmail = view.findViewById<TextView>(R.id.tvSettingsUserEmail)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)

        tvName.text = sessionManager.getUserName()
        tvEmail.text = sessionManager.getUserEmail()

        btnLogout.setOnClickListener {
            (activity as? MainActivity)?.logoutUser()
        }
    }
}