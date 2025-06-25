package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    public void sendArrayListQuiz(Long chatId) {
        log.debug("Викторина ArrayList подготовлена для отправки в чат: {}", chatId);
        // Реализация будет в TelegramBot
    }

    public void sendAdvancedQuiz(Long chatId) {
        log.debug("Продвинутая викторина подготовлена для отправки в чат: {}", chatId);
        // Реализация будет в TelegramBot
    }
} 