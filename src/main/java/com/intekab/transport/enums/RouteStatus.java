package com.intekab.transport.enums;

/**
 * Operational status of a {@link com.intekab.transport.entity.Route}.
 * Vehicles assigned to a non-{@link #ACTIVE} route cannot accept new bookings.
 */
public enum RouteStatus {
    ACTIVE,
    INACTIVE
}
