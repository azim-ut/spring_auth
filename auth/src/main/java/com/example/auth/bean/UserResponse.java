package com.example.auth.bean;

import com.example.auth.entity.AppUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse extends RequestResponse{
    private String name;
    private String email;
    private Boolean enabled;

    public UserResponse(AppUser appUser) {
        this.name = appUser.getName();
        this.email = appUser.getEmail();
        this.enabled = appUser.isEnabled();
    }
}
