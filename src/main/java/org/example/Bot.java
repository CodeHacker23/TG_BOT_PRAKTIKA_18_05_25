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
                // Найти пользователя и поставить статус ожидания имени
                UserEntity user = userService.getUserById(userId);
                if (user == null) {
                    user = new UserEntity();
                    user.setTgId(userId);
                }
                user.setState("AWAITING_CHARACTER_NAME");
                userService.saveUser(user);
                org.telegram.telegrambots.meta.api.methods.send.SendMessage askName = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
                askName.setChatId(chatId.toString());
                askName.setText("Напишите имя для персонажа");
                try {
                    execute(askName);
                } catch (TelegramApiException e) {
                    System.err.println("Ошибка отправки запроса имени персонажа: " + e.getMessage());
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

