package com.groupproject.libraryManagementSystem.dto.userDTO.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class VerifyUserRequest {
    @NotNull
    @NotEmpty
    @Length(min = 4, max = 4)
    private String otp;

    @NotNull
    @NotEmpty
    @Email
    private String email;
}
