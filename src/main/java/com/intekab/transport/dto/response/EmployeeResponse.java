package com.intekab.transport.dto.response;

import com.intekab.transport.entity.Employee;

import java.time.Instant;

public record EmployeeResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        String department,
        Instant createdAt
) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getDepartment(),
                employee.getCreatedAt());
    }
}
