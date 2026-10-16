package com.intekab.transport.repository;

import com.intekab.transport.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Vehicle> findByRouteId(Long routeId);
}
