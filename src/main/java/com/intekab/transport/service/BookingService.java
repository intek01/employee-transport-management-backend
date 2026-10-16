package com.intekab.transport.service;

import com.intekab.transport.dto.request.BookingRequest;
import com.intekab.transport.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse create(BookingRequest request);

    BookingResponse getById(Long id);

    List<BookingResponse> getAll();

    List<BookingResponse> getByEmployeeId(Long employeeId);

    BookingResponse cancel(Long id);
}
