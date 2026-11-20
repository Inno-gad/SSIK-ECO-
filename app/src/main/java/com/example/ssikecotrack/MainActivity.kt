package com.example.ssikecotrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.ssikecotrack.auth.LoginFragment
import com.example.ssikecotrack.auth.RegisterFragment
import com.example.ssikecotrack.auth.SessionManager
import com.example.ssikecotrack.auth.SettingsFragment
import com.example.ssikecotrack.fragments.HomeFragment
import com.example.ssikecotrack.reports.ReportsFragment
import com.example.ssikecotrack.ui.appliances.ApplianceListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        bottomNav = findViewById(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            if (sessionManager.isLoggedIn()) {
                enterMainApp()
            } else {
                showLoginScreen()
            }
        } else {
            bottomNav.isVisible = sessionManager.isLoggedIn()
        }

        // Handle navigation selection
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_appliances -> {
                    loadFragment(ApplianceListFragment())
                    true
                }
                R.id.nav_reports -> {
                    loadFragment(ReportsFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun showLoginScreen() {
        bottomNav.isVisible = false
        val loginFragment = LoginFragment(
            onLoginSuccess = { enterMainApp() },
            onNavigateToRegister = { showRegisterScreen() }
        )
        loadFragment(loginFragment)
    }

    private fun showRegisterScreen() {
        bottomNav.isVisible = false
        val registerFragment = RegisterFragment(
            onRegisterSuccess = { enterMainApp() },
            onNavigateToLogin = { showLoginScreen() }
        )
        loadFragment(registerFragment)
    }

    private fun enterMainApp() {
        bottomNav.isVisible = true
        loadFragment(HomeFragment())
        bottomNav.selectedItemId = R.id.nav_home
    }

    fun logoutUser() {
        sessionManager.logout()
        showLoginScreen()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}