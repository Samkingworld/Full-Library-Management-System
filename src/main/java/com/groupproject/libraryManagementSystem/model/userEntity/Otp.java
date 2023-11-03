package com.groupproject.libraryManagementSystem.model.userEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "otp")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "v_email", unique = true)
    private String verificationMail;

    @NotNull
    @Column(name = "otp_value")
    private int otp;
    @NotNull
    @Column(name = "generated_time_date")
    private Date generatedTime;

    @NotNull
    @Column(name = "expired_date_time")
    private Date expiryTime;
}
