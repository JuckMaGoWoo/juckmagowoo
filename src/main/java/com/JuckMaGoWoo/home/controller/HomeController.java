package com.JuckMaGoWoo.home.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";  // templates/index.html 렌더링
    }
}
