package com.juckmagowoo.home.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class TtsService {

    private final WebClient webClient;

    public TtsService(@Value("${openai.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    //텍스트를 음성(MP3)으로 변환하는 메서드
    public Mono<byte[]> textToSpeech(String text) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "tts-1");
        requestBody.put("voice", "nova"); // nova 목소리 사용
        requestBody.put("input", text);
        requestBody.put("response_format", "mp3");
        requestBody.put("speed", 1.0);

        return webClient.post()
                .uri("/audio/speech")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(byte[].class);
    }
}
