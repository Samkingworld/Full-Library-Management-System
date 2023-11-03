package com.groupproject.libraryManagementSystem.dto.userDTO.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class RegisterRequest {
    @NotEmpty(message = "first name cannot be empty")
    @NotNull(message = "first name cannot be null")
    @Pattern(regexp = "[A-Z a-z]{3,30}", message = "first name must be combination of alphabetical characters")
    private String firstName;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    @Pattern(regexp = "[A-Z a-z]{3,30}", message = "must be combination of alphabetical characters")
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Pattern(regexp = "[0-9]{11}", message = "must be a number")
    private String phoneNumber;

    @NotEmpty
    private String address;

    @NotNull
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "please use this format dd/mm/yyyy  e.g. 23/03/2023")
//    @Temporal(TemporalType.DATE)
//    @DateTimeFormat(pattern = "^\\d{2}/\\d{2}/\\d{4}$")
    private String dob;

    private String favoriteWord;

    @NotNull
    @Length(min = 8, message = "must be equals or greater than 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password" +
            "must contain at least 1 each: capital letter, small letter, number and symbol ")
    private String password;
}
