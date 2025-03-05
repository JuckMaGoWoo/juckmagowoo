package com.JuckMaGoWoo.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // src/main/resources/templates/admin.html 반환
    }
}

