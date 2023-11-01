package com.groupproject.libraryManagementSystem.dto.userDTO.response;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String token;
}