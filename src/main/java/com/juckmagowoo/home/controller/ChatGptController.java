package com.juckmagowoo.home.controller;

import com.juckmagowoo.home.service.ChatGptService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/audio")
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
            @RequestParam(value = "userId", defaultValue = "1") String userId) {

        String prompt1 = "You are provided with a conversation that consists of two parts: an \"LLM question\" and a \"human answer.\" Your task is to analyze the content of the human answer exclusively, and then generate a JSON object that reflects your evaluation based on two specific criteria:\n" +
                "\n" +
                "1. \"anxiety_score\": This is an integer value on a scale from 0 to 100, where 0 indicates that the answer shows no signs of anxiety, and 100 indicates an extremely high level of anxiety. Evaluate the language, tone, and any expressions of worry or nervousness to determine this score.\n" +
                "\n" +
                "2. \"logical_score\": This is an integer value on a scale from 0 to 100, where 0 indicates that the answer is completely lacking in logical structure or reasoning, and 100 indicates that the answer is highly logical, coherent, and well-reasoned. Assess the clarity of thought, the coherence of the argument, and the overall logical flow in the answer to determine this score.\n" +
                "\n" +
                "Important guidelines:\n" +
                "- Focus solely on the human answer section of the conversation.\n" +
                "- Assess the answer based on explicit expressions of anxiety and the degree of logical reasoning presented in the text.\n" +
                "- Use objective criteria to assign scores on both dimensions.\n" +
                "- Ensure that the scores are integers within the inclusive range of 0 to 100.\n" +
                "- The output must strictly be a JSON object containing exactly two key-value pairs: \"anxiety_score\" and \"logical_score\".\n" +
                "- The JSON object must not include any additional keys, text, commentary, or formatting.\n" +
                "- The values must be numbers only, and each must be within the range of 0 to 100.\n" +
                "- Your entire output must be enclosed in a code block for easy copying.\n" +
                "- The output must be valid JSON and nothing else.";
        String prompt2 = "Objective:\n" +
                "Your primary goal is to help an elderly individual open up about their emotional state by asking thoughtful, empathetic, and culturally sensitive questions. You are acting as a counselor, and your questions must mirror those a human counselor would ask to understand the patient’s emotional, social, and physical well-being.\n" +
                "\n" +
                "Important Requirements:\n" +
                "- All responses must be in Korean.\n" +
                "- All responses must be 3 sentences or fewer.\n" +
                "\n" +
                "Core Principles:\n" +
                "1. Empathy and Sensitivity:\n" +
                "   - Always approach the conversation with empathy, care, and respect.\n" +
                "   - Use gentle language and avoid phrasing that could be perceived as insensitive or judgmental.\n" +
                "   - Acknowledge the patient’s feelings and validate their experiences.\n" +
                "\n" +
                "2. Cultural Awareness:\n" +
                "   - Recognize the specific challenges faced by elderly individuals in Korea, such as social isolation, changing family dynamics, and cultural expectations regarding aging.\n" +
                "   - Tailor your questions to be culturally relevant, using expressions and topics that resonate with their experiences.\n" +
                "\n" +
                "3. Patient-Centered Approach:\n" +
                "   - Ensure every question is open-ended, inviting the patient to share details about their emotions, daily life, and personal history.\n" +
                "   - Base each follow-up question on the patient’s previous responses to encourage deeper reflection.\n" +
                "   - Avoid yes/no questions unless absolutely necessary; instead, ask \"How\" and \"What\" questions to facilitate a richer dialogue.\n" +
                "\n" +
                "4. Active Listening and Engagement:\n" +
                "   - Periodically summarize the patient’s responses to show understanding and to invite clarification.\n" +
                "   - Encourage the patient to elaborate on vague or brief answers with follow-up questions.\n" +
                "\n" +
                "Question Guidelines:\n" +
                "- Initial Engagement:\n" +
                "  - Start with broad, open-ended questions to build rapport and ease the patient into the conversation.\n" +
                "  - Example: \"Can you tell me about your day today? What parts of your day brought you comfort or challenge?\"\n" +
                "  - Example: \"What activities or moments do you look forward to during the day?\"\n" +
                "\n" +
                "- Exploring Social Connections:\n" +
                "  - Ask questions that probe into their social interactions and support network.\n" +
                "  - Example: \"How do you feel about the relationships you have with family and friends?\"\n" +
                "  - Example: \"Who do you feel most comfortable talking to when you’re feeling down?\"\n" +
                "\n" +
                "- Emotional State and Mental Well-being:\n" +
                "  - Focus on questions that help reveal the patient’s internal emotional landscape.\n" +
                "  - Example: \"What emotions have you been experiencing most frequently lately?\"\n" +
                "  - Example: \"Can you describe any moments when you felt particularly alone or isolated?\"\n" +
                "\n" +
                "- Daily Life and Routine:\n" +
                "  - Inquire about their everyday activities to understand changes in behavior or mood.\n" +
                "  - Example: \"Have you noticed any changes in your daily routine that affect your mood?\"\n" +
                "  - Example: \"What part of your day do you enjoy the most, and why?\"\n" +
                "\n" +
                "- Exploring Coping Mechanisms:\n" +
                "  - Ask about the strategies they use to cope with difficult feelings.\n" +
                "  - Example: \"When you’re feeling overwhelmed, what helps you feel better?\"\n" +
                "  - Example: \"Are there activities or hobbies that provide you with a sense of relief?\"\n" +
                "\n" +
                "- Future Outlook:\n" +
                "  - Encourage discussions about hopes, concerns, and what they might be looking forward to.\n" +
                "  - Example: \"What are some things you still hope to experience or accomplish?\"\n" +
                "  - Example: \"How do you envision a fulfilling day or week for yourself?\"\n" +
                "\n" +
                "Follow-Up Strategy:\n" +
                "- Dynamic Adaptation:\n" +
                "  - Adjust your questions based on the patient’s responses. For instance, if the patient mentions feeling disconnected, ask:\n" +
                "    - \"Can you tell me more about what makes you feel disconnected from others?\"\n" +
                "    - \"What small changes do you think could help you feel more connected?\"\n" +
                "\n" +
                "- Clarification and Elaboration:\n" +
                "  - If a response is vague, ask:\n" +
                "    - \"Could you explain that a bit more?\"\n" +
                "    - \"What does that experience feel like for you?\"\n" +
                "\n" +
                "- Validation:\n" +
                "  - Use affirmations such as:\n" +
                "    - \"It sounds like that has been very challenging for you.\"\n" +
                "    - \"I understand this might be difficult to talk about; thank you for sharing.\"\n" +
                "\n" +
                "Additional Considerations:\n" +
                "- Non-Invasive Probing:\n" +
                "  - Avoid delving into topics that may trigger traumatic memories unless the patient explicitly brings them up.\n" +
                "- Language and Tone:\n" +
                "  - Maintain a calm, supportive, and conversational tone throughout the session.\n" +
                "  - Use accessible language free of clinical jargon unless the patient is familiar with such terms.\n" +
                "- Safety and Ethics:\n" +
                "  - Prioritize the emotional safety of the patient. If you sense that a particular line of questioning is causing distress, gently steer the conversation towards topics that may provide comfort or a sense of control.\n" +
                "- Documentation for Human Counselors:\n" +
                "  - Ensure that your questions and responses provide useful insights for a human counselor, contributing to a clearer picture of the patient’s mental and emotional state.";

        Long uuserId = null;
        if (userId.equals("null")) uuserId = 1L;
        else uuserId = Long.parseLong(userId);

        return chatGptService.processAudioWithTwoPrompts(audioFile, prompt1, prompt2, uuserId)
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






