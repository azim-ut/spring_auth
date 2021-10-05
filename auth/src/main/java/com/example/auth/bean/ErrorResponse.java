package com.example.auth.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse extends BaseResponse{
    private String message;

    public ErrorResponse(String message) {
        this.ok = false;
        this.message = message;
    }
}
