package com.intekab.transport.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingRequest(

        @NotNull(message = "employeeId is required")
        Long employeeId,

        @NotNull(message = "vehicleId is required")
        Long vehicleId,

        @NotNull(message = "bookingDate is required")
        @FutureOrPresent(message = "bookingDate cannot be in the past")
        LocalDate bookingDate,

        @NotBlank(message = "pickupPoint is required")
        String pickupPoint
) {
}
