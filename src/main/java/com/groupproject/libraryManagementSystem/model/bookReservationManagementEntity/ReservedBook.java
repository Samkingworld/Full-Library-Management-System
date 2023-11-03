package com.groupproject.libraryManagementSystem.model.bookReservationManagementEntity;

import com.groupproject.libraryManagementSystem.model.catalogueEntity.Catalog;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reserved_books")
public class ReservedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Catalog reservedBook;

    @NotNull
    @Column(name = "reserved_date")
    private LocalDateTime reservationDate;

    @Column(name = "expiry_date")
    private LocalDateTime reservationExpireDate;

    @NotNull
    @Column(name = "ready_for_pickup")
    private boolean isReadyForPickup;

}
