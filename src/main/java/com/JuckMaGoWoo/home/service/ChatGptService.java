package com.JuckMaGoWoo.home.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatGptService {

    private final WebClient webClient;

    public ChatGptService(@Value("${openai.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * 추가 프롬프트를 받아 질문과 함께 GPT의 답변 텍스트를 반환하는 메서드
     *
     * @param question 사용자 질문
     * @param prompt   추가 시스템 프롬프트(예: "너는 친절하고 자세한 설명을 제공하는 어시스턴트이다.")
     * @return GPT의 답변 텍스트
     */
    public Mono<String> getAnswer(String question, String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        // 시스템 프롬프트와 사용자 메시지를 구성합니다.
        List<Map<String, String>> messages = new ArrayList<>();
        // 추가 프롬프트(시스템 메시지)
        messages.add(Map.of("role", "system", "content", prompt));
        // 사용자의 질문 메시지
        messages.add(Map.of("role", "user", "content", question));
        requestBody.put("messages", messages);

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ChatGptResponse.class)
                .map(response -> response.getChoices().get(0).getMessage().getContent());
    }

    /**
     * 질문과 프롬프트를 보내고 GPT의 답변을 받아 파일(gpt_answer.txt)에 저장한 후 답변 텍스트를 반환합니다.
     */
    public Mono<String> getAndSaveAnswer(String question, String prompt) {
        return getAnswer(question, prompt)
                .doOnNext(answer -> {
                    try {
                        Files.writeString(Paths.get("gpt_answer.txt"), answer,
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}


