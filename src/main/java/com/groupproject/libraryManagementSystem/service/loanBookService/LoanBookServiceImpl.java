package com.groupproject.libraryManagementSystem.service.loanBookService;

import com.groupproject.libraryManagementSystem.dto.CustomResponseDto;
import com.groupproject.libraryManagementSystem.dto.loanDTO.LoanBookRequest;
import com.groupproject.libraryManagementSystem.dto.loanDTO.PayFineRequest;
import com.groupproject.libraryManagementSystem.dto.loanDTO.ReturnBookRequest;
import com.groupproject.libraryManagementSystem.model.catalogueEntity.Catalog;
import com.groupproject.libraryManagementSystem.model.loanManagementEntity.FineStatus;
import com.groupproject.libraryManagementSystem.model.loanManagementEntity.LoanBook;
import com.groupproject.libraryManagementSystem.model.loanManagementEntity.UserNotified;
import com.groupproject.libraryManagementSystem.model.userEntity.Status;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import com.groupproject.libraryManagementSystem.repository.LoanBookRepository;
import com.groupproject.libraryManagementSystem.service.catalogService.CatalogService;
import com.groupproject.libraryManagementSystem.service.userService.UserService;
import com.groupproject.libraryManagementSystem.util.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoanBookServiceImpl implements LoanBookService{

    private final LoanBookRepository loanBookRepo;
    @Autowired
    UserService userService;
    @Autowired
    CatalogService catalogService;

    @Autowired
    private EmailService sendmail;

    CustomResponseDto custom = new CustomResponseDto();
    String otherDetails = "otherDetails";

    private static final double FINE_RATE_PER_DAY = 100.0;

/**
 * Receive request to borrow book return a map
 *
 * Validations
 * •	check if the user is a registered user. If not registered return error else continue
 *      check if the return_date_in_days passed from request is greater than 5. if greater return error.
 * •	check if the user account has been verified.
 * •	check if the user has a yet to return book by checking the status of books he has borrowed. If true then error else continue
 * •	check if user has any fine record yet to be paid.
 * •	check if the book to be borrowed exist in the Catalog Library by finding the book by title
 * •	if exist, check if it is available to be borrowed if available
 * •	then set the borrowed date
 * •	set the due date TO (Borrowed Date + 5days)
 * •	set notified to NO
 * •	set returned status to  false
 * •	Catalog – Increase Copies out + 1
 * •	Save both Catalog and LoanBook
 * */
    @Override
    public Map<String, Object> loanBook(LoanBookRequest loanBookRequest) {
        Map<String, Object> response = new HashMap<>();
        Catalog catalog1 = catalogService.getBookByTitle(loanBookRequest.getBook_title_1());
        Catalog catalog2;
        User user = userService.getUserByEmail(loanBookRequest.getUser_email());
        if (user == null) {
            custom.setMessage("User with this email " + loanBookRequest.getUser_email() + " does not exist");
            response.put(otherDetails, custom);
            return response;
        }
        if (loanBookRequest.getReturn_date_in_days()< 1){
            response.put("return_date_error", "Number of day book will be borrowed cannot be blank.");
            response.put("message", "Failed");
            response.put("status", "99");
            return response;
        }
        if (loanBookRequest.getReturn_date_in_days() > 5){
            response.put("return_date_error", "Number of day book will be borrowed cannot be greater than 5.");
            response.put("message", "Failed");
            response.put("status", "99");
            return response;
        }

        if(user.getStatus() == Status.PENDING){
            custom.setMessage("User Account is not Verified. Please verify your account!");
            response.put(otherDetails, custom);
            return response;
        }
        LoanBook borrowedBook = loanBookRepo.findByUser_emailWithUnpaidFine(loanBookRequest.getUser_email());

        if(borrowedBook != null){
            if (!borrowedBook.isReturn_status()){
                custom.setMessage("You can't borrow at this time. Please return the previous one you borrowed");
                response.put(otherDetails, custom);
                return response;
            }
            if (borrowedBook.getFine_for_late_return() > 0 && borrowedBook.getFine_status() == FineStatus.UNPAID){
                custom.setMessage("You can't borrow book at this time. Pay your previous fine of N" +
                        borrowedBook.getFine_for_late_return() + ".");
                response.put(otherDetails, custom);
                return response;
            }
        }
        if(catalog1 == null){
            custom.setMessage("This book with the title: " + loanBookRequest.getBook_title_1() + " does not exist");
            response.put(otherDetails, custom);
            return response;
        }

        if (!catalog1.isAvailable()){
            response.put("error_book1", "This book with the title: " + loanBookRequest.getBook_title_2() + " is not available for borrowing");
        }

        if (loanBookRequest.getBook_title_2() != null && !loanBookRequest.getBook_title_2().isEmpty()){
            catalog2 = catalogService.getBookByTitle(loanBookRequest.getBook_title_2());
            if (catalog2 == null){
                custom.setMessage("This book with the title: " + loanBookRequest.getBook_title_2() + " does not exist");
                response.put(otherDetails, custom);
                return response;
            }
            if (!catalog2.isAvailable()){
                response.put("error_book2", "This book with the title: " + loanBookRequest.getBook_title_2() + " is not available for borrowing");
            }

            catalog2.setCopies_out(catalog2.getCopies_out() + 1);
            catalog2.setCopies_available1();
            catalogService.updateBook(catalog2);

        }


        if (response.containsKey("error_book1") || response.containsKey("error_book2")) {
            return response;
        }
        catalog1.setCopies_out(catalog1.getCopies_out() + 1);
        catalog1.setCopies_available1();
        catalogService.updateBook(catalog1);

        LoanBook newLoanBook = new LoanBook();
        newLoanBook.setBook_title_1(loanBookRequest.getBook_title_1());
        newLoanBook.setBook_title_2(loanBookRequest.getBook_title_2() != null ? loanBookRequest.getBook_title_2() : null);
        newLoanBook.setUser_email(loanBookRequest.getUser_email());
        Date loanDate = new Date();
        newLoanBook.setDate_borrowed(loanDate);
        newLoanBook.setDue_date(dueDate(loanDate, loanBookRequest.getReturn_date_in_days()));
        newLoanBook.setNotified_user(UserNotified.NO);
        newLoanBook.setReturn_status(false);
        newLoanBook.setFine_status(FineStatus.UNPAID);
        loanBookRepo.save(newLoanBook);
        custom.setStatus("00");
        custom.setMessage("Book Borrowed Successfully. Do ensure to return before the due date as failure to do so attract a fine.");
        response.put(otherDetails, custom);
        response.put("loan_details", newLoanBook);

        return response;
    }

    /**
     *
     Return Book
     Validations
     •	check if the user is a registered user. If not registered return error else continue
     •	get user’s information from borrowedBook table
     •	get book details from Catalog for each borrowedBook
     •	check if the title of the book in borrowedBook is what the user brought back
     •	check if the ISBN on the book returned from request is the same with the Book from Catalog
     •	check if the date of returned is before the returned date or after?
     •	If after, then call fine calculation and calculate the fine (fine is N100 per day). Else, skip and continue.
     o	Update copies_out in Catalog to -1
     o	Set returned_status in LoanMgt Table to True;
     o	save the fine to LoanBook Table for customer and set Paid to NO. return a custom message.
     •	Update copies_out in Catalog to -1
     •	Set returned_status in LoanMgt Table to True.

     * */
    @Override
    public Map<String, Object> returnBook(ReturnBookRequest returnBook) {
        Map<String, Object> response = new HashMap<>();
        Date returnedDate = new Date();
        Catalog book1 = catalogService.getBookByTitle(returnBook.getBook_title_1());
        Catalog book2;
        User user = userService.getUserByEmail(returnBook.getUser_email());
        if (user == null) {
            custom.setMessage("User with this email " + returnBook.getUser_email() + " does not exist");
            response.put(otherDetails, custom);
            return response;
        }
        LoanBook borrowedBook = loanBookRepo.findByUser_emailWithUnpaidFine(returnBook.getUser_email());

        if(borrowedBook == null){
            custom.setMessage("No UNPAID Loan details exist for this user");
            response.put(otherDetails, custom);
            return response;
        }

        if (book1 == null) {
            custom.setMessage("Book with this title " + returnBook.getBook_title_1() + " does not exist");
            response.put("error_details_book1", custom);
        }
        if (!book1.getIsbn().equalsIgnoreCase(returnBook.getBook1_ISBN())){
            custom.setMessage("The ISBN of the Book Borrowed - " + book1.getIsbn() + " is different from the ISBN of the Book returned - " + returnBook.getBook1_ISBN() + ".");
            response.put("error_book1", custom);

        }

        if (returnBook.getBook_title_2() != null){
            book2 = catalogService.getBookByTitle(returnBook.getBook_title_2());
            if (book2 == null) {
                custom.setMessage("Book with this title " + returnBook.getBook_title_2() + " does not exist");
                response.put("error_details_book2", custom);
            }
            if (book2.getIsbn() != returnBook.getBook2_ISBN()){
                custom.setMessage("The ISBN of the Book Borrowed - " + book2.getIsbn() + " is different from the ISBN of the Book returned - " + returnBook.getBook2_ISBN() + ".");
                response.put("error_book1", custom);
            }
            book2.setCopies_out(book2.getCopies_out() - 1);
            book2.setCopies_available(book2.getCopies_available() + 1);
            catalogService.updateBook(book2);

        }

        if (response.containsKey("error_details_book1") || response.containsKey("error_details_book2")){
            return response;
        } else if (response.containsKey("error_book1") || response.containsKey("error_book2")){
            return response;
        }

        book1.setCopies_out(book1.getCopies_out() - 1);
        book1.setCopies_available(book1.getCopies_available() + 1);
        catalogService.updateBook(book1);

        double fine = calculateFine(returnedDate, borrowedBook.getDue_date());
        if (fine > 0){
            borrowedBook.setFine_for_late_return(fine);
            borrowedBook.setFine_status(FineStatus.UNPAID);
            custom.setMessage("Book returned late. Fine calculated: " + fine + ". Please pay your fine else you will not be allowed to borrow book again");
            custom.setStatus("55");//incomplete Transaction - (55), while (99) - Failed Transaction, (00) is Success.
            response.put(otherDetails, custom);

        }

        borrowedBook.setReturn_status(true);
        borrowedBook.setDate_returned(returnedDate);
        borrowedBook.setFine_status(FineStatus.PAID);
        loanBookRepo.save(borrowedBook);
        response.put("borrowed_book_details", borrowedBook);
        custom.setMessage("Book successfully returned and timely");
        custom.setStatus("00");//incomplete Transaction - (55), while (99) - Failed Transaction, (00) is Success.
        response.put(otherDetails, custom);

        return response;
    }

    @Override
    public Map<String, Object> payFine(PayFineRequest payFineRequest) {
        Map<String, Object> response = new HashMap<>();
        LoanBook loanBook = loanBookRepo.findByUser_emailWithUnpaidFine(payFineRequest.getUser_email());

        if (loanBook == null){
            custom.setMessage("Book Loan details with the  email " + payFineRequest.getUser_email() + " for an UNPAID FINE does not exist");
            response.put(otherDetails, custom);
            return response;
        }
        if (loanBook.getFine_for_late_return() != payFineRequest.getFine_amount_Naira()){
            custom.setMessage("You are to pay the exact fine! The amount of fine to be paid is - N" + loanBook.getFine_for_late_return() + " and not - N" + payFineRequest.getFine_amount_Naira() + ".");
            response.put("error_Details", custom);
            return response;
        }

        loanBook.setFine_status(FineStatus.PAID);
        loanBookRepo.save(loanBook);
        custom.setStatus("00");
        custom.setMessage("Payment Successful! - Fine Paid.");
        response.put(otherDetails, custom);
        response.put("payment_details", loanBook);

        return response;
    }

    //Other Methods

    public Date dueDate(Date borrowedDate, int num_of_days) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(borrowedDate);

        // Add 5 days to the borrowed date
        calendar.add(Calendar.DAY_OF_MONTH, num_of_days);

        // Get the due date as a Date object
        Date dueDate = calendar.getTime();

        return dueDate;
    }

    public double calculateFine(Date returnDate, Date dueDate) {
        long timeDifferenceMillis = returnDate.getTime() - dueDate.getTime();
        long daysLate = timeDifferenceMillis / (24 * 60 * 60 * 1000); // Convert milliseconds to days

        if (daysLate <= 0) {
            return 0.0; // No fine if returned on or before the due date
        } else {
            double fine = daysLate * FINE_RATE_PER_DAY;
            return fine;
        }
    }

}



