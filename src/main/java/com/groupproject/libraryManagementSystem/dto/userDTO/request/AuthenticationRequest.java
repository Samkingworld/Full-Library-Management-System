package com.groupproject.libraryManagementSystem.dto.userDTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationRequest {
    @NotEmpty
    @NotNull
    @Email
    private String email;

    @NotEmpty
    @NotNull
    private String password;


}
