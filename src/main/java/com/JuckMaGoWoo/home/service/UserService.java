package com.JuckMaGoWoo.home.service;

import com.JuckMaGoWoo.home.entity.User;
import com.JuckMaGoWoo.home.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}