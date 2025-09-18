package com.edwinbaquiax.courseadministratorservice.security;

import com.edwinbaquiax.courseadministratorservice.exceptions.UserNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.user.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public UserPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return (UserPrincipal) authentication.getPrincipal();
    }

    public Long getUserId() {
        return getPrincipal().getId();
    }

    public String getUsername() {
        return getPrincipal().getUsername();
    }
}
