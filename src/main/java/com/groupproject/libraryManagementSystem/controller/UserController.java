package com.groupproject.libraryManagementSystem.controller;

import com.groupproject.libraryManagementSystem.dto.userDTO.request.*;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import com.groupproject.libraryManagementSystem.service.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;



    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email){
        return userService.getUserByEmail(email);
    }

    @GetMapping("/membershipNo")
    public User getUserByUserId(@RequestParam(name = "membershipID") String membershipNo){
        try {
            return userService.getUserByMembershipNo(membershipNo);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new User();

        }
    }

    @PutMapping("/update/{email}")
    public Map<String, Object> updateUser(@PathVariable String email, @RequestBody @Valid UpdateUserRequest updateRequest){
        Map<String, Object> response = new HashMap<>();
        User userToUpdate = userService.updateUser(email, updateRequest);
        response.put("newUserDetails", userToUpdate);
        return response;
    }



}
