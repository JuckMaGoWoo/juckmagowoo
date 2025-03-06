package com.JuckMaGoWoo.home.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "sex", nullable = false)
    private Boolean sex;

    @Column(name = "age", nullable = false)
    private Long age;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}