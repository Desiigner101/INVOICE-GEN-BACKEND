package com.invoicegen.invoice_backend.controller;

import com.invoicegen.invoice_backend.dto.LoginRequest;
import com.invoicegen.invoice_backend.dto.MobileAuthResponse;
import com.invoicegen.invoice_backend.dto.RegisterRequest;
import com.invoicegen.invoice_backend.service.MobileAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MobileAuthController {

    private final MobileAuthService mobileAuthService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            MobileAuthResponse response = mobileAuthService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            MobileAuthResponse response = mobileAuthService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}