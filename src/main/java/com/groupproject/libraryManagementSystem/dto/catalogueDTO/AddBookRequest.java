package com.groupproject.libraryManagementSystem.dto.catalogueDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddBookRequest {
    @NotEmpty(message = "Book Title cannot be empty")
    @NotNull(message = "Book Title cannot be null")
    private String title;

    @NotEmpty(message = "Author name  cannot be empty")
    @NotNull(message = "Author name  cannot be null")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "entries must be Alphabets only")
    private String author;

    private String publisher;

    @NotEmpty(message = "ISBN cannot be empty")
    @NotNull(message = "ISBN cannot be null")
    private String isbn;

    @NotNull(message = "publication year cannot be null")
    private int pub_year;

    @NotNull(message = "copies cannot be null")
    private int copies;
}
