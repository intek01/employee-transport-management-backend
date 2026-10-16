package com.intekab.transport.dto.response;

import com.intekab.transport.entity.Vehicle;
import com.intekab.transport.enums.VehicleStatus;

public record VehicleResponse(
        Long id,
        String registrationNumber,
        String driverName,
        Integer capacity,
        VehicleStatus status,
        Long routeId,
        String routeName
) {
    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getRegistrationNumber(),
                vehicle.getDriverName(),
                vehicle.getCapacity(),
                vehicle.getStatus(),
                vehicle.getRoute().getId(),
                vehicle.getRoute().getRouteName());
    }
}
