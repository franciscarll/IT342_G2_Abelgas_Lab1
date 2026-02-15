package com.abelgas.userauth.Service;

import com.abelgas.userauth.DTO.ApiResponse;
import com.abelgas.userauth.Entity.User;
import com.abelgas.userauth.Repository.UserRepository;
import com.abelgas.userauth.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    /**
     * Fetch user profile details by token
     * @param token JWT token
     * @return ApiResponse with user profile data
     */
    public ApiResponse fetchUserProfileDetails(String token) {
        try {
            // Validate token
            if (!jwtTokenProvider.validateToken(token)) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Invalid or expired token")
                        .build();
            }
            
            // Get email from token
            String email = jwtTokenProvider.getEmailFromToken(token);
            
            // Find user
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("User not found")
                        .build();
            }
            
            User user = userOptional.get();
            
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("userId", user.getUserId());
            profileData.put("username", user.getUsername());
            profileData.put("email", user.getEmail());
            profileData.put("firstName", user.getFirstName());
            profileData.put("lastName", user.getLastName());
            profileData.put("role", user.getRole());
            profileData.put("isActive", user.getIsActive());
            profileData.put("createdAt", user.getCreatedAt());
            profileData.put("lastLogin", user.getLastLogin());
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Profile fetched successfully")
                    .data(profileData)
                    .build();
                    
        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Failed to fetch profile: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Update user profile
     * @param user Updated user data
     * @return ApiResponse with success/error message
     */
    @Transactional
    public ApiResponse updateProfile(User user) {
        try {
            // Validate user ID
            if (user.getUserId() == null) {
                return ApiResponse.builder()
                        .success(false)
                        .message("User ID is required")
                        .build();
            }
            
            Optional<User> existingUserOptional = userRepository.findById(user.getUserId());
            
            if (existingUserOptional.isEmpty()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("User not found")
                        .build();
            }
            
            User existingUser = existingUserOptional.get();
            
            // Update allowed fields with validation
            if (user.getFirstName() != null) {
                if (user.getFirstName().trim().isEmpty()) {
                    return ApiResponse.builder()
                            .success(false)
                            .message("First name cannot be empty")
                            .build();
                }
                if (user.getFirstName().length() > 50) {
                    return ApiResponse.builder()
                            .success(false)
                            .message("First name must not exceed 50 characters")
                            .build();
                }
                existingUser.setFirstName(user.getFirstName());
            }
            
            if (user.getLastName() != null) {
                if (user.getLastName().trim().isEmpty()) {
                    return ApiResponse.builder()
                            .success(false)
                            .message("Last name cannot be empty")
                            .build();
                }
                if (user.getLastName().length() > 50) {
                    return ApiResponse.builder()
                            .success(false)
                            .message("Last name must not exceed 50 characters")
                            .build();
                }
                existingUser.setLastName(user.getLastName());
            }
            
            if (user.getUsername() != null) {
                if (user.getUsername().trim().isEmpty()) {
                    return ApiResponse.builder()
                            .success(false)
                            .message("Username cannot be empty")
                            .build();
                }
                if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
                    return ApiResponse.builder()
                            .success(false)
                            .message("Username must be between 3 and 50 characters")
                            .build();
                }
                existingUser.setUsername(user.getUsername());
            }
            
            User updatedUser = userRepository.save(existingUser);
            
            Map<String, Object> data = new HashMap<>();
            data.put("userId", updatedUser.getUserId());
            data.put("username", updatedUser.getUsername());
            data.put("email", updatedUser.getEmail());
            data.put("firstName", updatedUser.getFirstName());
            data.put("lastName", updatedUser.getLastName());
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Profile updated successfully")
                    .data(data)
                    .build();
                    
        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Failed to update profile: " + e.getMessage())
                    .build();
        }
    }
}