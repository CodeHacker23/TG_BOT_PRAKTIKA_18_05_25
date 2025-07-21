package org.example.bot;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.UserEntity;

import org.example.service.PhotoService;
import org.example.service.UserService;
import org.example.service.StoryStartService;
import org.example.service.PersonageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.example.model.personage.Personage1;
import org.example.model.personage.Personage2;
import org.example.model.personage.Personage3;


/**
 * Обрабатывает ВСЕ callbackQuery (инлайн-кнопки) Telegram-бота.
 *
 * - Каждый кейс — отдельный метод, никакой копипасты if-else!
 * - Если забудешь добавить обработку новой кнопки — пользователь будет тыкать в неё до посинения, а бот будет молчать, как твой дед на семейных праздниках.
 *
 * Пример:
 *   case "accept_armor" -> handleAcceptArmor(bot, chatId, userId);
 */
@Service
@RequiredArgsConstructor
public class CallbackQueryHandlerService {
    private static final Logger log = LoggerFactory.getLogger(CallbackQueryHandlerService.class);
    private final UserService userService;
    private final PhotoService photoService;

    private final StoryStartService storyStartService;
    private final PersonageService personageService;

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
            case "accept_armor" -> handleAcceptArmor(bot, chatId, userId);
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

    /**
     * Обработка инлайн-кнопки 'Принять доспехи' (accept_armor)
     * Здесь происходит магия: персонажу начисляются новые характеристики, а пользователю отправляется обновлённая карточка.
     */
    private void handleAcceptArmor(Bot bot, Long chatId, Long userId) {
        // 1. Получаем пользователя по userId (Telegram ID)
        UserEntity user = userService.getUserByTgId(userId);
        if (user == null) {
            // Если пользователя нет — отправляем ошибку и выходим
            try {
                bot.execute(new SendMessage(chatId.toString(), "Ошибка: пользователь не найден!"));
            } catch (Exception ignored) {}
            return;
        }
        // 2. Получаем его персонажа
        var personage = user.getPersonage();
        if (personage == null) {
            try {
                bot.execute(new SendMessage(chatId.toString(), "Ошибка: персонаж не найден!"));
            } catch (Exception ignored) {}
            return;
        }
        // 3. Определяем тип персонажа (characterType)
        String type = personage.getCharacterType();
        // 4. Обновляем характеристики через PersonageService (рандом для аналитики)
        // (добавь @Autowired PersonageService personageService; в этот класс!)
        personageService.updateStats(
            personage,
            1,      // levelDelta
            50,     // achievementDelta
            450.0,  // currencyDelta
            25,     // analyticsDelta (min)
            30,     // analyticsMax (max)
            true    // randomAnalytics
        );
        // 5. Создаём объект нужного персонажа и заполняем его из сущности
        if ("Personage1".equals(type)) {
            Personage1 p1 = new Personage1();
            p1.fillFromEntity(personage);
            try {
                bot.execute(p1.getRomanArmorCard( chatId,  0 ));
            } catch (Exception e) {
                log.error("Ошибка при отправке карточки Personage1: {}", e.getMessage());
            }
        } else if ("Personage2".equals(type)) {
            Personage2 p2 = new Personage2();
            p2.fillFromEntity(personage);
            // ... аналогично: bot.execute(p2.getRomanArmorCard(chatId));
        } else if ("Personage3".equals(type)) {
            Personage3 p3 = new Personage3();
            p3.fillFromEntity(personage);
            // ... аналогично: bot.execute(p3.getRomanArmorCard(chatId));
        } else {
            try {
                bot.execute(new SendMessage(chatId.toString(), "Ошибка: неизвестный тип персонажа!"));
            } catch (Exception ignored) {}
        }
    }
} 