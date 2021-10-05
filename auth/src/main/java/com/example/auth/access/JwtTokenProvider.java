package com.example.auth.access;

import com.example.auth.entity.AppUser;
import com.example.auth.service.AppUserDetailsService;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final AppUserDetailsService userDetailsService;

    @Value("${auth.cookie.secret}")
    private String secretKey;

    @Getter
    @Value("${auth.cookie.auth}")
    private String authCookieName;

    @Getter
    @Value("${auth.cookie.refresh}")
    private String refreshCookieName;

    @Getter
    @Value("${auth.cookie.expiration-auth}")
    private Integer authExpirationCookie;

    @Getter
    @Value("${auth.cookie.expiration-refresh}")
    private Integer refreshExpirationCookie;

    @Getter
    @Value("${auth.cookie.path}")
    private String cookiePath;

    public JwtTokenProvider(AppUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    public void init() {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAuthToken(String userName, String role) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("role", role);
        Date now = new Date();
        Date valid = new Date(now.getTime() + getAuthExpirationCookie());
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(valid).signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }


    public String createRefreshToken(String userName, String role) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("role", role);
        Date now = new Date();
        Date valid = new Date(now.getTime() + getRefreshExpirationCookie());
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(valid).signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return claimsJws.getBody().getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            log.error(e.getLocalizedMessage());
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        AppUser appUser = userDetailsService.loadUserByUsername(getUserName(token));
        return new UsernamePasswordAuthenticationToken(appUser, appUser.getPassword(), appUser.getAuthorities());
    }

    private String getUserName(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        String res = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(getAuthCookieName())) {
                    res = cookie.getValue();
                }
            }
        }
        return res;
    }
}
