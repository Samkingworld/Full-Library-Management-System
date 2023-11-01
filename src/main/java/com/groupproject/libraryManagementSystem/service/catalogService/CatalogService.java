package com.groupproject.libraryManagementSystem.service.catalogService;


import com.groupproject.libraryManagementSystem.dto.catalogueDTO.AddBookRequest;
import com.groupproject.libraryManagementSystem.dto.userDTO.request.UpdateBookRequest;
import com.groupproject.libraryManagementSystem.model.catalogueEntity.Catalog;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CatalogService {

    Page<Catalog> getAllBooks(int page, int size, String sortField);
    Object addBook(AddBookRequest request);
    Optional<Object> getBookById(Long id);
    Object getBookByIsbn(String isbn);
    Object updateBookByIsbn(String isbn, UpdateBookRequest updateBookRequest);
    Object updateBookById(Long id, UpdateBookRequest updateBookRequestForId);
    String deleteBookByIsbn(String isbn);
    String deleteBookById(Long id);
    List<Catalog> getBookByAuthor(String author);

    Catalog updateBook (Catalog catalog);

    Catalog getBookByTitle(String bookTitle1);
}
