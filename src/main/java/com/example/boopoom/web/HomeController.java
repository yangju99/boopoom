package com.example.boopoom.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {
    @RequestMapping("/")
    public String home(Authentication authentication, Model model){
        log.info("home controller");
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
        boolean isAdmin = isLoggedIn && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
        boolean isUser = isLoggedIn && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "ROLE_USER".equals(grantedAuthority.getAuthority()));

        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isUser", isUser);
        model.addAttribute("username", isLoggedIn ? authentication.getName() : null);
        return "home";
    }
}
