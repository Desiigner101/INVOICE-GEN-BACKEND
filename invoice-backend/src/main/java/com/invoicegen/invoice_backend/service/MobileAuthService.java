package com.invoicegen.invoice_backend.service;

import com.invoicegen.invoice_backend.config.JwtUtil;
import com.invoicegen.invoice_backend.dto.LoginRequest;
import com.invoicegen.invoice_backend.dto.MobileAuthResponse;
import com.invoicegen.invoice_backend.dto.RegisterRequest;
import com.invoicegen.invoice_backend.entity.User;
import com.invoicegen.invoice_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MobileAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public MobileAuthResponse register(RegisterRequest request) {
        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // Create new user
        String userId = "mobile_" + UUID.randomUUID().toString();
        User user = User.builder()
                .clerkId(userId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .subscriptionType("FREE")
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(userId, request.getEmail());

        return new MobileAuthResponse(
                token,
                userId,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                "Registration successful"
        );
    }

    public MobileAuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getClerkId(), user.getEmail());

        return new MobileAuthResponse(
                token,
                user.getClerkId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                "Login successful"
        );
    }
}