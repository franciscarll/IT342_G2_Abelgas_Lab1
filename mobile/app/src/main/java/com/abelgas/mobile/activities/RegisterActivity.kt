package com.abelgas.mobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abelgas.mobile.R
import com.abelgas.mobile.api.RetrofitClient
import com.abelgas.mobile.databinding.ActivityRegisterBinding
import com.abelgas.mobile.models.RegisterRequest
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.signUpButton.setOnClickListener {
            handleRegister()
        }

        binding.loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun handleRegister() {
        // Get input values
        val firstName = binding.firstNameInput.text.toString().trim()
        val lastName = binding.lastNameInput.text.toString().trim()
        val username = binding.usernameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        // Validation
        if (firstName.isEmpty()) {
            showError("First name is required")
            return
        }

        if (lastName.isEmpty()) {
            showError("Last name is required")
            return
        }

        if (username.isEmpty() || username.length < 3) {
            showError("Username must be at least 3 characters")
            return
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Invalid email format")
            return
        }

        if (password.isEmpty() || password.length < 6) {
            showError("Password must be at least 6 characters")
            return
        }

        // Create request
        val request = RegisterRequest(
            username = username,
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName
        )

        // Show loading
        setLoading(true)

        // Call API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.register(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration successful! Please login.",
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val message = response.body()?.message ?: "Registration failed"
                    showError(message)
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        binding.signUpButton.isEnabled = !loading
        binding.signUpButton.text = if (loading) "Creating Account..." else "Sign Up"
    }
}