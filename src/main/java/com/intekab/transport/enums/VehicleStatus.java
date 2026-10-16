package com.intekab.transport.enums;

/**
 * Operational status of a {@link com.intekab.transport.entity.Vehicle}.
 * Only {@link #ACTIVE} vehicles can accept new bookings.
 */
public enum VehicleStatus {
    ACTIVE,
    UNDER_MAINTENANCE,
    INACTIVE
}
