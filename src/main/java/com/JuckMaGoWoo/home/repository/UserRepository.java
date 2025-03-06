package com.JuckMaGoWoo.home.repository;

import com.JuckMaGoWoo.home.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}