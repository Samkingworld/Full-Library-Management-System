package com.groupproject.libraryManagementSystem.controller;

import com.groupproject.libraryManagementSystem.model.catalogueEntity.Catalog;
import com.groupproject.libraryManagementSystem.service.catalogService.CatalogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    @Autowired
    CatalogService catalogService;



    @GetMapping(value = "/all-books")
    public ResponseEntity<Page<Catalog>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortField
    ){
        Page<Catalog> catalogs = catalogService.getAllBooks(page, size, sortField);
        return ResponseEntity.ok(catalogs);
    }

    @GetMapping("/book/isbn/{book_ISBN}")
    public Object getBookByIsbn(@PathVariable("book_ISBN") String isbn){return catalogService.getBookByIsbn(isbn);}

    @GetMapping("/book/id/{book_ID}")
    public Optional<Object> getBookById(@PathVariable("book_ID") Long bookId){return catalogService.getBookById(bookId);}
    @GetMapping("/book/author/{book_Author}")
    public List<Catalog> getBookByAuthor(@PathVariable("book_Author") String author){return catalogService.getBookByAuthor(author);}
    @GetMapping("/book/title/{book_Title}")
    public Catalog getBookByTitle(@PathVariable("book_Title") String bookTitle) {
        return  catalogService.getBookByTitle(bookTitle);
    }

}
