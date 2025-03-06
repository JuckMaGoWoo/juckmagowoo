package com.juckmagowoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.juckmagowoo.home.entity"})
@EnableJpaRepositories(basePackages = {"com.juckmagowoo.home.repository"})
public class JuckmagowooApplication {

    public static void main(String[] args) {
        SpringApplication.run(JuckmagowooApplication.class, args);
    }

}
