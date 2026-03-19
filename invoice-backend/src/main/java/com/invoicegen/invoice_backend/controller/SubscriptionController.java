package com.invoicegen.invoice_backend.controller;

import com.invoicegen.invoice_backend.dto.SubscriptionRequest;
import com.invoicegen.invoice_backend.dto.SubscriptionResponse;
import com.invoicegen.invoice_backend.entity.User;
import com.invoicegen.invoice_backend.repository.UserRepository;
import com.invoicegen.invoice_backend.service.StripeService;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final StripeService stripeService;
    private final UserRepository userRepository;

    @PostMapping("/upgrade")
    public ResponseEntity<SubscriptionResponse> upgradeToPremium(
            @RequestBody SubscriptionRequest request,
            Authentication authentication) {

        try {
            String clerkId = authentication.getName();
            User user = userRepository.findByClerkId(clerkId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if already premium
            if ("PREMIUM".equals(user.getSubscriptionType())) {
                return ResponseEntity.ok(new SubscriptionResponse(
                        "SUCCESS",
                        "Already subscribed to Premium",
                        "PREMIUM"
                ));
            }

            // Create Stripe subscription
            Subscription subscription = stripeService.createSubscription(user, request.getPaymentMethodId());

            // Update user to Premium
            user.setSubscriptionType("PREMIUM");
            user.setStripeSubscriptionId(subscription.getId());
            user.setSubscriptionStartDate(Instant.now());
            user.setSubscriptionEndDate(Instant.ofEpochSecond(subscription.getCurrentPeriodEnd()));
            userRepository.save(user);

            return ResponseEntity.ok(new SubscriptionResponse(
                    "SUCCESS",
                    "Successfully upgraded to Premium!",
                    "PREMIUM"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new SubscriptionResponse(
                    "FAILED",
                    "Payment failed: " + e.getMessage(),
                    "FREE"
            ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<SubscriptionResponse> getSubscriptionStatus(Authentication authentication) {
        String clerkId = authentication.getName();
        User user = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(new SubscriptionResponse(
                "SUCCESS",
                "Subscription status retrieved",
                user.getSubscriptionType()
        ));
    }

    @PostMapping("/cancel")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(Authentication authentication) {
        try {
            String clerkId = authentication.getName();
            User user = userRepository.findByClerkId(clerkId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getStripeSubscriptionId() != null) {
                stripeService.cancelSubscription(user.getStripeSubscriptionId());
            }

            user.setSubscriptionType("FREE");
            user.setStripeSubscriptionId(null);
            user.setSubscriptionEndDate(null);
            userRepository.save(user);

            return ResponseEntity.ok(new SubscriptionResponse(
                    "SUCCESS",
                    "Subscription cancelled",
                    "FREE"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new SubscriptionResponse(
                    "FAILED",
                    "Failed to cancel: " + e.getMessage(),
                    "PREMIUM"
            ));
        }
    }
}