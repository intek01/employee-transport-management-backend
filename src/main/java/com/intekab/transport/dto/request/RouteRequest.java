package com.intekab.transport.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RouteRequest(

        @NotBlank(message = "routeName is required")
        String routeName,

        @NotBlank(message = "startLocation is required")
        String startLocation,

        @NotBlank(message = "endLocation is required")
        String endLocation,

        @Positive(message = "distanceKm must be greater than zero")
        Double distanceKm
) {
}
