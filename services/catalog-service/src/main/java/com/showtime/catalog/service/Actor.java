package com.showtime.catalog.service;

import org.springframework.security.core.Authentication;

/** Who is making the request, reduced to what the business rules need. */
public record Actor(String username, boolean admin) {

    public static Actor from(Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return new Actor(authentication.getName(), admin);
    }
}
