package org.example.config;

import lombok.Data;
import org.example.bot.TelegramBot;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Data
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotConfig {
    private   String username = "Collection_bot";
    private   String token = "7712574233:AAHi-5gCchegAh7tnhxyQ8oMu--ekbjwyjo";
   // private int messageDeletionDelay = 10; // секунды по умолчанию

    @Bean
    TelegramBotsApi telegramBotsApi(TelegramBot bot) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
        return telegramBotsApi;
    }
}
