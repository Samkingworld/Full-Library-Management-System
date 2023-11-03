package com.groupproject.libraryManagementSystem.controller;

import com.groupproject.libraryManagementSystem.dto.userDTO.request.AuthenticationRequest;
import com.groupproject.libraryManagementSystem.dto.userDTO.request.ForgotPasswordRequest;
import com.groupproject.libraryManagementSystem.dto.userDTO.request.RegisterRequest;
import com.groupproject.libraryManagementSystem.dto.userDTO.request.VerifyUserRequest;
import com.groupproject.libraryManagementSystem.dto.userDTO.response.AuthenticationResponse;
import com.groupproject.libraryManagementSystem.dto.userDTO.response.VerifyResponse;
import com.groupproject.libraryManagementSystem.service.userService.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    UserService userService;

    @PostMapping("/signup")
    public String registerUser(@RequestBody @Valid RegisterRequest request){
        String result = userService.registerUser(request);
        return result;
    }

    @PostMapping("/verify-user")
    public VerifyResponse verifyOtp (@RequestBody @Valid VerifyUserRequest verifyRequest){
        return userService.verifyUser(verifyRequest);

    }

    @PostMapping("/signin")
    public AuthenticationResponse signIn(@RequestBody @Valid AuthenticationRequest authRequest){
        return userService.signIn(authRequest);
    }

    @PostMapping("/forgot-password")
    public Object forgotPassword(String email){
        return userService.forgotPassword(email);
    }

    @PostMapping("/verify-password-change")
    public Object verifyPasswordInfo(@RequestBody @Valid ForgotPasswordRequest passwordRequest){
        return userService.verifyPasswordInfo(passwordRequest);
    }

    @PostMapping("/resend-otp/{email}")
    public String resendOtp(@PathVariable("email") String email){
        return userService.resendOtp(email);
    }

}
