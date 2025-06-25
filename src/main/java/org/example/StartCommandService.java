package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class StartCommandService {
    private final org.example.Service service;

    public void handleStartCommand(TelegramLongPollingBot bot, Long chatId) {
        try {
            bot.execute(service.photoStart(chatId));
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки фото на функции старт: " + e.getMessage());
            // Не бросаем RuntimeException, чтобы бот продолжал работать
        }

        try {
            bot.execute(service.startCommand(chatId));
        } catch (TelegramApiException e) {
            System.err.println("Сообщение после команды Start не отправлено: " + e.getMessage());
            // Не бросаем RuntimeException, чтобы бот продолжал работать
        }
    }
} 