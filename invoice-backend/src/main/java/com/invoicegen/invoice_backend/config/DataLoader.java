package com.invoicegen.invoice_backend;

import com.invoicegen.invoice_backend.entity.User;
import com.invoicegen.invoice_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        User user = User.builder()
                .clerkId("123")
                .email("gino@example.com")
                .firstName("Gino")
                .lastName("Sarsonas")
                .photoUrl("http://photo.url")
                .build();

        userRepository.save(user);
        System.out.println("User saved: " + user);
    }
}
