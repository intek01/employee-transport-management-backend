package com.intekab.transport.service;

import com.intekab.transport.dto.request.VehicleRequest;
import com.intekab.transport.dto.response.VehicleResponse;

import java.util.List;

public interface VehicleService {

    VehicleResponse create(VehicleRequest request);

    VehicleResponse getById(Long id);

    List<VehicleResponse> getAll();

    VehicleResponse update(Long id, VehicleRequest request);

    void delete(Long id);
}
