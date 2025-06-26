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
        }

        try {
            org.telegram.telegrambots.meta.api.methods.send.SendMessage message = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
            message.setChatId(chatId.toString());
            message.setText("*БайтФордж*\nЯ здесь, чтобы помочь тебе создать своего героя, разобраться в логике и определить любые вызовы.\nГотов ли ты начать свое путешествие?");
            message.setReplyMarkup(KeyboardService.getCreatePersonageInlineKeyboard());
            message.setParseMode("Markdown");
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Сообщение после команды Start не отправлено: " + e.getMessage());
        }
    }
} 