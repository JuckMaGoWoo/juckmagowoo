package com.juckmagowoo.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";  // templates/index.html 렌더링
    }

    @GetMapping("/main")
    public String mainPage(){
        return "main";
    }
}
