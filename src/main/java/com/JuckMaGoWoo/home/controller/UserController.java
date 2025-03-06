package com.JuckMaGoWoo.home.controller;

import com.JuckMaGoWoo.home.entity.User;
import com.JuckMaGoWoo.home.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(
            @RequestParam String name,
            @RequestParam Long age,
            @RequestParam Boolean sex) {

        User newUser = new User();
        newUser.setName(name);
        newUser.setAge(age);
        newUser.setSex(sex);
        newUser.setCreatedAt(LocalDateTime.now());

        return userService.saveUser(newUser);
    }
}