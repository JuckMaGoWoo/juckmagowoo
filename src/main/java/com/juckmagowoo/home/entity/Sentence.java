package com.juckmagowoo.home.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "sentence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Sentence {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long sentenceId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String userInput;

    @Column(columnDefinition = "TEXT")
    private String gptOutput;

    @Column(columnDefinition = "INTEGER")
    private Long anxietyScore;

    @Column(columnDefinition = "INTEGER")
    private Long logicalScore;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

}
