package com.groupproject.libraryManagementSystem.dto.userDTO.response;

import lombok.Data;

@Data
public class AddBookResponse {
    private Long id;

    private String title;

    private String author;

    private String publisher;

    private String isbn;

    private int pub_year;

    private int copies;
}
