package com.groupproject.libraryManagementSystem.repository;

import com.groupproject.libraryManagementSystem.model.bookReservationManagementEntity.ReservedBook;
import com.groupproject.libraryManagementSystem.model.catalogueEntity.Catalog;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservedBookRepository extends JpaRepository<ReservedBook, Long> {
    List<ReservedBook> findByUser(User user);
    List<ReservedBookRepository> findByReservedBook(Catalog book);
    List<ReservedBookRepository> findByReservationDate(LocalDate reservationDate);
    List<ReservedBookRepository> findByIsReadyForPickup(boolean isReadyForPickup);

    @Query(value = "SELECT * FROM reserved_books WHERE ready_for_pickup = false ORDER BY reserved_date ASC", nativeQuery = true)
    List<ReservedBook> findAllNotReadyForPickupOrderedByReservationDate();

    @Query(value = "SELECT * FROM reserved_books WHERE ready_for_pickup = true AND expiry_date < NOW()", nativeQuery = true)
    List<ReservedBook> findAllReadyForPickupWithDueDatePassed();


}
