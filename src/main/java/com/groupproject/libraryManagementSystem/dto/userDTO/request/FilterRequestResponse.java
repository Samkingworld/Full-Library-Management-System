package com.groupproject.libraryManagementSystem.dto.userDTO.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilterRequestResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
}
