package com.example.client.provider;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtSettingsProvider {

    @Value("${auth.cookie.auth}")
    private String cookieAuthTokenName;
}
