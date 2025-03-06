package com.juckmagowoo.home.controller;

import com.juckmagowoo.home.service.ChatGptService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/audio")  // API 경로 통일
public class ChatGptController {

    private final ChatGptService chatGptService;

    public ChatGptController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    /**
     * 🎤 STT → ChatGPT → TTS (MP3 변환 후 반환)
     */
    @PostMapping(value = "/stt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<byte[]>> processAudioToTts(
            @RequestParam("file") MultipartFile audioFile,
            @RequestParam("userId") String userId) {

        String prompt1 = "너는 친절한 AI야. 사용자의 질문을 이해하고 적절한 답변을 제공해.";
        String prompt2 = "너는 감성적인 AI야. 사용자의 감정을 이해하고 공감하는 답변을 제공해.";

        return chatGptService.processAudioWithTwoPrompts(audioFile, prompt1, prompt2)
                .map(audioData -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(audioData));
    }

    /**
     * 🔥 저장된 MP3 파일을 반환하는 API
     */
    @GetMapping(value = "/mp3", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<byte[]>> getGeneratedAudio() {
        return chatGptService.getGeneratedAudio()
                .map(audioData -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=gpt_answer.mp3")
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .body(audioData));
    }
}






