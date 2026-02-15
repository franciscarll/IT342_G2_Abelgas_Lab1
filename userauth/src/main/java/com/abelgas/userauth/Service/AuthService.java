package com.abelgas.userauth.Service;

import com.abelgas.userauth.DTO.ApiResponse;
import com.abelgas.userauth.DTO.RegisterRequest;
import com.abelgas.userauth.Entity.User;
import com.abelgas.userauth.Repository.UserRepository;
import com.abelgas.userauth.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    /**
     * Register a new user
     * @param request Registration request containing user details
     * @return ApiResponse with success/error message
     */
    @Transactional
    public ApiResponse registerUser(RegisterRequest request) {
        try {
            // Manual validation
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Username is required")
                        .build();
            }
            
            if (request.getUsername().length() < 3 || request.getUsername().length() > 50) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Username must be between 3 and 50 characters")
                        .build();
            }
            
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Email is required")
                        .build();
            }
            
            if (!isValidEmail(request.getEmail())) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Invalid email format")
                        .build();
            }
            
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Password is required")
                        .build();
            }
            
            if (request.getPassword().length() < 6) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Password must be at least 6 characters")
                        .build();
            }
            
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("First name is required")
                        .build();
            }
            
            if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Last name is required")
                        .build();
            }
            
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Email already exists")
                        .build();
            }
            
            // Create new user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setIsActive(true);
            user.setRole("USER");
            
            // Save user
            User savedUser = userRepository.save(user);
            
            return ApiResponse.builder()
                    .success(true)
                    .message("User registered successfully")
                    .data(Map.of(
                            "userId", savedUser.getUserId(),
                            "email", savedUser.getEmail(),
                            "username", savedUser.getUsername()
                    ))
                    .build();
                    
        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Registration failed: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Login user with email and password
     * @param email User's email
     * @param password User's password
     * @return ApiResponse with token and user data
     */
    @Transactional
    public ApiResponse loginUser(String email, String password) {
        try {
            // Manual validation
            if (email == null || email.trim().isEmpty()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Email is required")
                        .build();
            }
            
            if (!isValidEmail(email)) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Invalid email format")
                        .build();
            }
            
            if (password == null || password.isEmpty()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Password is required")
                        .build();
            }
            
            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("User not found")
                        .build();
            }
            
            User user = userOptional.get();
            
            // Validate credentials
            if (!validateCredentials(email, password)) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Invalid credentials")
                        .build();
            }
            
            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getEmail());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getUserId());
            data.put("email", user.getEmail());
            data.put("username", user.getUsername());
            data.put("firstName", user.getFirstName());
            data.put("lastName", user.getLastName());
            data.put("role", user.getRole());
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .data(data)
                    .build();
                    
        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Login failed: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Logout user by revoking token
     * @param token JWT token to revoke
     * @return ApiResponse with success/error message
     */
    public ApiResponse logoutUser(String token) {
        try {
            // In a production app, you would invalidate the token here
            // For now, we'll just return success
            // You can implement token blacklisting with Redis or database
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Logout successful")
                    .build();
                    
        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Logout failed: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Validate user credentials
     * @param email User's email
     * @param password User's password
     * @return true if credentials are valid, false otherwise
     */
    public Boolean validateCredentials(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        return passwordEncoder.matches(password, user.getPassword());
    }
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid email format
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}