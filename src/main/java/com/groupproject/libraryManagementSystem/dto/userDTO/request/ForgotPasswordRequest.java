package com.groupproject.libraryManagementSystem.dto.userDTO.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
    private String new_password;
    private String otp;
}
