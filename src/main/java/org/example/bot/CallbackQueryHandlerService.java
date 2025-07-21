package org.example.bot;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.UserEntity;

import org.example.service.PhotoService;
import org.example.service.UserService;
import org.example.service.StoryStartService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



/**
 * =========================
 * CallbackQueryHandlerService — твой спаситель от if-ового ада и архитектурного позора.
 * =========================
 *
 * Этот сервис централизованно обрабатывает ВСЕ callbackQuery (инлайн-кнопки) Telegram-бота.
 * Теперь твой Bot.java не будет разрастаться до размеров Войны и Мира, а Архитектор не будет плакать кровавыми слезами.
 *
 * Как работает:
 *   1. В Bot.java ты просто делегируешь обработку:
 *      callbackQueryHandlerService.handleCallback(this, data, chatId, userId, messageId);
 *   2. В этом сервисе switch-case по data (callbackData) решает, какой сюжет запускать.
 *   3. Для каждой кнопки — отдельный метод, никакой копипасты if-else!
 *
 * Пример (для самых уставших):
 *   case "enter_arraylist" -> handleArrayList(bot, chatId, userId);
 *   case "create_personage" -> storyStartService.handleCreatePersonage(bot, chatId, userId);
 *
 * Как расширять:
 *   - Добавляешь новую кнопку? Просто добавь новый case и метод.
 *   - Не пихай бизнес-логику прямо в switch — делегируй в отдельные методы!
 *   - Если логика повторяется — выноси в абстрактные классы/интерфейсы.
 *
 * Анти-пример:
 *   // ПЛОХО:
 *   if (data.equals("a")) {...} else if (data.equals("b")) {...} else if ...
 *   // Архитектор лично придёт и перепишет твой код на Brainfuck.
 *
 * Юмор:
 *   - Если начнёшь писать 100 if-ов в Bot.java — Архитектор лично напишет тебе в Telegram.
 *   - Если забудешь добавить обработку новой кнопки — пользователь будет тыкать в неё до посинения, а бот будет молчать, как твой дед на семейных праздниках.
 *   - Если не понял, как работает switch — перечитай ещё раз, потом выпей кофе, потом спроси у Архитектора.
 *
 * =========================
 */
@Service
@RequiredArgsConstructor
public class CallbackQueryHandlerService {
    private static final Logger log = LoggerFactory.getLogger(CallbackQueryHandlerService.class);
    private final UserService userService;
    private final PhotoService photoService;

    private final StoryStartService storyStartService;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    /**
     * Главный обработчик всех callbackQuery
     * @param bot — твой TelegramLongPollingBot (Bot)
     * @param data — callbackData кнопки
     * @param chatId — ID чата
     * @param userId — ID пользователя
     * @param messageId — ID сообщения с кнопкой (если нужно)
     */
    public void handleCallback(Bot bot, String data, Long chatId, Long userId, Integer messageId) {
        switch (data) {
            case "enter_arraylist" -> handleArrayList(bot, chatId, userId);
            case "create_personage" -> storyStartService.
                    handleCreatePersonage(bot, chatId, userId);
            // Добавляй новые кейсы для других кнопок!
            default -> log.info("Неизвестный callbackData: {} (chatId={}, userId={})", data, chatId, userId);
        }
    }

    /**
     * Обработка инлайн-кнопки 'Войти в мир ArrayList'
     */
    private void handleArrayList(Bot bot, Long chatId, Long userId) {
        UserEntity user = userService.getUserByTgId(userId);
        if (user != null && user.isPassedArrayList()) {
            try {
                bot.execute(new SendMessage(chatId.toString(), "Ты уже прошёл этот сюжет! \n В следующий раз приходи с мамой."));
            } catch (TelegramApiException e) {
                log.error("Ошибка при отправке сообщения: {}", e.getMessage());
            }
        } else if (user != null) {
            try {
                log.info("[CallbackQueryHandlerService] photoWarrior(chatId) - вызвана отправка командора пользователю: {}" ,chatId);
                bot.execute(photoService.photoWarrior(chatId)); //вызываем наш мем с воином
                scheduler.schedule(() -> {
                    try {
                        log.info(" [CallbackQueryHandlerService] - photoService.photoIteratorius() - вызвана отправка командора пользователю: {}" ,chatId);
                        bot.execute(photoService.photoIteratorius(chatId)); //вызываем нашу фотку командора Итературиаса
                        KeyboardService.gladiatorPlot(chatId);
                    } catch (TelegramApiException e) {
                        log.error("Ошибка при отправке фото Итератуса: {}", e.getMessage());
                    }

                }, 4,TimeUnit.SECONDS);
            } catch (TelegramApiException e) {
                log.error("Ошибка при отправке фото воина: {}", e.getMessage());
            }
            user.setPassedArrayList(true);
            userService.saveUser(user);
        } else {
            try {
                bot.execute(new SendMessage(chatId.toString(), "Ошибка: пользователь не найден. Попробуй ещё раз или напиши Архитектору."));
            } catch (TelegramApiException e) {
                log.error("Ошибка при отправке сообщения об ошибке: {}", e.getMessage());
            }
        }
    }
} 