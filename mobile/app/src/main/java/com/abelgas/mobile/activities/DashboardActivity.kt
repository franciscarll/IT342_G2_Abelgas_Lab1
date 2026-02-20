package com.abelgas.mobile.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abelgas.mobile.databinding.ActivityDashboardBinding
import com.abelgas.mobile.utils.TokenManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        if (!tokenManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        displayWelcome()
        setupNavigation()
    }

    private fun displayWelcome() {
        val userData = tokenManager.getUserData()
        val name = userData?.firstName ?: userData?.username ?: "User"
        binding.welcomeText.text = "Welcome, $name"
    }

    private fun setupNavigation() {
        // Profile Button
        binding.profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Logout Button
        binding.logoutButton.setOnClickListener {
            handleLogout()
        }
    }

    private fun handleLogout() {
        tokenManager.clearToken()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}