package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot { // класс бота
    private final org.example.Service service;
    private final ArrayListStoryService arrayListStoryService;
    private final MessageHandlerService messageHandlerService;
    private final StartCommandService startCommandService;

    @Override
    public void onUpdateReceived(Update update) {
        if (!(update.hasMessage() && update.getMessage().hasText())) return;

        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        // Обрабатываем команду /start отдельно
        if ("/start".equals(text)) {
            startCommandService.handleStartCommand(this, chatId);
            return; // Выходим, чтобы не обрабатывать дальше
        }

        // Обрабатываем все остальные сообщения через MessageHandlerService
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                messageHandlerService.handleMessage(this, update.getMessage());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "Collection_bot";
    }

    @Override
    public String getBotToken() {
        return "";
    }
}

