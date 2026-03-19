package com.invoicegen.invoice_backend.dto;

import lombok.Data;

@Data
public class SubscriptionRequest {
    private String paymentMethodId; // Payment method ID from Stripe frontend
}