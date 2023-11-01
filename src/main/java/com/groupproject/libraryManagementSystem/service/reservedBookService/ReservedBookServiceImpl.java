package com.groupproject.libraryManagementSystem.service.reservedBookService;

import com.groupproject.libraryManagementSystem.dto.CustomResponseDto;
import com.groupproject.libraryManagementSystem.dto.reservedBookDTO.ReservedBookRequest;
import com.groupproject.libraryManagementSystem.model.bookReservationManagementEntity.ReservedBook;
import com.groupproject.libraryManagementSystem.model.catalogueEntity.Catalog;
import com.groupproject.libraryManagementSystem.model.loanManagementEntity.FineStatus;
import com.groupproject.libraryManagementSystem.model.loanManagementEntity.LoanBook;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import com.groupproject.libraryManagementSystem.repository.CatalogRepository;
import com.groupproject.libraryManagementSystem.repository.LoanBookRepository;
import com.groupproject.libraryManagementSystem.repository.ReservedBookRepository;
import com.groupproject.libraryManagementSystem.service.userService.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReservedBookServiceImpl implements ReservedBookService {

    private final ReservedBookRepository reservedBookRepo;

    private final LoanBookRepository loanBookRepo;

    private final CatalogRepository catalogRepo;
    @Autowired
    private UserService userService;

    CustomResponseDto custom = new CustomResponseDto();
    String otherDetails = "otherDetails";


    /*
    * check if the user exist
    * check if the user is verified
    * check if book exist
    * check if user has borrowed book before that is yet to return or is owing a fine
    * check if user with same book already exist in the reserved book table
    * check if the number of reserved book by user is less than <2. if yes then user can reserve else return error
    * if all validation passed,
    * set reservation date to today
    * if book is available, set isReadyForPickup to true else set IsReadyForPickup to false
    * set the book field to book object
    * set the user  field to user object
    * save reservedBook
    * */

    @Override
    public Object reserveBook (ReservedBookRequest request){
        Map<String, Object> response = new HashMap<>();
        User user = userService.getUserByEmail(request.getUserEmail());
        Catalog book = catalogRepo.findBookById(request.getBookId());
        LoanBook loanBook = loanBookRepo.findByUser_emailWithUnpaidFine(request.getUserEmail());

        if (user == null){
            custom.setMessage("User with this email " + request.getUserEmail() + " does not exist");
            response.put(otherDetails, custom);
            return response;
        }

        if (book == null){
            custom.setMessage("Book with this ID " + request.getBookId() + " does not exist");
            response.put(otherDetails, custom);
            return response;
        }

        if(loanBook != null) {
            if (!loanBook.isReturn_status()) {
                custom.setMessage("You can't reserve book at this time. Please return the previous one you borrowed");
                response.put(otherDetails, custom);
                return response;
            }

            if (loanBook.getFine_for_late_return() > 0 && loanBook.getFine_status() == FineStatus.UNPAID) {
                custom.setMessage("You can't make any book reservation at this time. Pay your previous fine of N" +
                        loanBook.getFine_for_late_return() + ".");
                response.put(otherDetails, custom);
                return response;
            }
        }

        List<ReservedBook> reservedBook = reservedBookRepo.findByUser(user);

        if (reservedBook.size() >=2){
            custom.setMessage("You can't make book reservation at this time. Maximum reservation reached for this user.");
            response.put(otherDetails, custom);
            return response;
        }

        if (book.isAvailable()) {
            custom.setMessage("You can't reserve this book as it is available in the Library.");
            response.put(otherDetails, custom);
            return response;
        }
        LocalDateTime reservedDate = LocalDateTime.now();

        ReservedBook reservation = new ReservedBook();

        reservation.setReservedBook(book);
        reservation.setReservationDate(reservedDate);
        reservation.setUser(user);
        custom.setMessage("Book successfully reserved. You will receive an email when book is available");
        custom.setStatus("00");
        response.put("reservation_details", reservedBookRepo.save(reservation));
        response.put(otherDetails, custom);

        return response;

    }

    @Override
    public Object deleteReservedBook(Long reservationID){
        Map<String, Object> response = new HashMap<>();
        Optional<ReservedBook> reservedBook = reservedBookRepo.findById(reservationID);

        if (reservedBook.isEmpty()){
            custom.setMessage("No ReservedBook Found with this ID");
            response.put(otherDetails, custom);
            return response;
        }

        reservedBookRepo.deleteById(reservationID);
        custom.setMessage("Reservation for Book ID - " + reservationID + "  have been successfully deleted.");
        custom.setStatus("00");
        response.put(otherDetails, custom);
        return response;
    }



    //Create a scheduler to always check every 30seconds if book is available if available,
    // if available, get all the reservedbook for that book and order by date and time of reservation.
    //
    // send the first user


}
