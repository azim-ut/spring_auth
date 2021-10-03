package com.example.auth.controller;

import com.example.auth.access.JwtTokenProvider;
import com.example.auth.bean.ErrorResponse;
import com.example.auth.bean.LoginRequest;
import com.example.auth.bean.RegistrationsRequest;
import com.example.auth.bean.UserResponse;
import com.example.auth.entity.AppUser;
import com.example.auth.service.LoginService;
import com.example.auth.service.RegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "api/v1/auth")
@ResponseBody
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(path = "registration")
    public ResponseEntity<?> registration(@RequestBody RegistrationsRequest registrationsRequest, HttpServletResponse response) {
        try {
            AppUser appUser = registrationService.register(registrationsRequest);
            setAuthToken(appUser, response);
            setRefreshToken(appUser, response);
            return buildUserResponse(appUser);
        } catch (Exception e) {
            log.error("Registration exception", e);
            clearAuthAndRefreshTokens(response);
            return buildErrorResponse(e.getLocalizedMessage());
        }
    }

    @PostMapping(path = "login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, RegistrationsRequest registrationsRequest, HttpServletResponse response) {
        try {
            AppUser appUser = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
            setAuthToken(appUser, response);
            setRefreshToken(appUser, response);
            return buildUserResponse(appUser);
        } catch (Exception e) {
            log.error("Login exception", e);
            clearAuthAndRefreshTokens(response);
            return buildErrorResponse(e.getLocalizedMessage());
        }
    }

    @GetMapping(path = "logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            SecurityContextHolder.clearContext();
            clearAuthAndRefreshTokens(response);
            return buildUserResponse(new AppUser());
        } catch (Exception e) {
            log.error("Logout exception", e);
            return buildErrorResponse(e.getLocalizedMessage());
        }
    }

    @GetMapping(path = "current")
    public ResponseEntity<?> current() {
        AppUser appUser = null;
        try {
            appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            appUser = new AppUser();
        }
        return buildUserResponse(appUser);
    }

    private void clearAuthAndRefreshTokens(HttpServletResponse httpServletResponse) {

        Cookie authCookie = new Cookie(jwtTokenProvider.getAuthCookieName(), "-");
        authCookie.setPath(jwtTokenProvider.getCookiePath());

        Cookie refreshCookie = new Cookie(jwtTokenProvider.getRefreshCookieName(), "-");
        refreshCookie.setPath(jwtTokenProvider.getCookiePath());

        httpServletResponse.addCookie(authCookie);
        httpServletResponse.addCookie(refreshCookie);
    }

    private void setAuthToken(AppUser appUser, HttpServletResponse httpServletResponse) {
        String token = jwtTokenProvider.createAuthToken(appUser.getEmail(), appUser.getUserRole().name());
        Cookie cookie = new Cookie(jwtTokenProvider.getAuthCookieName(), token);
        cookie.setPath(jwtTokenProvider.getCookiePath());
        cookie.setMaxAge(jwtTokenProvider.getAuthExpirationCookie());
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
    }

    private void setRefreshToken(AppUser appUser, HttpServletResponse httpServletResponse) {
        String token = jwtTokenProvider.createRefreshToken(appUser.getEmail(), appUser.getUserRole().name());
        Cookie cookie = new Cookie(jwtTokenProvider.getRefreshCookieName(), token);
        cookie.setPath(jwtTokenProvider.getCookiePath());
        cookie.setMaxAge(jwtTokenProvider.getRefreshExpirationCookie());
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
    }

    private ResponseEntity<?> buildUserResponse(AppUser appUser) {
        return ResponseEntity.ok(new UserResponse(appUser));
    }

    private ResponseEntity<?> buildErrorResponse(String message) {
        return ResponseEntity.ok(new ErrorResponse(message));
    }
}
