package com.intekab.transport.service;

import com.intekab.transport.dto.request.RouteRequest;
import com.intekab.transport.dto.response.RouteResponse;

import java.util.List;

public interface RouteService {

    RouteResponse create(RouteRequest request);

    RouteResponse getById(Long id);

    List<RouteResponse> getAll();

    RouteResponse update(Long id, RouteRequest request);

    void delete(Long id);
}
