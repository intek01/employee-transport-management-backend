package com.intekab.transport.controller;

import com.intekab.transport.dto.request.RouteRequest;
import com.intekab.transport.dto.response.RouteResponse;
import com.intekab.transport.service.RouteService;
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
@RequestMapping("/api/v1/routes")
@Tag(name = "Routes", description = "Shuttle routes that vehicles are assigned to")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    @Operation(summary = "Create a new route")
    public ResponseEntity<RouteResponse> create(@Valid @RequestBody RouteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a route by id")
    public ResponseEntity<RouteResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all routes")
    public ResponseEntity<List<RouteResponse>> getAll() {
        return ResponseEntity.ok(routeService.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a route")
    public ResponseEntity<RouteResponse> update(@PathVariable Long id, @Valid @RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a route")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
