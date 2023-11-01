package com.groupproject.libraryManagementSystem.controller;

import com.groupproject.libraryManagementSystem.dto.catalogueDTO.AddBookRequest;
import com.groupproject.libraryManagementSystem.dto.loanDTO.LoanBookRequest;
import com.groupproject.libraryManagementSystem.dto.loanDTO.PayFineRequest;
import com.groupproject.libraryManagementSystem.dto.loanDTO.ReturnBookRequest;
import com.groupproject.libraryManagementSystem.dto.reservedBookDTO.ReservedBookRequest;
import com.groupproject.libraryManagementSystem.dto.userDTO.request.UpdateBookRequest;
import com.groupproject.libraryManagementSystem.dto.userDTO.request.UpdateRoleRequest;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import com.groupproject.libraryManagementSystem.service.catalogService.CatalogService;
import com.groupproject.libraryManagementSystem.service.loanBookService.LoanBookService;
import com.groupproject.libraryManagementSystem.service.reservedBookService.ReservedBookService;
import com.groupproject.libraryManagementSystem.service.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    UserService userService;
    @Autowired
    LoanBookService loanBookService;
    @Autowired
    CatalogService catalogService;
    @Autowired
    ReservedBookService reservedBookService;



//    *************************** USERS **********************************
    @GetMapping("/users/all")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField
    ){
        Page<User> users = userService.getAllUsers(page, size, sortField);
        return ResponseEntity.ok(users);
    }


    @PutMapping("/update/role/{email}")
    public Map<String, Object> updateRole(@PathVariable String email, @RequestBody UpdateRoleRequest updateRole){
        Map<String, Object> userToUpdate = userService.updateRole(email, updateRole.getRole());
        return userToUpdate;
    }

    @DeleteMapping("/memberShipNo/{membershipNo}")
    public String deleteUserByUserId(@PathVariable String membershipNo){return userService.deleteByMembershipNo(membershipNo);}

    @DeleteMapping("/email/{email}")
    public String deleteUserByEmail(@PathVariable String email){return userService.deleteUserByEmail(email);}


    //    *************************** loanBooks **********************************

    @PostMapping("/loanBook-management/loan-book")
    Map<String, Object> loanBook (@RequestBody @Valid LoanBookRequest loanBookRequest){
        return loanBookService.loanBook(loanBookRequest);
    }

    @PostMapping("/loanBook-management/return-book")
    Map<String, Object> returnBook (@RequestBody @Valid ReturnBookRequest returnBook){
        return loanBookService.returnBook(returnBook);
    }

    @PostMapping("/loanBook-management/pay-fine")
    Map<String, Object> payFine (PayFineRequest payFineRequest){
        return loanBookService.payFine(payFineRequest);
    }


    //    *************************** CATALOGS **********************************

    @PostMapping("/catalog/book/add")
    public Object addBook(@RequestBody @Valid AddBookRequest book){return catalogService.addBook(book);}

    @PutMapping("/catalog/update/isbn/{book_ISBN}")
    public Object updateBookByIsbn(@PathVariable("book_ISBN") String bookIsbn, @RequestBody UpdateBookRequest bookRequest)
    {
        return catalogService.updateBookByIsbn(bookIsbn, bookRequest);
    }

    @PutMapping("/catalog/update/id/{book_ID}")
    public Object updateBookById(@PathVariable("book_ID") Long bookId, @RequestBody UpdateBookRequest bookRequest)
    {
        return catalogService.updateBookById(bookId, bookRequest);
    }

    @DeleteMapping("/catalog/delete/isbn/{book_ISBN}")
    public String deleteBookByIsbn(@PathVariable("book_ISBN") String bookIsbn){return catalogService.deleteBookByIsbn(bookIsbn);}

    @DeleteMapping("/catalog/delete/id/{book_ID}")
    public String deleteBookById(@PathVariable("book_ID") Long bookId){return catalogService.deleteBookById(bookId); }


    //    *************************** BOOK RESERVATION **********************************

    @PostMapping("/book-reservation/reserve")
    public Object reserveBook (@RequestBody @Valid ReservedBookRequest request){return reservedBookService.reserveBook(request);}


    @DeleteMapping("/book-reservation/delete/{reservationID}")
    public Object deleteReservedBook(@PathVariable("reservationID") Long reservationID){return reservedBookService.deleteReservedBook(reservationID);}

}
