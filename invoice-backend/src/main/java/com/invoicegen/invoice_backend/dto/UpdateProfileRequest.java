package com.invoicegen.invoice_backend.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String password;
    private String photoUrl;
}