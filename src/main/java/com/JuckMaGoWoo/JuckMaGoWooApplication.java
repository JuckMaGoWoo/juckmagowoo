package com.JuckMaGoWoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.JuckMaGoWoo.home.entity"})
@EnableJpaRepositories(basePackages = {"com.JuckMaGoWoo.home.repository"})
public class JuckMaGoWooApplication {

    public static void main(String[] args) {
        SpringApplication.run(JuckMaGoWooApplication.class, args);
    }

}
