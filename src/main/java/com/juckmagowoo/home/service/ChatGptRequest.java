package com.juckmagowoo.home.service;

public class ChatGptRequest {
    private String question;
    private String prompt;

    public ChatGptRequest() {
    }

    public ChatGptRequest(String question, String prompt) {
        this.question = question;
        this.prompt = prompt;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}

