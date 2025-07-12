package org.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Config — Spring-конфиг для регистрации Telegram-бота.
 * Здесь создаются и настраиваются бины, которые нужны для работы приложения.
 *
 * Как расширять:
 *   - Новый бин? Добавь новый @Bean-метод.
 *   - Не пихай бизнес-логику — только конфигурация.
 *
 * Пример использования:
 *   @Bean
 *   TelegramBotsApi telegramBotsApi(Bot bot) { ... }
 *
 * Юмор: если начнёшь регистрировать бота вручную в main — Архитектор лично напишет тебе в Telegram.
 */
@Configuration
public class Config {
    /**
     * Регистрирует Telegram-бота как Spring-бин
     * @param bot — твой основной Bot
     * @return TelegramBotsApi
     */
    @Bean
    TelegramBotsApi telegramBotsApi(Bot bot) {
        TelegramBotsApi telegramBotsApi;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
            System.out.println("[Config] telegramBotsApi() — бот успешно зарегистрирован!");
        } catch (TelegramApiException e) {
            System.err.println("[Config] Ошибка регистрации бота: " + e.getMessage());
            throw new RuntimeException();
        }
        return telegramBotsApi;
    }

    // --- Советы по расширению ---
    // 1. Новый бин? Добавь новый @Bean-метод.
    // 2. Не пихай бизнес-логику — только конфигурация.
    // 3. Если добавишь бин без комментария — Архитектор лично напишет тебе в Telegram.
}
