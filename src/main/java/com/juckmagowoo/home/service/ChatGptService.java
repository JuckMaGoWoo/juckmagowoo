package com.juckmagowoo.home.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatGptService {
    private final WebClient webClient;
    private final TtsService ttsService;
    private final SttService sttService;
    private final ObjectMapper objectMapper;

    public ChatGptService(@Value("${openai.api.key}") String apiKey, TtsService ttsService, SttService sttService, ObjectMapper objectMapper) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofMinutes(2))))
                .build();
        this.ttsService = ttsService;
        this.sttService = sttService;
        this.objectMapper = objectMapper;
    }

    /**
     * 🎤 STT → ChatGPT(2개 프롬프트) → JSON + TTS(MP3 변환)
     */
    public Mono<byte[]> processAudioWithTwoPrompts(MultipartFile audioFile, String prompt1, String prompt2) {
        return Mono.fromCallable(() -> sttService.transcribeAudio(audioFile))
                .flatMap(question -> {
                    System.out.println("🎤 STT 변환된 질문: " + question);

                    return getAnswer(question, prompt1)
                            .zipWith(getAnswer(question, prompt2), (response1, response2) -> {
                                System.out.println("💬 GPT 응답 1: " + response1);
                                System.out.println("💬 GPT 응답 2: " + response2);

                                return ttsService.textToSpeech(response2)
                                        .doOnNext(audioData -> {
                                            try {
                                                // 🔥 MP3 파일을 프로젝트 루트에 저장
                                                Files.write(Paths.get("./gpt_answer.mp3"), audioData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                            }).flatMap(mono -> mono);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * GPT에게 질문을 보내고 답변을 받음
     */
    private Mono<String> getAnswer(String question, String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", prompt));
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
     * 저장된 MP3 파일을 반환
     */
    public Mono<byte[]> getGeneratedAudio() {
        return Mono.fromCallable(() -> Files.readAllBytes(Paths.get("./gpt_answer.mp3")))
                .subscribeOn(Schedulers.boundedElastic());
    }
}


