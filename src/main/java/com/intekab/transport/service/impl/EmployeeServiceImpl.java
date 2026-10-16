package com.intekab.transport.service.impl;

import com.intekab.transport.dto.request.EmployeeRequest;
import com.intekab.transport.dto.response.EmployeeResponse;
import com.intekab.transport.entity.Employee;
import com.intekab.transport.exception.ResourceNotFoundException;
import com.intekab.transport.repository.EmployeeRepository;
import com.intekab.transport.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponse create(EmployeeRequest request) {
        Employee employee = new Employee(
                request.fullName(), request.email(), request.phoneNumber(), request.department());
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return EmployeeResponse.from(findEmployeeOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Override
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findEmployeeOrThrow(id);
        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setPhoneNumber(request.phoneNumber());
        employee.setDepartment(request.department());
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Override
    public void delete(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        employeeRepository.delete(employee);
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }
}
