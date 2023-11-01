package com.groupproject.libraryManagementSystem.model.loanManagementEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loaned_Book")
public class LoanBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    private String user_email;

    @NotNull
    @NotEmpty
    private String book_title_1;

    private String book_title_2;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date date_borrowed;


    @NotNull
    @Temporal(TemporalType.DATE)
    private Date due_date;

    private Date date_returned;

    private double fine_for_late_return;

    @Enumerated(EnumType.STRING)
    private FineStatus fine_status;

    private boolean return_status;


    @Enumerated(EnumType.STRING)
    private UserNotified notified_user;


}
