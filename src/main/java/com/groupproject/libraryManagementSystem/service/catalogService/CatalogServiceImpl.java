package com.groupproject.libraryManagementSystem.service.catalogService;

import com.groupproject.libraryManagementSystem.dto.catalogueDTO.AddBookRequest;
import com.groupproject.libraryManagementSystem.dto.userDTO.request.UpdateBookRequest;
import com.groupproject.libraryManagementSystem.model.catalogueEntity.Catalog;
import com.groupproject.libraryManagementSystem.repository.CatalogRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class CatalogServiceImpl implements CatalogService{
    @Autowired
    private CatalogRepository catalogRepository;

    @Override
    public Object addBook(AddBookRequest request) {
        Map<String, Object> newResponse = new HashMap<>();
        Catalog findBook = catalogRepository.findByIsbn(request.getIsbn());
        Catalog searchByTitle = catalogRepository.findByTitle(request.getTitle());
        if(findBook != null) {
            newResponse.put("error", "Book with the supplied ISBN already exist!");
            return newResponse;
        }
        if(searchByTitle != null){
            newResponse.put("error", "Book with the supplied Title already exist!");
            return newResponse;
        }

        Catalog newBook = new Catalog();
        newBook.setTitle(request.getTitle());
        newBook.setAuthor(request.getAuthor());
        newBook.setCopies(request.getCopies());
        newBook.setPub_year(request.getPub_year());
        newBook.setIsbn(request.getIsbn());
        newBook.setCopies_available1();
        newBook.setPublisher(request.getPublisher());

        return catalogRepository.save(newBook);
    }

    @Override
    public Page<Catalog> getAllBooks(int page, int size, String sortField) {
        Sort sort = Sort.by(sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        return catalogRepository.findAll(pageable);
    }

    @Override
    public Optional<Object> getBookById(Long id) {
        Optional<Catalog> findCatalog = catalogRepository.findById(id);
        if(!findCatalog.isPresent()){
            Map<String, Object> newResponse = new HashMap<>();
            newResponse.put("error", "Book with this Id does not exist");
            return Optional.of(newResponse);
        }
        return Optional.of(findCatalog);
    }

    @Override
    public Object getBookByIsbn(String isbn) {
        Catalog findCatalog = catalogRepository.findByIsbn(isbn);
        if(findCatalog == null){
            Map<String, Object> newResponse = new HashMap<>();
            newResponse.put("error", "Book with this ISBN does not exist");
            return newResponse;
        }
        return findCatalog;
    }

    @Override
    public Object updateBookByIsbn(String isbn, UpdateBookRequest request) {
        Map<String, Object> newResponse = new HashMap<>();
        Catalog bookToUpdate = catalogRepository.findByIsbn(isbn);
        if (bookToUpdate == null){
            newResponse.put("error", "Book with this ISBN does not exist");
            return newResponse;
        }

        if (request.getAuthor() != null && !request.getAuthor().isEmpty()) {
            bookToUpdate.setAuthor(request.getAuthor());
        }
        if (request.getPublisher() != null && !request.getPublisher().isEmpty()) {
            bookToUpdate.setPublisher(request.getPublisher());
        }

        if ( request.getCopies() > 0) {
            bookToUpdate.setCopies(request.getCopies() + bookToUpdate.getCopies());
        }

        if (request.getPub_year() > 0) {
            bookToUpdate.setPub_year(request.getPub_year());
        }

        catalogRepository.save(bookToUpdate);
        return bookToUpdate;
    }

    @Override
    public Object updateBookById(Long id, UpdateBookRequest updateBookRequest) {
        Map<String, Object> newResponse = new HashMap<>();
        Catalog bookToUpdate = catalogRepository.findBookById(id);
        if (bookToUpdate == null){
            newResponse.put("error", "Book with this ID does not exist");
            return newResponse;
        }

        log.info("checking first condition Author because RESULT FROM findBookByID = " + bookToUpdate);

        bookToUpdate.setTitle((updateBookRequest.getTitle() != null
                && !updateBookRequest.getTitle().isEmpty())
                ? updateBookRequest.getTitle() : bookToUpdate.getTitle());

        bookToUpdate.setIsbn((updateBookRequest.getIsbn() != null
                && !updateBookRequest.getIsbn().isEmpty())
                ? updateBookRequest.getIsbn() : bookToUpdate.getIsbn());

        if (updateBookRequest.getAuthor() != null && !updateBookRequest.getAuthor().isEmpty()) {
            log.info("Setting Author to what is passed from Request");

            bookToUpdate.setAuthor(updateBookRequest.getAuthor());

            log.info("Author changed to new request " + bookToUpdate.getAuthor());
        }
        if (updateBookRequest.getPublisher() != null && !updateBookRequest.getPublisher().isEmpty()) {
            bookToUpdate.setPublisher(updateBookRequest.getPublisher());

            log.info("Publisher changed to new request " + bookToUpdate.getPublisher());
        }

        if ( updateBookRequest.getCopies() > 0) {
            bookToUpdate.setCopies(updateBookRequest.getCopies() + bookToUpdate.getCopies());

            log.info("Book Copies changed to new request " + bookToUpdate.getCopies());
        }

        if (updateBookRequest.getPub_year() > 0) {
            bookToUpdate.setPub_year(updateBookRequest.getPub_year());
            log.info("Publication Year changed to new request " + bookToUpdate.getPub_year());
        }
        log.info("saving changes " + bookToUpdate.getAuthor());
        catalogRepository.save(bookToUpdate);
        log.info("changes saved " + bookToUpdate);

        return bookToUpdate;

    }

    @Override
    public String deleteBookByIsbn(String isbn) {
        Catalog findBook = catalogRepository.findByIsbn(isbn);
        if (findBook == null){
            return"Book with the ISBN: " + isbn + " does not exist";
        }
        catalogRepository.deleteByIsbn(isbn);
        return "Book with ISBN: " + isbn + " is successfully deleted";
    }

    @Override
    public String deleteBookById(Long id) {
        Catalog findBook = catalogRepository.findBookById(id);
        if (findBook == null){
            return "Book with the ID NO: " + id + " does not exist";
        }
        catalogRepository.deleteById(id);
        return "Book with ID NO: " + id + " is successfully deleted";
    }

    @Override
    public List<Catalog> getBookByAuthor(String author) {
        return catalogRepository.findByAuthor(author);
    }

    @Override
    public Catalog updateBook(Catalog catalog) {
        return catalogRepository.save(catalog);
    }

    @Override
    public Catalog getBookByTitle(String bookTitle) {

        return catalogRepository.findByTitle(bookTitle);
    }
}
