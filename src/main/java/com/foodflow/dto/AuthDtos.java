package com.foodflow.dto;

import com.foodflow.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDtos {

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String name;

        @NotBlank @Email
        private String email;

        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        private String phoneNumber;

        @NotNull
        private Role role; // CUSTOMER, RESTAURANT_OWNER, DELIVERY_AGENT
        // Note: ADMIN accounts should be created manually/seeded, not via public registration
    }

    @Data
    public static class LoginRequest {
        @NotBlank @Email
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String email;
        private String role;
        private Long userId;

        public AuthResponse(String token, String email, String role, Long userId) {
            this.token = token;
            this.email = email;
            this.role = role;
            this.userId = userId;
        }
    }
}
