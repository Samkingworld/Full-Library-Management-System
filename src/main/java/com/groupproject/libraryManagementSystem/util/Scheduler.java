package com.groupproject.libraryManagementSystem.util;


import com.groupproject.libraryManagementSystem.model.bookReservationManagementEntity.ReservedBook;
import com.groupproject.libraryManagementSystem.model.catalogueEntity.Catalog;
import com.groupproject.libraryManagementSystem.model.loanManagementEntity.LoanBook;
import com.groupproject.libraryManagementSystem.model.loanManagementEntity.UserNotified;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import com.groupproject.libraryManagementSystem.repository.CatalogRepository;
import com.groupproject.libraryManagementSystem.repository.LoanBookRepository;
import com.groupproject.libraryManagementSystem.repository.ReservedBookRepository;
import com.groupproject.libraryManagementSystem.service.catalogService.CatalogService;
import com.groupproject.libraryManagementSystem.service.userService.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class Scheduler {

    private final LoanBookRepository loanBookRepo;

    private final ReservedBookRepository reservedBookRepo;

    private final CatalogRepository catalogRepo;
    @Autowired
    UserService userService;
    @Autowired
    private EmailService sendmail;





    /**
     * This cron service send's a reminder email to the users that borrow books and having a day left to return
     * Validations:
     * 1. get all the loaned books whose due is = current Date + 1day && the returned status is FALSE && userNotified Status is NO
     * This will return a list. if the list is not empty,
     * for each item in the list, get the email, Book Title1, Book Title 2 and Due Date and save in a variable
     * prepare the message and attach the variables
     * send the message;
     * if the message is successful, for each item in the list, set the NotifiedUser Status to YES and save the record
     * if the message failed
     *
     * */

    @Scheduled(fixedRate = 14400000) //THis runs every 4 hours for 4 times in a day(1000ms*60s =1min*60min =1hr * 24/6 = 4hrs)
    public void loanBookReminderMessageScheduler(){
        log.info("INSIDE BOOK REMINDER CRON SERVICE");
        List<LoanBook> loanBookDue = loanBookRepo.findByDueDateAndNotifyStatus();
        System.out.println(loanBookDue);
        if (loanBookDue == null || !loanBookDue.isEmpty()) {
            for (int i = 0; i < loanBookDue.size(); i++) {
                LoanBook individual = loanBookDue.get(i);
                User user = userService.getUserByEmail(individual.getUser_email());


                /** Sending email**/
                log.info("Composing email to send USER DETAILS");
                String toEmail = individual.getUser_email();
                String subject = "REMINDER: BOOK DUE FOR RETURN";
                String body = "Hello " + user.getFirstName() + " " + user.getLastName() + ", " +
                        "\n The following Book will be due for return by tomorrow: " + individual.getDue_date() + "." +
                        "\n\nBook Details: " +
                        "\n\nBook1 - Title: \t" + individual.getBook_title_1() + "." +
                        "\nBook2 - Title: \t" + individual.getBook_title_2() + "." +
                        "\n\nPlease endeavour to return early as lateness attracts a fine. " +
                        "\n\nRegards" +
                        "\n\nMr Damilola\n" +
                        "LIBRARIAN";
                try {
                    log.info("SENDING REMINDER FOR RETURNING BORROWED BOOK TO " + user.getEmail());

                    sendmail.sendMail(toEmail, subject, body);
                    log.info("REMINDER SENT");
                    individual.setNotified_user(UserNotified.YES);
                    log.info("USER NOTIFICATION SET TO YES");

                }catch (Exception e){
                    log.info("NO");
                }

            }
        }
        log.info("*****NO RECORD OF LOAN BOOK DUE FOR RETURN IS FOUND...WILL CHECK AGAIN IN THE NEXT 4HRS*****");
    }



    // SCHEDULER - CRON SERVICE FOR SENDING MAIL TO USERS WHEN THE BOOK THEY RESERVED IS AVAILABLE

    @Scheduled(fixedRate = 1200000) // 20 minutes in milliseconds
    public void reservedBookAvailableReminder(){
        Sort sort = Sort.by(Sort.Direction.ASC, "reservationDate");
        List<ReservedBook> reservedBooks = reservedBookRepo.findAllNotReadyForPickupOrderedByReservationDate();
        log.info(reservedBooks.toString());
        if (!reservedBooks.isEmpty()){

            for (ReservedBook reservedBook : reservedBooks){
                Catalog book = catalogRepo.findBookById(reservedBook.getId());
                if (book.isAvailable()){
                    LocalDateTime currentTime = LocalDateTime.now().plusHours(1);
                    /** Sending email**/
                    log.info("Composing email to REMIND USER THAT BOOK IS NOW AVAILABLE");
                    String toEmail = reservedBook.getUser().getEmail();
                    String subject = "REMINDER: RESERVED BOOK IS NOW AVAILABLE";
                    String body = "Hello " + reservedBook.getUser().getFirstName() + " " + reservedBook.getUser().getLastName() + ", " +
                            "\n The Book you earlier reserved is now available. " +
                            "\n\nBook Details: " +
                            "\n\nBook - Title: \t" + reservedBook.getReservedBook().getTitle() + "." +
                            "\nBook - Author: \t" + reservedBook.getReservedBook().getAuthor() + "." +
                            "\nReservation Date: \t" + reservedBook.getReservationDate() + "." +
                            "\nReservation Expiry Date: \t" + currentTime + "." +
                            "\n\nPlease endeavour get the book in the next 2hrs else your reservation will be cancelled. " +
                            "\n\nRegards" +
                            "\n\nMr Damilola\n" +
                            "LIBRARIAN";
                    try {
                        log.info("SENDING REMINDER FOR RETURNING BORROWED BOOK TO " + reservedBook.getUser().getEmail());

                        sendmail.sendMail(toEmail, subject, body);
                        log.info("REMINDER SENT");
                        reservedBook.setReservationExpireDate(currentTime);
                        reservedBook.setReadyForPickup(true);
                        reservedBookRepo.save(reservedBook);

                    }catch (Exception e){
                        log.info("NOT SUCCESSFUL");
                    }
                }

            }

        }

    }

    // The task method will be executed every hour
    // This task delete from the DATABASE every reserved book that are due/expired already.
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void deleteReservedBookWithDueDatePassed(){
        log.info("FINDING LIST OF RESERVATIONS THAT ARE EXPIRED");
        List<ReservedBook> reservedBooks = reservedBookRepo.findAllReadyForPickupWithDueDatePassed();

        if (!reservedBooks.isEmpty()){
            log.info("DELETING EACH OF THE RESERVATION THAT IS EXPIRED");
            for(ReservedBook reservedBook : reservedBooks){
                reservedBookRepo.deleteById(reservedBook.getId());
                log.info("DELETED FOR " + reservedBook.getUser());
            }
        }
    }
}
