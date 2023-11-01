package com.groupproject.libraryManagementSystem.model.catalogueEntity;

import com.groupproject.libraryManagementSystem.model.bookReservationManagementEntity.ReservedBook;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "catalog")
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotNull
    @Column(unique = true)
    private String title;

    @NotEmpty
    @NotNull
    private String author;
    private String publisher;

    @NotEmpty
    @NotNull
    @Column(unique = true)
    private String isbn;

    @NotNull
    private int pub_year;

    @NotNull
    private int copies;

    @NotNull
    private int copies_out = 0;

    private int copies_available;

    @OneToMany(mappedBy = "reservedBook", fetch = FetchType.LAZY)
    private Set<ReservedBook> reservations;

    //methods
    public boolean isAvailable(){
        return copiesLeft() > 0 ? true : false;
    }
    public int copiesLeft(){
        return copies - copies_out;
    }
    public void setCopies_available1() {
        this.copies_available = copiesLeft();
    }

}
