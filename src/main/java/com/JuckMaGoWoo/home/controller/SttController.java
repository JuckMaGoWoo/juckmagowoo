package com.JuckMaGoWoo.home.controller;

import com.JuckMaGoWoo.home.service.SttService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SttController {

    private final SttService sttService;

    public SttController(SttService sttService) {
        this.sttService = sttService;
    }

    @PostMapping(value = "/api/audio/stt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> transcribeAudio(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(sttService.transcribeAudio(file));
    }
}

