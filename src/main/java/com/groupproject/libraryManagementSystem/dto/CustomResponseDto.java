package com.groupproject.libraryManagementSystem.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomResponseDto {
    private String message;
    private String status="99";


    public void setStatus(String status) {
        if (status == null || status.isEmpty()){
            this.status = "99";
        }
        this.status = status;

    }
}
