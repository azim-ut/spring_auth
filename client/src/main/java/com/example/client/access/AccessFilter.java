package com.example.client.access;

import com.example.client.bean.CurrentUser;
import com.example.client.provider.JwtSettingsProvider;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Slf4j
@Order(1)
@AllArgsConstructor
public class AccessFilter implements Filter {

    private final JwtSettingsProvider jwtSettingsProvider;
    private final CurrentUserProvider currentUserProvider;
    private final Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest res = (HttpServletRequest) servletRequest;
        Cookie[] cookies = res.getCookies();
        CurrentUser currentUser = new CurrentUser();
        if (cookies != null) {
            String authToken = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtSettingsProvider.getCookieAuthTokenName())) {
                    authToken = cookie.getValue();
                }
            }

            if (authToken != null && !authToken.isEmpty()) {
                currentUser = fetchRemoteUser(res);
            }

        }

        currentUserProvider.set(currentUser);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private CurrentUser fetchRemoteUser(HttpServletRequest res) {
        try {
            URL url = new URL("http://localhost:8088/api/v1/auth/current");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(1000);
            connection.setRequestProperty("Cookie", res.getHeader("Cookie"));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            connection.disconnect();

            return gson.fromJson(content.toString(), CurrentUser.class);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
        return new CurrentUser();
    }
}
