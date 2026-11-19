package com.example.ssikecotrack.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ssikecotrack.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterFragment(
    private val onRegisterSuccess: () -> Unit = {},
    private val onNavigateToLogin: () -> Unit = {}
) : Fragment(R.layout.fragment_register) {

    private val authRepository: AuthRepository = InMemoryAuthRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val etName = view.findViewById<TextInputEditText>(R.id.etRegisterName)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etRegisterEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etRegisterPassword)
        val etConfirm = view.findViewById<TextInputEditText>(R.id.etRegisterConfirmPassword)
        val tvError = view.findViewById<TextView>(R.id.tvRegisterError)
        val btnRegister = view.findViewById<MaterialButton>(R.id.btnRegister)
        val tvGoToLogin = view.findViewById<TextView>(R.id.tvGoToLogin)

        btnRegister.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()
            val confirm = etConfirm.text?.toString().orEmpty()

            when {
                name.length < 2 -> {
                    tvError.text = "Please enter your full name."
                    tvError.isVisible = true
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    tvError.text = "Please enter a valid email address."
                    tvError.isVisible = true
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    tvError.text = "Password must be at least 6 characters."
                    tvError.isVisible = true
                    return@setOnClickListener
                }
                password != confirm -> {
                    tvError.text = "Passwords do not match."
                    tvError.isVisible = true
                    return@setOnClickListener
                }
            }

            tvError.isVisible = false
            btnRegister.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                val result = authRepository.registerUser(name, email, password)
                btnRegister.isEnabled = true

                result.onSuccess { user ->
                    sessionManager.saveUserSession(user)
                    onRegisterSuccess()
                }.onFailure { error ->
                    tvError.text = error.message ?: "Registration failed."
                    tvError.isVisible = true
                }
            }
        }

        tvGoToLogin.setOnClickListener {
            onNavigateToLogin()
        }
    }
}