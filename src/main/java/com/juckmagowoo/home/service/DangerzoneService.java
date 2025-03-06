package com.juckmagowoo.home.service;

import com.juckmagowoo.home.entity.Sentence;
import com.juckmagowoo.home.repository.SentenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DangerzoneService {

    private final SentenceRepository sentenceRepository;

    public DangerzoneService(SentenceRepository sentenceRepository) {
        this.sentenceRepository = sentenceRepository;
    }

    // 우울 척도 70 이상이거나 언어 능력 30 이하인 문장 조회
    public List<Sentence> getDangerSentences(Long userId) {
        return sentenceRepository.findByUser_UserIdAndAnxietyScoreGreaterThanEqualAndLogicalScoreLessThanEqual(userId, 70L, 30L);
    }

    // 특정 사용자의 모든 데이터 조회
    public List<Sentence> getAllSentencesByUserId(Long userId) {
        return sentenceRepository.findByUser_UserId(userId);
    }
}
