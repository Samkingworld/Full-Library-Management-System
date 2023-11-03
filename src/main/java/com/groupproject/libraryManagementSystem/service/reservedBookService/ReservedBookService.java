package com.groupproject.libraryManagementSystem.service.reservedBookService;

import com.groupproject.libraryManagementSystem.dto.reservedBookDTO.ReservedBookRequest;

public interface ReservedBookService {

    Object reserveBook (ReservedBookRequest request);

    Object deleteReservedBook(Long reservationID);
}
