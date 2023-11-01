package com.groupproject.libraryManagementSystem.dto.userDTO.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Data
public class UpdateUserRequest {


    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String address;

    private String favoriteWord;
}
