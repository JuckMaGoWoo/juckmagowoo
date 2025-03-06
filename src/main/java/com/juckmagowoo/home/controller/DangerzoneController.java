package com.juckmagowoo.home.controller;

import com.juckmagowoo.home.entity.Sentence;
import com.juckmagowoo.home.entity.User;
import com.juckmagowoo.home.service.DangerzoneService;
import com.juckmagowoo.home.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dangerzone")
public class DangerzoneController {

    private final DangerzoneService dangerzoneService;
    private final UserService userService;

    public DangerzoneController(DangerzoneService dangerzoneService, UserService userService) {
        this.dangerzoneService = dangerzoneService;
        this.userService = userService;
    }

    // 우울 척도 70 이상 또는 언어 능력 70 이상인 문장 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<Sentence>> getDangerSentences(@PathVariable Long userId) {
        List<Sentence> dangerSentences = dangerzoneService.getDangerSentences(userId);
        return ResponseEntity.ok(dangerSentences);
    }

    // 특정 사용자의 모든 데이터 조회
    @GetMapping("/{userId}/all")
    public ResponseEntity<List<Sentence>> getAllSentencesByUserId(@PathVariable Long userId) {
        List<Sentence> allSentences = dangerzoneService.getAllSentencesByUserId(userId);
        return ResponseEntity.ok(allSentences);
    }
}
