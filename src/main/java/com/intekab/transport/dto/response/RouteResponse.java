package com.intekab.transport.dto.response;

import com.intekab.transport.entity.Route;
import com.intekab.transport.enums.RouteStatus;

public record RouteResponse(
        Long id,
        String routeName,
        String startLocation,
        String endLocation,
        Double distanceKm,
        RouteStatus status
) {
    public static RouteResponse from(Route route) {
        return new RouteResponse(
                route.getId(),
                route.getRouteName(),
                route.getStartLocation(),
                route.getEndLocation(),
                route.getDistanceKm(),
                route.getStatus());
    }
}
