package com.invoicegen.invoice_backend.repository;

import com.invoicegen.invoice_backend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
