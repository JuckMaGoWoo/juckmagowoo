package com.juckmagowoo.home.repository;

import com.juckmagowoo.home.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

//    // 우울 척도 또는 언어 능력이 70 이상인 문장 조회
//    List<Sentence> findByUser_UserIdAndAnxietyScoreGreaterThanEqualOrLogicalScoreLessThanEqual(Long userId, Long anxietyScore, Long logicalScore);

    @Query("SELECT s FROM Sentence s " +
            "WHERE s.user.userId = :userId " +
            "AND (s.anxietyScore >= :anxietyScore OR s.logicalScore <= :logicalScore)")
    List<Sentence> findSentencesByUserIdAndAnxietyOrLogical(
            @Param("userId") Long userId,
            @Param("anxietyScore") Long anxietyScore,
            @Param("logicalScore") Long logicalScore
    );


    List<Sentence> findByUser_UserId(Long userId);
}
