package com.intekab.transport.service;

import com.intekab.transport.dto.request.EmployeeRequest;
import com.intekab.transport.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse create(EmployeeRequest request);

    EmployeeResponse getById(Long id);

    List<EmployeeResponse> getAll();

    EmployeeResponse update(Long id, EmployeeRequest request);

    void delete(Long id);
}
