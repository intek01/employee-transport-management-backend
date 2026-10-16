package com.intekab.transport.dto.response;

import com.intekab.transport.entity.Booking;
import com.intekab.transport.enums.BookingStatus;

import java.time.Instant;
import java.time.LocalDate;

public record BookingResponse(
        Long id,
        Long employeeId,
        String employeeName,
        Long vehicleId,
        String vehicleRegistrationNumber,
        LocalDate bookingDate,
        String pickupPoint,
        BookingStatus status,
        Instant createdAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getEmployee().getId(),
                booking.getEmployee().getFullName(),
                booking.getVehicle().getId(),
                booking.getVehicle().getRegistrationNumber(),
                booking.getBookingDate(),
                booking.getPickupPoint(),
                booking.getStatus(),
                booking.getCreatedAt());
    }
}
