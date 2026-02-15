package com.abelgas.userauth.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
}