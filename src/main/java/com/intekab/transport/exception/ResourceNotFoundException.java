package com.intekab.transport.exception;

/**
 * Thrown when a referenced entity (employee, route, vehicle or booking) does not exist.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
