package com.juckmagowoo.home.controller;

import com.juckmagowoo.home.entity.User;
import com.juckmagowoo.home.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        user.setCreatedAt(LocalDateTime.now());
        return userService.saveUser(user);
    }

    @GetMapping
    public Map<String, Object> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "2") int size) {

        // Pageable 객체 생성 (Spring Data의 Page는 0-based index)
        Pageable pageable = PageRequest.of(page - 1, size);

        // 사용자 목록을 페이징하여 조회
        Page<User> userPage = userService.getAllUsers(pageable);

        // 사용자 목록과 페이지 관련 정보 반환
        return Map.of(
                "users", userPage.getContent(),  // 페이지 내 사용자 목록
                "totalUsers", userPage.getTotalElements(),  // 전체 사용자 수
                "totalPages", userPage.getTotalPages()  // 전체 페이지 수
        );
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

}
