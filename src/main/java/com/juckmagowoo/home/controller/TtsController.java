package com.juckmagowoo.home.controller;

import com.juckmagowoo.home.service.TtsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class TtsController {

    private final TtsService ttsService;

    public TtsController(TtsService ttsService) {
        this.ttsService = ttsService;
    }

    //tts 엔드포인트로 텍스트를 전달받아 음성(MP3 파일)을 다운로드함
    @GetMapping(value = "/tts", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<byte[]>> getTts(@RequestParam String text) {
        return ttsService.textToSpeech(text)
                .map(audioBytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tts_output.mp3")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(audioBytes));
    }
}
