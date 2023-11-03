package com.groupproject.libraryManagementSystem.dto.userDTO.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequest {
    private String title;
    private String publisher;
    private String author;
    private int pub_year;
    private String isbn;
    private int copies;
}
