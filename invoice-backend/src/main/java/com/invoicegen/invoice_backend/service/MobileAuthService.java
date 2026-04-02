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
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // If user exists but has no password (web Clerk user)
            // just add the password and use their existing clerkId
            if (user.getPassword() == null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(user);

                String token = jwtUtil.generateToken(user.getClerkId(), user.getEmail());
                return new MobileAuthResponse(
                        token,
                        user.getClerkId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        "Account linked successfully! Your existing invoices are now accessible."
                );
            }

            // User already has a mobile account
            throw new RuntimeException("Email already in use. Please login instead.");
        }

        // Brand new user - create fresh account
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

        // If user has no password (Clerk web user) - set password on first mobile login
        if (user.getPassword() == null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);

            String token = jwtUtil.generateToken(user.getClerkId(), user.getEmail());
            return new MobileAuthResponse(
                    token,
                    user.getClerkId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    "Welcome! Your account has been set up for mobile."
            );
        }

        // Normal login for existing mobile users
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
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