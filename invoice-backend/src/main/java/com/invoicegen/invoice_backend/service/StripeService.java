package com.invoicegen.invoice_backend.service;

import com.invoicegen.invoice_backend.entity.User;
import com.invoicegen.invoice_backend.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.price.id}")
    private String stripePriceId;

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public Subscription createSubscription(User user, String paymentMethodId) throws StripeException {
        // Create or get Stripe customer
        String customerId = user.getStripeCustomerId();

        if (customerId == null) {
            Customer customer = createCustomer(user, paymentMethodId);
            customerId = customer.getId();
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }

        // Create subscription
        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(
                        SubscriptionCreateParams.Item.builder()
                                .setPrice(stripePriceId)
                                .build()
                )
                .setDefaultPaymentMethod(paymentMethodId)
                .build();

        return Subscription.create(params);
    }

    private Customer createCustomer(User user, String paymentMethodId) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .setName(user.getFirstName() + " " + user.getLastName())
                .setPaymentMethod(paymentMethodId)
                .setInvoiceSettings(
                        CustomerCreateParams.InvoiceSettings.builder()
                                .setDefaultPaymentMethod(paymentMethodId)
                                .build()
                )
                .build();

        return Customer.create(params);
    }

    public void cancelSubscription(String subscriptionId) throws StripeException {
        Subscription subscription = Subscription.retrieve(subscriptionId);
        subscription.cancel();
    }
}