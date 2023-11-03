package com.groupproject.libraryManagementSystem.repository;

import com.groupproject.libraryManagementSystem.model.userEntity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Otp findByVerificationMail(String email);

    void deleteByVerificationMail(String email);


}
