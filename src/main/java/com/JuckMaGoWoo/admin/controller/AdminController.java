package com.JuckMaGoWoo.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/user")
public class AdminController {

    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // src/main/resources/templates/admin.html 반환
    }

    @GetMapping("/admin/users")
    public String adminUsersPage() {
        return "admin-users"; // src/main/resources/templates/admin-users.html 반환
    }
}

