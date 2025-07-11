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
    private final UserService userService;
    private final PersonageCreationService personageCreationService;
    private final StoryStartService storyStartService;

    private boolean hasCharacter(UserEntity user) {
        return user != null && user.getCharacterType() != null && !user.getCharacterType().isEmpty();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Long userId = update.getCallbackQuery().getFrom().getId();

            if ("create_personage".equals(data)) {
                storyStartService.handleCreatePersonage(this, chatId, userId);
                return;
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();

            // Обрабатываем команду /start отдельно
            if ("/start".equals(text)) {
                storyStartService.handleStart(this, chatId, userId);
                return;
            }

            // Обработка ввода имени персонажа
            UserEntity user = storyStartService.userService.getUserByTgId(userId);
            if (user != null && "AWAITING_CHARACTER_NAME".equals(user.getState())) {
                storyStartService.handleCharacterNameInput(this, chatId, userId, text);
                return;
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

