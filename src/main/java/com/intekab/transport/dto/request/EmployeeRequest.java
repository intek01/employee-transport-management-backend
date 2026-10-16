package com.intekab.transport.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmployeeRequest(

        @NotBlank(message = "fullName is required")
        String fullName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        String email,

        @NotBlank(message = "phoneNumber is required")
        @Pattern(regexp = "\\d{10}", message = "phoneNumber must be exactly 10 digits")
        String phoneNumber,

        @NotBlank(message = "department is required")
        String department
) {
}
