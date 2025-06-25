package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.BotConfig;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final BotConfig botConfig;

    public void sendMessage(Long chatId, String text) {
        log.debug("Сообщение подготовлено для отправки в чат: {}", chatId);
        // Реализация будет в TelegramBot
    }

    public void sendMessageWithKeyboard(Long chatId, String text, ReplyKeyboard keyboard) {
        log.debug("Сообщение с клавиатурой подготовлено для отправки в чат: {}", chatId);
        // Реализация будет в TelegramBot
    }

    public void deleteMessageAsync(Long chatId, Integer messageId) {
        CompletableFuture.runAsync(() -> {
            try {
                //Thread.sleep(TimeUnit.SECONDS.toMillis(botConfig.getMessageDeletionDelay()));
                log.debug("Сообщение {} подготовлено для удаления из чата {}", messageId, chatId);
                // Реализация будет в TelegramBot
            } catch (Exception e) {
                log.error("Ошибка удаления сообщения {} из чата {}: {}", messageId, chatId, e.getMessage());
            }
        });
    }
} 