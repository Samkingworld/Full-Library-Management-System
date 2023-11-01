package com.groupproject.libraryManagementSystem.dto.reservedBookDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservedBookRequest {

    @NotNull
    @NotEmpty
    @Email
    private String userEmail;

    private Long bookId;

    @NotNull
    @NotEmpty
    private String bookTitle;

}
