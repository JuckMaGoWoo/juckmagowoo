package com.juckmagowoo.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@RequestParam(name = "userId", required = false) String userId, Model model) {
        if (userId == null) {
            return "redirect:?userId=1";  // URL에 userId=1을 추가하여 리다이렉트
        }
        model.addAttribute("userId", userId);
        return "index";  // templates/index.html 렌더링
    }

    @GetMapping("/main")
    public String mainPage(@RequestParam(name = "userId", required = false) String userId, Model model) {
        if (userId == null) {
            return "redirect:/main?userId=1";  // URL에 userId=1을 추가하여 리다이렉트
        }
        model.addAttribute("userId", userId);
        return "main";  // templates/main.html 렌더링
    }
}
