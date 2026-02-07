package com.invoicegen.invoice_backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoCheck {

    @Autowired
    MongoTemplate mongoTemplate;

    @PostConstruct
    public void check() {
        System.out.println("Connected to DB: " + mongoTemplate.getDb().getName());
    }
}
