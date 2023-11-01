package com.groupproject.libraryManagementSystem.repository;

import com.groupproject.libraryManagementSystem.model.userEntity.Role;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    @Query(value = "SELECT MAX(CAST(SUBSTRING(membership_no, 9) AS SIGNED)) FROM user", nativeQuery = true)
    Integer findLastMembershipNo();
    List<User> findByAuthorities(Role role);
    void deleteByEmail(String email);

    @Query(value = "SELECT * FROM user where membership_no = :membershipNo", nativeQuery = true)
    User findByMembershipNo(@Param("membershipNo") String membershipNo);
    void deleteByMembershipNo(String membershipNo);

}
