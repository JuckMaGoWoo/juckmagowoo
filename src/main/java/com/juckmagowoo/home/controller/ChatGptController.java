package com.juckmagowoo.home.controller;


import com.juckmagowoo.home.service.ChatGptRequest;
import com.juckmagowoo.home.service.ChatGptService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ChatGptController {

    private final ChatGptService chatGptService;

    public ChatGptController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    /**
     * POST /chatgpt 엔드포인트: 요청 바디에 질문과 프롬프트를 포함하여 GPT의 답변을 파일에 저장한 후, 텍스트로 반환합니다.
     * 요청 예시 (JSON):
     * {
     *    "question": "오늘 날씨 어때?",
     *    "prompt": "대답은 용 으로 끝나게해"
     * }
     */
    @PostMapping(value="/chatgpt", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<ResponseEntity<String>> getChatGptAnswer(@RequestBody ChatGptRequest request) {
        String question = request.getQuestion();
        String prompt = request.getPrompt();
        return chatGptService.getAndSaveAnswer(question, prompt)
                .map(answer -> ResponseEntity.ok().body(answer));
    }
}




