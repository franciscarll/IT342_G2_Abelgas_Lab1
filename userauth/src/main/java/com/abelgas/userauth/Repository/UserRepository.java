package com.abelgas.userauth.Repository;

import com.abelgas.userauth.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     * @param email User's email
     * @return User if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if email already exists in database
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    Boolean existsByEmail(String email);
}