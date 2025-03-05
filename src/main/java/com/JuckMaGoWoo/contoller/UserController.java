package com.JuckMaGoWoo.contoller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/user")
@RestController
public class UserController {

    @PostMapping
    public ResponseEntity<String> createUser(@RequestParam String name, @RequestParam int age, @RequestParam boolean sex) {
        // body 없이 200
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable int id) {
        // body 없이 200
        Map<String, Object> user1 = Map.of(
                "userId", 1,
                "name", "홍길동",
                "sex", "남",
                "age", 20,
                "createdAt", "2021-07-01T00:00:00"
        );

        return ResponseEntity.ok(user1);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        // body 없이 200
        Map<String, Object> user1 = Map.of(
                "userId", 1,
                "name", "홍길동",
                "sex", "남",
                "age", 20,
                "createdAt", "2021-07-01T00:00:00"
        );
        Map<String, Object> user2 = Map.of(
                "userId", 2,
                "name", "홍박박",
                "sex", "여",
                "age", 20,
                "createdAt", "2021-07-21T00:00:00"
        );

        return ResponseEntity.ok(List.of(user1, user2));
    }
}
