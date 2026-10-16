package com.intekab.transport.service;

import com.intekab.transport.dto.request.EmployeeRequest;
import com.intekab.transport.dto.response.EmployeeResponse;
import com.intekab.transport.entity.Employee;
import com.intekab.transport.exception.ResourceNotFoundException;
import com.intekab.transport.repository.EmployeeRepository;
import com.intekab.transport.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EmployeeServiceImpl} covering standard CRUD behaviour
 * and the not-found path, with the repository mocked.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee("Asha Rahman", "asha.rahman@example.com", "9876543210", "Engineering");
        ReflectionTestUtils.setField(employee, "id", 1L);
    }

    @Test
    void create_savesAndReturnsEmployee() {
        EmployeeRequest request = new EmployeeRequest("Asha Rahman", "asha.rahman@example.com", "9876543210", "Engineering");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse response = employeeService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("asha.rahman@example.com");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void getById_whenExists_returnsEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.fullName()).isEqualTo("Asha Rahman");
    }

    @Test
    void getById_whenMissing_throwsResourceNotFound() {
        when(employeeRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("404");
    }

    @Test
    void getAll_returnsAllEmployeesMappedToResponses() {
        Employee second = new Employee("Rohan Das", "rohan.das@example.com", "9123456780", "Operations");
        ReflectionTestUtils.setField(second, "id", 2L);
        when(employeeRepository.findAll()).thenReturn(List.of(employee, second));

        List<EmployeeResponse> responses = employeeService.getAll();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(EmployeeResponse::email)
                .containsExactly("asha.rahman@example.com", "rohan.das@example.com");
    }

    @Test
    void update_whenExists_updatesFieldsAndSaves() {
        EmployeeRequest request = new EmployeeRequest("Asha Rahman", "asha.updated@example.com", "9876543210", "Platform");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        EmployeeResponse response = employeeService.update(1L, request);

        assertThat(response.email()).isEqualTo("asha.updated@example.com");
        assertThat(response.department()).isEqualTo("Platform");
        verify(employeeRepository).save(employee);
    }

    @Test
    void update_whenMissing_throwsResourceNotFoundAndDoesNotSave() {
        EmployeeRequest request = new EmployeeRequest("Asha Rahman", "asha.rahman@example.com", "9876543210", "Engineering");
        when(employeeRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.update(404L, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void delete_whenExists_deletesEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.delete(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void delete_whenMissing_throwsResourceNotFoundAndDoesNotDelete() {
        when(employeeRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.delete(404L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(employeeRepository, never()).delete(any());
    }
}
