package org.example.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.example.model.entity.UserEntity;

import org.example.service.PhotoService;
import org.example.service.UserService;
import org.example.service.StoryStartService;
import org.example.service.PersonageCreationService;
import org.example.service.ArrayListStoryService;

import java.util.ArrayList;

/**
 * Bot — главный дирижёр, связующее звено между Telegram и всеми сервисами.
 * Именно здесь решается, кто и когда будет страдать от багов, а кто — получать фоточки и карточки.
 *
 * Здесь происходит:
 * - Приём всех апдейтов от Telegram
 * - Маршрутизация команд и сообщений
 * - Вызов StoryStartService для старта и создания персонажа
 * - Вызов MessageHandlerService для остального трэша
 *
 * Если ты решишь добавить бизнес-логику прямо сюда — знай, Архитектор уже выехал за тобой.
 *
 * Пример использования:
 *   Bot бот = ...;
 *   бот.onUpdateReceived(update); // и понеслась душа в рай
 */
@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot { // класс бота
    private static final Logger log = LoggerFactory.getLogger(Bot.class);
    // Все сервисы, которые нужны боту. Если их станет больше 10 — пора делать рефакторинг.
    private final org.example.Service service;
    private final ArrayListStoryService arrayListStoryService;
    private final MessageHandlerService messageHandlerService;
    private final UserService userService;
    private final PersonageCreationService personageCreationService;
    private final StoryStartService storyStartService;
    private final PhotoService photoService;
    private final CallbackQueryHandlerService callbackQueryHandlerService;

    /**
     * Проверка, есть ли у пользователя персонаж
     * @param user — сущность пользователя
     * @return true, если персонаж уже создан
     *
     * Пример:
     *   if (hasCharacter(user)) { ... }
     */
    private boolean hasCharacter(UserEntity user) {
        return user != null && user.getPersonage() != null && user.getPersonage().getCharacterType() != null && !user.getPersonage().getCharacterType().isEmpty();
    }

    /**
     * Главный метод: обработка любого апдейта от Telegram
     * @param update — апдейт от Telegram
     *
     * Здесь происходит вся магия маршрутизации:
     * - Если пришёл callbackQuery с data "create_personage" — запускаем создание персонажа
     * - Если пришло текстовое сообщение:
     *     - /start — запускаем StoryStartService.handleStart
     *     - Если пользователь ждёт ввода имени — передаём в StoryStartService.handleCharacterNameInput
     *     - Всё остальное — в MessageHandlerService (и пусть он разбирается)
     *
     * Если что-то пойдёт не так — смотри логи и готовься к дебагу.
     */
    @Override
    public void onUpdateReceived(Update update) {
        log.info("onUpdateReceived() — получен апдейт: {}", update);
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Long userId = update.getCallbackQuery().getFrom().getId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            log.info("CallbackQuery: data={}, chatId={}, userId={}", data, chatId, userId);

            // Делегируем обработку всех callbackQuery в отдельный сервис
            callbackQueryHandlerService.handleCallback(this, data, chatId, userId, messageId);
            return;
        }









        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();
            log.info("Message: text='{}', chatId={}, userId={}", text, chatId, userId);

            // --- Удалён дублирующий планировщик отправки фото с вертолетом ---

            // Обрабатываем команду /start отдельно
            if ("/start".equals(text)) {
                log.info("Пользователь отправил /start.");
                storyStartService.handleStart(this, chatId, userId);
                log.info("Завершена обработка /start.");
                return;
            }

            // Обработка ввода имени персонажа
            UserEntity user = storyStartService.userService.getUserByTgId(userId);
            if (user != null && "AWAITING_CHARACTER_NAME".equals(user.getState())) {
                log.info("Пользователь вводит имя персонажа: '{}'", text);
                storyStartService.handleCharacterNameInput(this, chatId, userId, text);
                log.info("Завершена обработка имени персонажа.");
                return;
            }

            // Обрабатываем все остальные сообщения через MessageHandlerService
            try {
                log.info("Передаём сообщение в MessageHandlerService.");
                messageHandlerService.handleMessage(this, update.getMessage());
                log.info("Завершена обработка сообщения MessageHandlerService.");
            } catch (TelegramApiException e) {
                log.error("Ошибка в MessageHandlerService: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        log.info("onUpdateReceived() — обработка апдейта завершена.");
    }

    /**
     * Имя бота (Telegram username)
     * @return строка с именем
     */
    @Override
    public String getBotUsername() {
        return "Collection_bot";
    }

    /**
     * Токен бота (Telegram API token)
     * @return строка с токеном
     *
     * Никогда не выкладывай токен в открытый доступ, иначе твой бот быстро станет чужим!
     * гав
     */
    @Override
    public String getBotToken() { //
        return "";
    }
}

