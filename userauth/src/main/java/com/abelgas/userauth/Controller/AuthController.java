package com.abelgas.userauth.Controller;

import com.abelgas.userauth.DTO.ApiResponse;
import com.abelgas.userauth.DTO.LoginRequest;
import com.abelgas.userauth.DTO.RegisterRequest;
import com.abelgas.userauth.DTO.UpdateProfileRequest;
import com.abelgas.userauth.Service.AuthService;
import com.abelgas.userauth.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    /**
     * POST /api/auth/register
     * Register a new user
     */
    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody RegisterRequest request) {
        ApiResponse response = authService.registerUser(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * POST /api/auth/login
     * Login user and return JWT token
     */
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginRequest request) {
        ApiResponse response = authService.loginUser(request.getEmail(), request.getPassword());
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * POST /api/auth/logout
     * Logout user and revoke token
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse> logoutUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        ApiResponse response = authService.logoutUser(token);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/user/me
     * Get current authenticated user profile
     */
    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        ApiResponse response = userService.fetchUserProfileDetails(token);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * GET /api/user/profile
     * Get user profile (alias for /me)
     */
    @GetMapping("/user/profile")
    public ResponseEntity<ApiResponse> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        return getCurrentUser(authHeader);
    }
    
    /**
     * PUT /api/user/profile
     * Update user profile
     */
    @PutMapping("/user/profile")
    public ResponseEntity<ApiResponse> updateUserProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateProfileRequest request) {
        
        String token = authHeader.replace("Bearer ", "");
        
        // First get current user to ensure they exist
        ApiResponse profileResponse = userService.fetchUserProfileDetails(token);
        
        if (!profileResponse.getSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(profileResponse);
        }
        
        // Update profile
        com.abelgas.userauth.Entity.User user = new com.abelgas.userauth.Entity.User();
        user.setUserId(request.getUserId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        
        ApiResponse response = userService.updateProfile(user);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * GET /api/user/dashboard
     * Load dashboard data
     */
    @GetMapping("/user/dashboard")
    public ResponseEntity<ApiResponse> getDashboard(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        ApiResponse response = userService.fetchUserProfileDetails(token);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}