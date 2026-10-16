package com.intekab.transport.controller;

import com.intekab.transport.dto.request.BookingRequest;
import com.intekab.transport.dto.response.BookingResponse;
import com.intekab.transport.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Employee seat bookings, with duplicate and capacity checks enforced server-side")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Create a booking",
            description = "Rejects the request if the vehicle/route is inactive, the employee already holds "
                    + "an active booking on this vehicle for this date, or the vehicle is already at capacity.")
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a booking by id")
    public ResponseEntity<BookingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all bookings, optionally filtered by employeeId")
    public ResponseEntity<List<BookingResponse>> getAll(
            @RequestParam(required = false) Long employeeId) {
        if (employeeId != null) {
            return ResponseEntity.ok(bookingService.getByEmployeeId(employeeId));
        }
        return ResponseEntity.ok(bookingService.getAll());
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking",
            description = "Idempotent for an already-cancelled booking; rejected for a completed one.")
    public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancel(id));
    }
}
