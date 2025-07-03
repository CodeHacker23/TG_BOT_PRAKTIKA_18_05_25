package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.example.PersonageBase.getRandomPersonage;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot { // класс бота
    private final org.example.Service service;
    private final ArrayListStoryService arrayListStoryService;
    private final MessageHandlerService messageHandlerService;
    private final StartCommandService startCommandService;
    private final UserService userService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Long userId = update.getCallbackQuery().getFrom().getId();

            if ("create_personage".equals(data)) {
                PersonageBase personage = getRandomPersonage();
                SendPhoto photo = ((Personage1) personage).getSendPhotoTheory(chatId);
                try {
                    execute(photo);
                } catch (TelegramApiException e) {
                    System.err.println("Ошибка отправки фото персонажа: " + e.getMessage());
                }
                try {
                    userService.assignPersonageToUser(userId, personage);
                } catch (Exception e) {
                    System.err.println("Ошибка при привязке персонажа к пользователю: " + e.getMessage());
                }
                return;
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            // Обрабатываем команду /start отдельно
            if ("/start".equals(text)) {
                startCommandService.handleStartCommand(this, chatId);
                return; // Выходим, чтобы не обрабатывать дальше
            }

            // Обрабатываем все остальные сообщения через MessageHandlerService
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
    public String getBotToken() { //
        return "";
    }
}

