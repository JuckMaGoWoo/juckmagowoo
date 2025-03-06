package com.juckmagowoo.home.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juckmagowoo.home.entity.Sentence;
import com.juckmagowoo.home.entity.User;
import com.juckmagowoo.home.repository.SentenceRepository;
import com.juckmagowoo.home.repository.UserRepository;
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
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatGptService {
    private final WebClient webClient;
    private final TtsService ttsService;
    private final SttService sttService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final SentenceRepository sentenceRepository;
    private final UserRepository userRepository;

    public ChatGptService(@Value("${openai.api.key}") String apiKey,
                          TtsService ttsService,
                          UserService userService,
                          SttService sttService,
                          ObjectMapper objectMapper,
                          SentenceRepository sentenceRepository,
                          UserRepository userRepository) {
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
        this.sentenceRepository = sentenceRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Mono<byte[]> processAudioWithTwoPrompts(MultipartFile audioFile, String prompt1, String prompt2, long userId) {
        return Mono.fromCallable(() -> sttService.transcribeAudio(audioFile))
                .flatMap(question -> {
                    System.out.println("🎤 STT 변환된 질문: " + question);

                    User user = userRepository.findById(userId).orElseThrow();

                    String history = (user.getHistory() == null) ? "" : user.getHistory();
                    System.out.println("📝 사용자 히스토리: \n" + history);

                    Sentence sentence = new Sentence();
                    sentence.setUserInput(question);
                    sentence.setCreatedAt(LocalDateTime.now());
                    sentence.setUser(user);

                    String fullPrompt2 = history + "\n사용자: " + question + "\nAI:";  // 🟢 AI가 기억할 수 있도록 문맥 포함

                    return getAnswer(fullPrompt2, prompt2)
                            .flatMap(response2 -> {
                                System.out.println("💬 GPT 응답 2 (MP3 변환): " + response2);

                                // 🔹 TTS 변환 및 MP3 생성
                                return ttsService.textToSpeech(response2)
                                        .flatMap(audioData -> {
                                            return Mono.just(audioData);
                                        })
                                        .doFinally(signal -> {
                                            getAnswer(question, prompt1)
                                                    .flatMap(response1 -> {
                                                        System.out.println(" GPT 응답 1 (원본): " + response1);

                                                        response1 = cleanJsonResponse(response1);
                                                        System.out.println(" GPT 응답 1 (정리 후): " + response1);

                                                        Map<String, Integer> scores = parseScores(response1);
                                                        if (scores == null) {
                                                            System.err.println("❌ GPT 응답 JSON 파싱 실패, 기본값으로 대체");
                                                            scores = Map.of("anxiety_score", 50, "logical_score", 50);
                                                        }
                                                        sentence.setAnxietyScore(Long.valueOf(scores.get("anxiety_score")));
                                                        sentence.setLogicalScore(Long.valueOf(scores.get("logical_score")));
                                                        sentence.setGptOutput(response2);

                                                        sentenceRepository.save(sentence);

                                                        // 5️⃣ 📝 사용자 히스토리 업데이트
                                                        String updatedHistory = history + "\n사용자: " + question + "\nAI: " + response2;
                                                        user.setHistory(updatedHistory);
                                                        userRepository.save(user);

                                                        return Mono.empty();
                                                    })
                                                    .subscribeOn(Schedulers.boundedElastic())
                                                    .subscribe();
                                        });
                            });
                })
                .subscribeOn(Schedulers.boundedElastic());
    }




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

    public Mono<byte[]> getGeneratedAudio() {
        return Mono.fromCallable(() -> Files.readAllBytes(Paths.get("./gpt_answer.mp3")))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private String cleanJsonResponse(String response) {
        if (response == null) return "{}";

        response = response.replaceAll("```json", "").replaceAll("```", "").trim();

        int startIndex = response.indexOf("{");
        int endIndex = response.lastIndexOf("}");
        if (startIndex != -1 && endIndex != -1) {
            response = response.substring(startIndex, endIndex + 1);
        }

        return response;
    }

    private Map<String, Integer> parseScores(String jsonResponse) {
        try {
            Map<String, Object> parsedData = objectMapper.readValue(jsonResponse, Map.class);
            Map<String, Integer> scores = new HashMap<>();

            scores.put("anxiety_score", parsedData.getOrDefault("anxiety_score", 50) instanceof Integer ?
                    (Integer) parsedData.get("anxiety_score") : 50);
            scores.put("logical_score", parsedData.getOrDefault("logical_score", 50) instanceof Integer ?
                    (Integer) parsedData.get("logical_score") : 50);

            return scores;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
