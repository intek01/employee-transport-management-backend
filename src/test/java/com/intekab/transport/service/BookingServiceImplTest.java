package com.intekab.transport.service;

import com.intekab.transport.dto.request.BookingRequest;
import com.intekab.transport.dto.response.BookingResponse;
import com.intekab.transport.entity.Booking;
import com.intekab.transport.entity.Employee;
import com.intekab.transport.entity.Route;
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
import com.intekab.transport.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BookingServiceImpl} — this is where the real business
 * rules live (duplicate booking, seat capacity, inactive vehicle/route), so
 * these are the tests that matter most: every repository call is mocked, no
 * Spring context or database is involved.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Employee employee;
    private Route activeRoute;
    private Vehicle activeVehicle;
    private LocalDate bookingDate;

    @BeforeEach
    void setUp() {
        employee = new Employee("Asha Rahman", "asha.rahman@example.com", "9876543210", "Engineering");
        ReflectionTestUtils.setField(employee, "id", 1L);

        activeRoute = new Route("Route-1", "City Center", "Tech Park", 12.5);
        ReflectionTestUtils.setField(activeRoute, "id", 10L);

        activeVehicle = new Vehicle("TR-01-AB-1234", "Ramesh Kumar", 2, activeRoute);
        ReflectionTestUtils.setField(activeVehicle, "id", 100L);

        bookingDate = LocalDate.now().plusDays(1);
    }

    @Test
    void create_whenEverythingValid_savesAndReturnsBooking() {
        BookingRequest request = new BookingRequest(employee.getId(), activeVehicle.getId(), bookingDate, "Gate 2");

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(vehicleRepository.findById(activeVehicle.getId())).thenReturn(Optional.of(activeVehicle));
        when(bookingRepository.existsByEmployeeIdAndVehicleIdAndBookingDateAndStatusNot(
                employee.getId(), activeVehicle.getId(), bookingDate, BookingStatus.CANCELLED))
                .thenReturn(false);
        when(bookingRepository.countByVehicleIdAndBookingDateAndStatusNot(
                activeVehicle.getId(), bookingDate, BookingStatus.CANCELLED))
                .thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            ReflectionTestUtils.setField(b, "id", 500L);
            ReflectionTestUtils.setField(b, "createdAt", java.time.Instant.now());
            return b;
        });

        BookingResponse response = bookingService.create(request);

        assertThat(response.id()).isEqualTo(500L);
        assertThat(response.status()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(response.pickupPoint()).isEqualTo("Gate 2");
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_whenEmployeeDoesNotExist_throwsResourceNotFound() {
        BookingRequest request = new BookingRequest(999L, activeVehicle.getId(), bookingDate, "Gate 2");
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenVehicleDoesNotExist_throwsResourceNotFound() {
        BookingRequest request = new BookingRequest(employee.getId(), 999L, bookingDate, "Gate 2");
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_whenVehicleUnderMaintenance_throwsVehicleUnavailable() {
        activeVehicle.setStatus(VehicleStatus.UNDER_MAINTENANCE);
        BookingRequest request = new BookingRequest(employee.getId(), activeVehicle.getId(), bookingDate, "Gate 2");
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(vehicleRepository.findById(activeVehicle.getId())).thenReturn(Optional.of(activeVehicle));

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(VehicleUnavailableException.class)
                .hasMessageContaining("UNDER_MAINTENANCE");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenRouteInactive_throwsVehicleUnavailable() {
        activeRoute.setStatus(RouteStatus.INACTIVE);
        BookingRequest request = new BookingRequest(employee.getId(), activeVehicle.getId(), bookingDate, "Gate 2");
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(vehicleRepository.findById(activeVehicle.getId())).thenReturn(Optional.of(activeVehicle));

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(VehicleUnavailableException.class)
                .hasMessageContaining("not active");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenEmployeeAlreadyHasActiveBookingOnSameVehicleAndDate_throwsDuplicate() {
        BookingRequest request = new BookingRequest(employee.getId(), activeVehicle.getId(), bookingDate, "Gate 2");
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(vehicleRepository.findById(activeVehicle.getId())).thenReturn(Optional.of(activeVehicle));
        when(bookingRepository.existsByEmployeeIdAndVehicleIdAndBookingDateAndStatusNot(
                employee.getId(), activeVehicle.getId(), bookingDate, BookingStatus.CANCELLED))
                .thenReturn(true);

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(DuplicateBookingException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenVehicleAtCapacity_throwsCapacityExceeded() {
        // capacity is 2 on the shared fixture vehicle
        BookingRequest request = new BookingRequest(employee.getId(), activeVehicle.getId(), bookingDate, "Gate 2");
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(vehicleRepository.findById(activeVehicle.getId())).thenReturn(Optional.of(activeVehicle));
        when(bookingRepository.existsByEmployeeIdAndVehicleIdAndBookingDateAndStatusNot(
                eq(employee.getId()), eq(activeVehicle.getId()), eq(bookingDate), eq(BookingStatus.CANCELLED)))
                .thenReturn(false);
        when(bookingRepository.countByVehicleIdAndBookingDateAndStatusNot(
                activeVehicle.getId(), bookingDate, BookingStatus.CANCELLED))
                .thenReturn(2L); // == capacity

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(CapacityExceededException.class)
                .hasMessageContaining("capacity: 2");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancel_whenConfirmed_transitionsToCancelledAndSaves() {
        Booking booking = new Booking(employee, activeVehicle, bookingDate, "Gate 2");
        ReflectionTestUtils.setField(booking, "id", 42L);
        when(bookingRepository.findById(42L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponse response = bookingService.cancel(42L);

        assertThat(response.status()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancel_whenAlreadyCancelled_isIdempotentAndDoesNotSaveAgain() {
        Booking booking = new Booking(employee, activeVehicle, bookingDate, "Gate 2");
        ReflectionTestUtils.setField(booking, "id", 42L);
        booking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById(42L)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.cancel(42L);

        assertThat(response.status()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancel_whenCompleted_throwsInvalidBookingOperation() {
        Booking booking = new Booking(employee, activeVehicle, bookingDate, "Gate 2");
        ReflectionTestUtils.setField(booking, "id", 42L);
        booking.setStatus(BookingStatus.COMPLETED);
        when(bookingRepository.findById(42L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancel(42L))
                .isInstanceOf(InvalidBookingOperationException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancel_whenBookingDoesNotExist_throwsResourceNotFound() {
        when(bookingRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancel(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
