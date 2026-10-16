package com.intekab.transport.service.impl;

import com.intekab.transport.dto.request.VehicleRequest;
import com.intekab.transport.dto.response.VehicleResponse;
import com.intekab.transport.entity.Route;
import com.intekab.transport.entity.Vehicle;
import com.intekab.transport.exception.ResourceNotFoundException;
import com.intekab.transport.repository.RouteRepository;
import com.intekab.transport.repository.VehicleRepository;
import com.intekab.transport.service.VehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RouteRepository routeRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, RouteRepository routeRepository) {
        this.vehicleRepository = vehicleRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    public VehicleResponse create(VehicleRequest request) {
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", request.routeId()));
        Vehicle vehicle = new Vehicle(
                request.registrationNumber(), request.driverName(), request.capacity(), route);
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getById(Long id) {
        return VehicleResponse.from(findVehicleOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAll() {
        return vehicleRepository.findAll().stream()
                .map(VehicleResponse::from)
                .toList();
    }

    @Override
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = findVehicleOrThrow(id);
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", request.routeId()));
        vehicle.setRegistrationNumber(request.registrationNumber());
        vehicle.setDriverName(request.driverName());
        vehicle.setCapacity(request.capacity());
        vehicle.setRoute(route);
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Override
    public void delete(Long id) {
        Vehicle vehicle = findVehicleOrThrow(id);
        vehicleRepository.delete(vehicle);
    }

    private Vehicle findVehicleOrThrow(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }
}
