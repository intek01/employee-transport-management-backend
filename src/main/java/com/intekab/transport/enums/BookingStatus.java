package com.intekab.transport.enums;

/**
 * Lifecycle status of a {@link com.intekab.transport.entity.Booking}.
 *
 * <p>Valid transitions: {@code CONFIRMED -> CANCELLED}, {@code CONFIRMED -> COMPLETED}.
 * A {@code CANCELLED} or {@code COMPLETED} booking is terminal and does not count
 * against a vehicle's seat capacity for duplicate/capacity checks.</p>
 */
public enum BookingStatus {
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
