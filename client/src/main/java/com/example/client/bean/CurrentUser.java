package com.example.client.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CurrentUser {
    private String name;
    private String email;
    private boolean enabled;
}
