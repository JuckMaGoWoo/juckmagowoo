package com.juckmagowoo.admin.service;

import org.springframework.stereotype.Service;

import com.juckmagowoo.home.entity.User;
import com.juckmagowoo.home.repository.UserRepository;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserDetail(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
