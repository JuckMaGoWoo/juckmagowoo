package com.juckmagowoo.home.service;

import com.juckmagowoo.home.entity.User;
import com.juckmagowoo.home.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void appendToHistory(Long userId, String userMessage) {
        User user = userRepository.findById(userId).orElseThrow();

        // 기존 history 불러오기
        String previousHistory = user.getHistory();
        if (previousHistory == null) {
            previousHistory = "";
        }

        // 새로운 메시지 추가
        String newHistory = previousHistory +
                "\n사용자: " + userMessage;

        // 업데이트 후 저장
        user.setHistory(newHistory);
        userRepository.save(user);
    }

}



