package com.intekab.transport.controller;

import com.intekab.transport.dto.request.EmployeeRequest;
import com.intekab.transport.dto.response.EmployeeResponse;
import com.intekab.transport.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employees", description = "Employee directory used by the booking workflow")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @Operation(summary = "Register a new employee")
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by id")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all employees")
    public ResponseEntity<List<EmployeeResponse>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee")
    public ResponseEntity<EmployeeResponse> update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
