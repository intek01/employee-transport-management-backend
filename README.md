# Employee Transport Management Backend

A Spring Boot REST backend for managing an organization's employee shuttle service: employees, routes, vehicles, and seat bookings.

## Problem Statement

Companies that run an employee shuttle service need to track which routes exist, which vehicles are assigned to each route, and which employees have booked a seat on a given date. Two rules make this non-trivial:

1. A vehicle has a fixed seat capacity and must not be overbooked for a given date.
2. An employee should not be able to hold two active bookings on the same vehicle for the same date.

This project implements those rules as enforced, testable business logic in the service layer rather than leaving them to the client.

## Architecture

The backend follows a standard layered architecture:

```
Controller  ->  Service (interface + impl)  ->  Repository (Spring Data JPA)  ->  PostgreSQL
```

- **Controller layer** — thin REST adapters (`@RestController`) that validate incoming requests (`@Valid`) and delegate to the service layer. No business logic lives here.
- **Service layer** — one interface and one implementation per domain (Employee, Route, Vehicle, Booking). `BookingServiceImpl` is where the actual business rules live.
- **Repository layer** — Spring Data JPA repositories. The booking rules are backed by two derived-query methods (`existsByEmployeeIdAndVehicleIdAndBookingDateAndStatusNot`, `countByVehicleIdAndBookingDateAndStatusNot`) rather than loading full collections into memory.
- **DTOs** — Java records for both requests (with Jakarta Bean Validation annotations) and responses, so JPA entities are never exposed directly over the API.
- **Exception handling** — a single `@RestControllerAdvice` (`GlobalExceptionHandler`) maps each domain exception to a consistent JSON error body with the correct HTTP status.

### Domain model

| Entity | Key fields | Notes |
|---|---|---|
| `Employee` | fullName, email (unique), phoneNumber, department | |
| `Route` | routeName (unique), startLocation, endLocation, distanceKm, status | status is `ACTIVE` / `INACTIVE` |
| `Vehicle` | registrationNumber (unique), driverName, capacity, status, route | status is `ACTIVE` / `UNDER_MAINTENANCE` / `INACTIVE`; each vehicle belongs to one route |
| `Booking` | employee, vehicle, bookingDate, pickupPoint, status | status is `CONFIRMED` / `CANCELLED` / `COMPLETED` |

### Business rules enforced in `BookingServiceImpl`

When a booking is created, the following checks run in order, each mapped to a specific HTTP status:

1. Employee must exist — otherwise `404`.
2. Vehicle must exist — otherwise `404`.
3. Vehicle must be `ACTIVE` (not under maintenance / inactive) — otherwise `409`.
4. The vehicle's route must be `ACTIVE` — otherwise `409`.
5. The employee must not already hold a non-cancelled booking on the same vehicle for the same date — otherwise `409` (duplicate booking).
6. The vehicle must not already be at capacity for that date — otherwise `409` (capacity exceeded).

Cancelling a booking is idempotent for an already-cancelled booking (returns the current state without writing again) and rejected with `409` for a booking that is already `COMPLETED`.

## API Endpoints

Base path: `/api/v1`

| Method | Path | Description |
|---|---|---|
| POST | `/employees` | Register a new employee |
| GET | `/employees/{id}` | Get an employee by id |
| GET | `/employees` | List all employees |
| PUT | `/employees/{id}` | Update an employee |
| DELETE | `/employees/{id}` | Delete an employee |
| POST | `/routes` | Create a route |
| GET | `/routes/{id}` | Get a route by id |
| GET | `/routes` | List all routes |
| PUT | `/routes/{id}` | Update a route |
| DELETE | `/routes/{id}` | Delete a route |
| POST | `/vehicles` | Register a vehicle on a route |
| GET | `/vehicles/{id}` | Get a vehicle by id |
| GET | `/vehicles` | List all vehicles |
| PUT | `/vehicles/{id}` | Update a vehicle |
| DELETE | `/vehicles/{id}` | Delete a vehicle |
| POST | `/bookings` | Create a booking (duplicate/capacity rules enforced) |
| GET | `/bookings/{id}` | Get a booking by id |
| GET | `/bookings?employeeId=` | List bookings, optionally filtered by employee |
| PATCH | `/bookings/{id}/cancel` | Cancel a booking |

Interactive API documentation (Swagger UI, via springdoc-openapi) is served at `/swagger-ui.html` once the application is running, with the raw OpenAPI spec at `/v3/api-docs`.

## Tech Stack

- Java 17
- Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Spring Validation)
- PostgreSQL (runtime datasource)
- springdoc-openapi 2.6.0 (Swagger UI)
- JUnit 5, Mockito, AssertJ (test scope, via `spring-boot-starter-test`)
- Maven

## Running Locally

1. Create a PostgreSQL database and user matching (or override via environment variables):
   ```
   DB_URL=jdbc:postgresql://localhost:5432/transport_db
   DB_USERNAME=transport_user
   DB_PASSWORD=transport_pass
   ```
2. Build and run:
   ```
   mvn spring-boot:run
   ```
3. The API is available at `http://localhost:8080/api/v1/...` and Swagger UI at `http://localhost:8080/swagger-ui.html`.

`spring.jpa.hibernate.ddl-auto` is set to `update`, so tables are created automatically on first run.

## Testing

Unit tests cover the service layer with Mockito — repositories are mocked, so no database or Spring context is required to run them:

- `BookingServiceImplTest` — 11 tests covering every business rule: successful booking creation, employee/vehicle not found, vehicle under maintenance, inactive route, duplicate booking, capacity exceeded, successful cancellation, idempotent re-cancellation, rejecting cancellation of a completed booking, and cancelling a booking that doesn't exist.
- `EmployeeServiceImplTest` — CRUD coverage for the employee service, including the not-found path for get/update/delete.

Run all tests with:
```
mvn test
```

A GitHub Actions workflow (`.github/workflows/ci.yml`) runs `mvn clean verify` on every push and pull request against `main`, so the build and full test suite are verified automatically on GitHub's runners.

## API Testing with Postman

A Postman collection is included at `postman/EmployeeTransportManagementBackend.postman_collection.json`, covering the main flows: creating an employee, route, and vehicle; creating a booking successfully; and the two conflict cases (duplicate booking, capacity exceeded). Import it into Postman and set the `baseUrl` collection variable (defaults to `http://localhost:8080`).

## License

Licensed under the MIT License — see [LICENSE](LICENSE).
