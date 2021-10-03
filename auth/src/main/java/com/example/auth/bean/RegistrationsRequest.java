package com.example.auth.bean;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationsRequest {
    private final String name;
    private final String email;
    private final String password;
    private final Boolean checked;
}
