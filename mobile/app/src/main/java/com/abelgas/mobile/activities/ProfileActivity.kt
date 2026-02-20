// File: app/src/main/java/com/abelgas/mobile/activities/ProfileActivity.kt

package com.abelgas.mobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abelgas.mobile.api.RetrofitClient
import com.abelgas.mobile.databinding.ActivityProfileBinding
import com.abelgas.mobile.models.User
import com.abelgas.mobile.utils.TokenManager
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        if (!tokenManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        loadProfile()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Back to Dashboard Button
        binding.dashboardButton.setOnClickListener {
            finish() // Close ProfileActivity and return to DashboardActivity
        }
    }

    private fun loadProfile() {
        val userData = tokenManager.getUserData()
        if (userData != null) {
            displayProfile(userData.username, userData.email, userData.firstName, userData.lastName, userData.role)
        } else {
            fetchProfileFromApi()
        }
    }

    private fun fetchProfileFromApi() {
        binding.loadingIndicator.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val token = tokenManager.getToken() ?: return@launch
                val response = RetrofitClient.apiService.getProfile("Bearer $token")

                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.data
                    if (user != null) {
                        displayProfile(user.username, user.email, user.firstName, user.lastName, user.role)
                    }
                }
            } catch (e: Exception) {
                // Fallback to cached data
            } finally {
                binding.loadingIndicator.visibility = View.GONE
            }
        }
    }

    private fun displayProfile(username: String, email: String, firstName: String, lastName: String, role: String) {
        binding.profileAvatar.text = firstName.firstOrNull()?.toString() ?: "U"
        binding.profileName.text = "$firstName $lastName"
        binding.usernameValue.text = username
        binding.emailValue.text = email
        binding.firstNameValue.text = firstName
        binding.lastNameValue.text = lastName
        binding.roleValue.text = role
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}