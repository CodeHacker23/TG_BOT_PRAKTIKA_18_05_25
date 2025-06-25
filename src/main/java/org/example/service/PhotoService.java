package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    public void sendStartPhoto(Long chatId) {
        log.debug("Стартовое фото подготовлено для отправки в чат: {}", chatId);
        // Реализация будет в TelegramBot
    }

    public void sendTheoryPhoto(Long chatId) {
        log.debug("Фото теории подготовлено для отправки в чат: {}", chatId);
        // Реализация будет в TelegramBot
    }

    public void sendMemePhoto(Long chatId) {
        log.debug("Мем-фото подготовлено для отправки в чат: {}", chatId);
        // Реализация будет в TelegramBot
    }

    public void sendPythonPhoto(Long chatId) {
        log.debug("Python фото подготовлено для отправки в чат: {}", chatId);
        // Реализация будет в TelegramBot
    }
} 