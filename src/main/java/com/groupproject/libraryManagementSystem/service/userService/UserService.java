package com.groupproject.libraryManagementSystem.service.userService;

import com.groupproject.libraryManagementSystem.dto.userDTO.request.*;
import com.groupproject.libraryManagementSystem.dto.userDTO.response.AuthenticationResponse;
import com.groupproject.libraryManagementSystem.dto.userDTO.response.VerifyResponse;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface UserService {
    public String registerUser(RegisterRequest registerRequest);
    public VerifyResponse verifyUser(VerifyUserRequest verifyUserRequest);

    public String deleteUserByEmail(String email);

    @Transactional()
    @CacheEvict(value = {"allUsers", "getUserByEmail", "getUserByUserId"})
    String deleteByMembershipNo(String membershipNo);

    String resendOtp(String email);

    public Page<User> getAllUsers(int page, int size, String sortField);

    public User getUserByEmail(String email);
//    public User updateUser( String user_id, UpdateRequest updateRequest);


    @Cacheable(value = "getUserByMembershipNo", key = "#membershipNo")
    User getUserByMembershipNo(String membershipNo);

    User updateUser(String email, UpdateUserRequest updateRequest);

    Map<String, Object> updateRole(String email, String role);

    Object forgotPassword (String email);

    Object verifyPasswordInfo(ForgotPasswordRequest passwordRequest);
    AuthenticationResponse signIn(AuthenticationRequest authRequest);
}
