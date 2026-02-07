package com.invoicegen.invoice_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class InvoiceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceBackendApplication.class, args);
	}

}
