package com.example.boopoom.web;

import com.example.boopoom.domain.User;
import com.example.boopoom.service.UserService;
import com.example.boopoom.web.forms.UserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users/new")
    public String createForm(Model model){
        model.addAttribute("userForm", new UserForm());
        return "users/createUserForm";
    }

    @PostMapping("/users/new")
    public String create(@Valid UserForm form, BindingResult result){
        if (result.hasErrors()){
            return "users/createUserForm";
        }

        userService.registerUser(form.getNickName(), form.getEmail(), form.getPassword());
        return "redirect:/";
    }

    @GetMapping("/users")
    public String userList(Model model){
        List<User> users = userService.findUsers();
        model.addAttribute("users", users);
        return "users/userList";
    }

    @GetMapping("/users/{userId}")
    public String userDetail(@PathVariable("userId") Long userId, Model model){
        User user = userService.findOne(userId);
        model.addAttribute("user", user);
        return "users/userDetail";
    }
}
