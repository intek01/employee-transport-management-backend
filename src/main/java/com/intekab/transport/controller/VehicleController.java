package com.intekab.transport.controller;

import com.intekab.transport.dto.request.VehicleRequest;
import com.intekab.transport.dto.response.VehicleResponse;
import com.intekab.transport.service.VehicleService;
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
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Vehicles", description = "Vehicles assigned to a route, each with a fixed seat capacity")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @Operation(summary = "Register a new vehicle on a route")
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a vehicle by id")
    public ResponseEntity<VehicleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all vehicles")
    public ResponseEntity<List<VehicleResponse>> getAll() {
        return ResponseEntity.ok(vehicleService.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a vehicle")
    public ResponseEntity<VehicleResponse> update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vehicle")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
