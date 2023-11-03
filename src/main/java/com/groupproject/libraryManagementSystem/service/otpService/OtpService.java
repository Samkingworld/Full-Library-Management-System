package com.groupproject.libraryManagementSystem.service.otpService;

import com.groupproject.libraryManagementSystem.dto.userDTO.response.VerifyResponse;
import com.groupproject.libraryManagementSystem.model.userEntity.Otp;

public interface OtpService {
    public VerifyResponse otpIsValidated(String email, String otp);
    public Integer generateOtp(String email);

    public Otp getOtpDetailsByEmail(String email);
    void deleteOtpDetailsByEmail(String email);
}
