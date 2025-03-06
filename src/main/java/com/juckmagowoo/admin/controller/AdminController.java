package com.juckmagowoo.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.juckmagowoo.admin.service.AdminService;
import com.juckmagowoo.home.entity.User;

@Controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // src/main/resources/templates/admin.html 반환
    }

    @GetMapping("/admin/user")
    public ResponseEntity<User> getUserDetail(@RequestParam Long userId) {
        return ResponseEntity.ok(adminService.getUserDetail(userId));
    }
}

