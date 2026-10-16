package com.intekab.transport.service.impl;

import com.intekab.transport.dto.request.BookingRequest;
import com.intekab.transport.dto.response.BookingResponse;
import com.intekab.transport.entity.Booking;
import com.intekab.transport.entity.Employee;
import com.intekab.transport.entity.Vehicle;
import com.intekab.transport.enums.BookingStatus;
import com.intekab.transport.enums.RouteStatus;
import com.intekab.transport.enums.VehicleStatus;
import com.intekab.transport.exception.CapacityExceededException;
import com.intekab.transport.exception.DuplicateBookingException;
import com.intekab.transport.exception.InvalidBookingOperationException;
import com.intekab.transport.exception.ResourceNotFoundException;
import com.intekab.transport.exception.VehicleUnavailableException;
import com.intekab.transport.repository.BookingRepository;
import com.intekab.transport.repository.EmployeeRepository;
import com.intekab.transport.repository.VehicleRepository;
import com.intekab.transport.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Owns every booking business rule so the controller stays a thin HTTP adapter.
 * See the class-level Javadoc on each exception type for exactly which rule
 * maps to which HTTP status in {@link com.intekab.transport.exception.GlobalExceptionHandler}.
 */
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EmployeeRepository employeeRepository;
    private final VehicleRepository vehicleRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                               EmployeeRepository employeeRepository,
                               VehicleRepository vehicleRepository) {
        this.bookingRepository = bookingRepository;
        this.employeeRepository = employeeRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public BookingResponse create(BookingRequest request) {
        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.employeeId()));

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));

        if (vehicle.getStatus() != VehicleStatus.ACTIVE) {
            throw new VehicleUnavailableException(
                    "Vehicle " + vehicle.getId() + " is not active (status: " + vehicle.getStatus() + ")");
        }
        if (vehicle.getRoute().getStatus() != RouteStatus.ACTIVE) {
            throw new VehicleUnavailableException(
                    "Route " + vehicle.getRoute().getId() + " assigned to vehicle " + vehicle.getId()
                            + " is not active");
        }

        boolean alreadyBooked = bookingRepository.existsByEmployeeIdAndVehicleIdAndBookingDateAndStatusNot(
                request.employeeId(), request.vehicleId(), request.bookingDate(), BookingStatus.CANCELLED);
        if (alreadyBooked) {
            throw new DuplicateBookingException(
                    request.employeeId(), request.vehicleId(), request.bookingDate().toString());
        }

        long activeBookings = bookingRepository.countByVehicleIdAndBookingDateAndStatusNot(
                request.vehicleId(), request.bookingDate(), BookingStatus.CANCELLED);
        if (activeBookings >= vehicle.getCapacity()) {
            throw new CapacityExceededException(
                    request.vehicleId(), request.bookingDate().toString(), vehicle.getCapacity());
        }

        Booking booking = new Booking(employee, vehicle, request.bookingDate(), request.pickupPoint());
        return BookingResponse.from(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getById(Long id) {
        return BookingResponse.from(findBookingOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAll() {
        return bookingRepository.findAll().stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getByEmployeeId(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee", employeeId);
        }
        return bookingRepository.findByEmployeeId(employeeId).stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Override
    public BookingResponse cancel(Long id) {
        Booking booking = findBookingOrThrow(id);

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new InvalidBookingOperationException(
                    "Booking " + id + " is already COMPLETED and cannot be cancelled");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            // Cancelling an already-cancelled booking is a no-op, not an error.
            return BookingResponse.from(booking);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return BookingResponse.from(bookingRepository.save(booking));
    }

    private Booking findBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }
}
