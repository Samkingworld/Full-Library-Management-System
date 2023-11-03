package com.groupproject.libraryManagementSystem.repository;

import com.groupproject.libraryManagementSystem.model.userEntity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByAuthority(String authority);
}
