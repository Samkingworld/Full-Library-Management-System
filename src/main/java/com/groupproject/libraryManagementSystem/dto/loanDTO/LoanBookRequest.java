package com.groupproject.libraryManagementSystem.dto.loanDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoanBookRequest {
    @Email
    @NotNull
    private String user_email;
    @NotNull
    @NotEmpty
    private String book_title_1;

    private String book_title_2;

    @NotNull
    private int return_date_in_days;
}
