package com.invoicegen.invoice_backend.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "invoices")
public class Invoice {
    @Id
    private String id;

    private String clerkId;
    private Company company;
    private Billing billing;
    private Shipping shipping;
    private InvoiceDetails invoice;
    private List<Item> items;
    private String notes;
    private String logo;
    private double tax;
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant lastUpdatedAt;
    private String thumbnailUrl;
    private String template;
    private String title;

    @Data
    public static class Company {
        private String name;
        private String phone;
        private String address;
    }

    @Data
    public static class Billing {
        private String name;
        private String phone;
        private String address;
    }

    @Data
    public static class Shipping {
        private String name;
        private String phone;
        private String address;
    }

    @Data
    public static class InvoiceDetails {
        private String number;
        private String date;
        private String dueDate;
    }

    @Data
    public static class Item {
        private String name;
        private int qty;
        private double amount;
        private String description;
    }
}