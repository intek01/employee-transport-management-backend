package com.intekab.transport.repository;

import com.intekab.transport.entity.Booking;
import com.intekab.transport.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Used for the duplicate-booking business rule: true if this employee already
     * has a booking on this vehicle for this date that isn't in {@code excludedStatus}
     * (callers pass {@link BookingStatus#CANCELLED} so a re-booking after cancelling
     * is allowed).
     */
    boolean existsByEmployeeIdAndVehicleIdAndBookingDateAndStatusNot(
            Long employeeId, Long vehicleId, LocalDate bookingDate, BookingStatus excludedStatus);

    /**
     * Used for the seat-capacity business rule: counts bookings on this vehicle for
     * this date that aren't in {@code excludedStatus}, so cancelled bookings free up
     * their seat.
     */
    long countByVehicleIdAndBookingDateAndStatusNot(
            Long vehicleId, LocalDate bookingDate, BookingStatus excludedStatus);

    List<Booking> findByEmployeeId(Long employeeId);

    List<Booking> findByVehicleIdAndBookingDate(Long vehicleId, LocalDate bookingDate);
}
