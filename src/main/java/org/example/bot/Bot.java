package org.example.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.example.model.entity.UserEntity;
import org.example.service.StoryStartService;
import org.example.service.ArrayList.QuizService;
import org.example.bot.MessageHandlerService;
import org.example.bot.CallbackQueryHandlerService;

/**
 * Bot — твой главный дирижёр, шлюз между Telegram и всем этим бардаком.
 *
 * Что делает:
 * - Принимает ВСЕ апдейты от Telegram (сообщения, кнопки, пердежи в чате).
 * - Маршрутизирует команды: /start, /ArrayList, /LinkedList, /пошёл_нахуй.
 * - Делегирует обработку в сервисы, чтобы не было 1000 строк if-else.
 *
 * Если начнёшь писать бизнес-логику прямо тут — Архитектор уже выехал за тобой.
 *
 * Пример:
 *   bot.onUpdateReceived(update); // и понеслась душа в рай
 */
@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot { // класс бота
    private static final Logger log = LoggerFactory.getLogger(Bot.class);
    // Все сервисы, которые нужны боту. Если их станет больше 10 — пора делать рефакторинг.
    private final MessageHandlerService messageHandlerService;
    private final StoryStartService storyStartService;
    private final CallbackQueryHandlerService callbackQueryHandlerService;
    private final QuizService quizService;

    /**
     * Главный метод: обработка любого апдейта от Telegram
     * @param update — апдейт от Telegram
     *
     * Теперь это красивый switch-case вместо говнокодных if'ов!
     * Определяем тип апдейта и обрабатываем через соответствующий метод.
     * 
     * Типы апдейтов:
     * - POLL_ANSWER — ответы на викторины  
     * - CALLBACK_QUERY — нажатия на inline кнопки
     * - TEXT_MESSAGE — обычные текстовые сообщения
     * - UNKNOWN — всё остальное (игнорируем с логом)
     */
    @Override
    public void onUpdateReceived(Update update) {
        log.info("onUpdateReceived() — получен апдейт: {}", update);
        
        UpdateType updateType = determineUpdateType(update);
        
        switch (updateType) {
            case POLL_ANSWER -> handlePollAnswer(update);
            case CALLBACK_QUERY -> handleCallbackQuery(update);
            case TEXT_MESSAGE -> handleTextMessage(update);
            case UNKNOWN -> handleUnknownUpdate(update);
        }
        
        log.info("onUpdateReceived() — обработка апдейта завершена.");
    }

    /**
     * Определяет тип апдейта.
     * Вместо кучи if'ов теперь одна чистая логика определения типа.
     * 
     * @param update — апдейт от Telegram
     * @return UpdateType — тип апдейта
     */
    private UpdateType determineUpdateType(Update update) {
        if (update.hasPollAnswer()) {
            return UpdateType.POLL_ANSWER;
        }
        if (update.hasCallbackQuery()) {
            return UpdateType.CALLBACK_QUERY;
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            return UpdateType.TEXT_MESSAGE;
        }
        return UpdateType.UNKNOWN;
    }

    /**
     * Обрабатывает ответы на викторины.
     * 
     * @param update — апдейт с ответом на викторину
     */
    private void handlePollAnswer(Update update) {
        log.info("PollAnswer: получен ответ на викторину от userId={}", 
                update.getPollAnswer().getUser().getId());
        quizService.handleQuizAnswer(update.getPollAnswer(), this);
    }

    /**
     * Обрабатывает нажатия на inline кнопки.
     * 
     * @param update — апдейт с callback query
     */
    private void handleCallbackQuery(Update update) {
        String data = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long userId = update.getCallbackQuery().getFrom().getId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        
        log.info("CallbackQuery: data={}, chatId={}, userId={}", data, chatId, userId);
        
        // Делегируем обработку всех callbackQuery в отдельный сервис
        callbackQueryHandlerService.handleCallback(this, data, chatId, userId, messageId);
    }

    /**
     * Обрабатывает текстовые сообщения.
     * Здесь тоже используем switch для определения типа сообщения.
     * 
     * @param update — апдейт с текстовым сообщением
     */
    private void handleTextMessage(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        
        log.info("Message: text='{}', chatId={}, userId={}", text, chatId, userId);
        
        MessageType messageType = determineMessageType(text, userId);
        
        switch (messageType) {
            case START_COMMAND -> handleStartCommand(chatId, userId);
            case CHARACTER_NAME_INPUT -> handleCharacterNameInput(chatId, userId, text);
            case REGULAR_MESSAGE -> handleRegularMessage(update);
        }
    }

    /**
     * Определяет тип текстового сообщения.
     * 
     * @param text — текст сообщения
     * @param userId — ID пользователя
     * @return MessageType — тип сообщения
     */
    private MessageType determineMessageType(String text, Long userId) {
        if ("/start".equals(text)) {
            return MessageType.START_COMMAND;
        }
        
        UserEntity user = storyStartService.userService.getUserByTgId(userId);
        if (user != null && "AWAITING_CHARACTER_NAME".equals(user.getState())) {
            return MessageType.CHARACTER_NAME_INPUT;
        }
        
        return MessageType.REGULAR_MESSAGE;
    }

    /**
     * Обрабатывает команду /start.
     * 
     * @param chatId — ID чата
     * @param userId — ID пользователя
     */
    private void handleStartCommand(Long chatId, Long userId) {
        log.info("Пользователь отправил /start.");
        storyStartService.handleStart(this, chatId, userId);
        log.info("Завершена обработка /start.");
    }

    /**
     * Обрабатывает ввод имени персонажа.
     * 
     * @param chatId — ID чата
     * @param userId — ID пользователя
     * @param characterName — введённое имя персонажа
     */
    private void handleCharacterNameInput(Long chatId, Long userId, String characterName) {
        log.info("Пользователь вводит имя персонажа: '{}'", characterName);
        storyStartService.handleCharacterNameInput(this, chatId, userId, characterName);
        log.info("Завершена обработка имени персонажа.");
    }

    /**
     * Обрабатывает обычные сообщения через MessageHandlerService.
     * 
     * @param update — апдейт с сообщением
     */
    private void handleRegularMessage(Update update) {
        try {
            log.info("Передаём сообщение в MessageHandlerService.");
            messageHandlerService.handleMessage(this, update.getMessage());
            log.info("Завершена обработка сообщения MessageHandlerService.");
        } catch (TelegramApiException e) {
            log.error("Ошибка в MessageHandlerService: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Обрабатывает неизвестные типы апдейтов.
     * 
     * @param update — неизвестный апдейт
     */
    private void handleUnknownUpdate(Update update) {
        log.warn("Получен неизвестный тип апдейта: {}", update);
        // Можно добавить метрики или отправку в мониторинг
    }

    /**
     * Enum для типов апдейтов.
     * Чётко определяет, какие типы апдейтов мы обрабатываем.
     */
    private enum UpdateType {
        POLL_ANSWER,      // Ответы на викторины
        CALLBACK_QUERY,   // Нажатия на inline кнопки
        TEXT_MESSAGE,     // Текстовые сообщения
        UNKNOWN           // Всё остальное
    }

    /**
     * Enum для типов текстовых сообщений.
     * Помогает различать команды, ввод данных и обычные сообщения.
     */
    private enum MessageType {
        START_COMMAND,        // Команда /start
        CHARACTER_NAME_INPUT, // Ввод имени персонажа
        REGULAR_MESSAGE       // Обычные сообщения
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

