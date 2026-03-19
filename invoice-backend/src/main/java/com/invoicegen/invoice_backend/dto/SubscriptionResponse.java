package com.invoicegen.invoice_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponse {
    private String status; // SUCCESS or FAILED
    private String message;
    private String subscriptionType; // FREE or PREMIUM
}