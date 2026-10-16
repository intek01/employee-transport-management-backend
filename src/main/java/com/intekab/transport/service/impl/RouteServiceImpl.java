package com.intekab.transport.service.impl;

import com.intekab.transport.dto.request.RouteRequest;
import com.intekab.transport.dto.response.RouteResponse;
import com.intekab.transport.entity.Route;
import com.intekab.transport.exception.ResourceNotFoundException;
import com.intekab.transport.repository.RouteRepository;
import com.intekab.transport.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public RouteResponse create(RouteRequest request) {
        Route route = new Route(
                request.routeName(), request.startLocation(), request.endLocation(), request.distanceKm());
        return RouteResponse.from(routeRepository.save(route));
    }

    @Override
    @Transactional(readOnly = true)
    public RouteResponse getById(Long id) {
        return RouteResponse.from(findRouteOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getAll() {
        return routeRepository.findAll().stream()
                .map(RouteResponse::from)
                .toList();
    }

    @Override
    public RouteResponse update(Long id, RouteRequest request) {
        Route route = findRouteOrThrow(id);
        route.setRouteName(request.routeName());
        route.setStartLocation(request.startLocation());
        route.setEndLocation(request.endLocation());
        route.setDistanceKm(request.distanceKm());
        return RouteResponse.from(routeRepository.save(route));
    }

    @Override
    public void delete(Long id) {
        Route route = findRouteOrThrow(id);
        routeRepository.delete(route);
    }

    private Route findRouteOrThrow(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", id));
    }
}
