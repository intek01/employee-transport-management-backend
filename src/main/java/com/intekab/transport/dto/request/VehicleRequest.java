package com.intekab.transport.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VehicleRequest(

        @NotBlank(message = "registrationNumber is required")
        String registrationNumber,

        @NotBlank(message = "driverName is required")
        String driverName,

        @Positive(message = "capacity must be greater than zero")
        Integer capacity,

        @NotNull(message = "routeId is required")
        Long routeId
) {
}
