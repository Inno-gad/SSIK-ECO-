package com.example.ssikecotrack.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ssikecotrack.R
import com.example.ssikecotrack.data.AppDatabase
import com.example.ssikecotrack.data.RoomAuthRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginFragment(
    private val onLoginSuccess: () -> Unit = {},
    private val onNavigateToRegister: () -> Unit = {}
) : Fragment(R.layout.fragment_login) {

    private val authRepository: AuthRepository by lazy {
        RoomAuthRepository(AppDatabase.getInstance(requireContext()).userDao())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val etEmail = view.findViewById<TextInputEditText>(R.id.etLoginEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etLoginPassword)
        val tvError = view.findViewById<TextView>(R.id.tvLoginError)
        val btnLogin = view.findViewById<MaterialButton>(R.id.btnLogin)
        val tvGoToRegister = view.findViewById<TextView>(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tvError.text = "Please enter a valid email address."
                tvError.isVisible = true
                return@setOnClickListener
            }

            if (password.length < 6) {
                tvError.text = "Password must be at least 6 characters."
                tvError.isVisible = true
                return@setOnClickListener
            }

            tvError.isVisible = false
            btnLogin.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                val result = authRepository.loginUser(email, password)
                btnLogin.isEnabled = true

                result.onSuccess { user ->
                    sessionManager.saveUserSession(user)
                    onLoginSuccess()
                }.onFailure { error ->
                    tvError.text = error.message ?: "Login failed."
                    tvError.isVisible = true
                }
            }
        }

        tvGoToRegister.setOnClickListener {
            onNavigateToRegister()
        }
    }
}