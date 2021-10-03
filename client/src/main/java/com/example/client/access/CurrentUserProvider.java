package com.example.client.access;

import com.example.client.bean.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
@AllArgsConstructor
public class CurrentUserProvider {

    private final static String ATTR_CURRENT_USER = "currentUser";
    private final HttpServletRequest request;

    public void set(CurrentUser currentUser){
        HttpSession httpSession = request.getSession(true);
        if(httpSession != null ){
            request.setAttribute(ATTR_CURRENT_USER, currentUser);
        }
    }

    public CurrentUser get(){
        HttpSession httpSession = request.getSession(true);
        if(httpSession != null ){
            return (CurrentUser) request.getAttribute(ATTR_CURRENT_USER);
        }
        return new CurrentUser();
    }
}
