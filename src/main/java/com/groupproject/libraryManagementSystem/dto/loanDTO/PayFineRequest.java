package com.groupproject.libraryManagementSystem.dto.loanDTO;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayFineRequest {
    @NotNull
    @NotEmpty
    @Email
    private String user_email;

    @NotNull
    @Digits(integer = 10, fraction = 2, message = "Invalid value. Maximum 10 Integral Digits and 2 fractions e.g. 200.00")
    private double fine_amount_Naira;

}
