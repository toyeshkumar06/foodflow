package com.foodflow.service;

import com.foodflow.dto.AuthDtos.AuthResponse;
import com.foodflow.dto.AuthDtos.LoginRequest;
import com.foodflow.dto.AuthDtos.RegisterRequest;
import com.foodflow.entity.Role;
import com.foodflow.entity.User;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.UserRepository;
import com.foodflow.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw ApiException.conflict("An account with this email already exists");
        }

        // Public registration should never let someone sign up as ADMIN
        if (request.getRole() == Role.ADMIN) {
            throw ApiException.forbidden("Admin accounts cannot be self-registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .enabled(true)
                .banned(false)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), user.getId());
    }

    public AuthResponse login(LoginRequest request) {
        // This throws AuthenticationException (handled globally) if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> ApiException.notFound("User not found"));

        if (user.isBanned()) {
            throw ApiException.forbidden("This account has been banned");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), user.getId());
    }
}
