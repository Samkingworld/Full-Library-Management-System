package com.groupproject.libraryManagementSystem.repository;

import com.groupproject.libraryManagementSystem.model.loanManagementEntity.LoanBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanBookRepository extends JpaRepository<LoanBook, Long> {

    //LoanBook findByUser_email(String userEmail);

    @Query(value = "SELECT * FROM loaned_Book WHERE UPPER(fine_status) = UPPER('UNPAID') AND user_email = :userEmail", nativeQuery = true)
    LoanBook findByUser_emailWithUnpaidFine(@Param("userEmail") String userEmail);

    @Query(value = "SELECT * FROM loaned_Book WHERE due_date = CURRENT_DATE + 1 AND " +
            "(notified_user = 'NO' AND ( return_status IS NULL OR return_status = false))", nativeQuery = true)
    List<LoanBook> findByDueDateAndNotifyStatus();
}
