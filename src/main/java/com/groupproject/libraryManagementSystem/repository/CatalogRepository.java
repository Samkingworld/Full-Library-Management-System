package com.groupproject.libraryManagementSystem.repository;

import com.groupproject.libraryManagementSystem.model.catalogueEntity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    Catalog findByIsbn(String isbn);
    @Query(value = "SELECT * from catalog where id = :id", nativeQuery = true)
    Catalog  findBookById(@Param("id") Long id);
    void deleteByIsbn(String isbn);
    List<Catalog> findByAuthor(String author);
    Catalog findByTitle(String bookTitle);
    @Query(value = "SELECT * FROM catalog WHERE copies_available > 0", nativeQuery = true)
    List<Catalog> findAllWithCopiesAvailable();
}
