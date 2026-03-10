package com.example.boopoom.web;

import com.example.boopoom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributeAdvice {

    private final UserService userService;

    @ModelAttribute("currentUserPoints")
    public Integer currentUserPoints(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return userService.findOneByEmail(authentication.getName())
                .map(user -> user.getCurrentPoints())
                .orElse(null);
    }
}
