package com.abelgas.mobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abelgas.mobile.api.RetrofitClient
import com.abelgas.mobile.databinding.ActivityLoginBinding
import com.abelgas.mobile.models.LoginRequest
import com.abelgas.mobile.utils.TokenManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // Check if already logged in
        if (tokenManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.signInButton.setOnClickListener {
            handleLogin()
        }

        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Invalid email format")
            return
        }

        if (password.isEmpty()) {
            showError("Password is required")
            return
        }

        val request = LoginRequest(email, password)
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val loginData = response.body()?.data
                    if (loginData != null) {
                        tokenManager.saveToken(loginData.token, loginData)
                        navigateToDashboard()
                    }
                } else {
                    showError(response.body()?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        binding.signInButton.isEnabled = !loading
        binding.signInButton.text = if (loading) "Signing In..." else "Sign In"
    }
}
