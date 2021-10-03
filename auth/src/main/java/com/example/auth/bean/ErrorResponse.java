package com.example.auth.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse extends RequestResponse {
    private String message;

    public ErrorResponse(String message) {
        this.ok = false;
        this.message = message;
    }
}
