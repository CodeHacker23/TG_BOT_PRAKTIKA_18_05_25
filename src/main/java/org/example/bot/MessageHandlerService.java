package org.example.bot;

import lombok.RequiredArgsConstructor;

import org.example.service.*;
import org.example.service.ArrayList.AudioService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.example.model.entity.UserEntity;
import org.example.model.personage.PersonageBase;
import org.example.model.personage.Personage1;
import org.example.model.personage.Personage2;
import org.example.model.personage.Personage3;
import org.example.service.PersonageService;
import org.example.service.StoryStartService;
import org.example.service.ArrayList.ArrayListStory;

/**
 * Главный обработчик всех входящих сообщений пользователя (кроме /start и создания персонажа).
 * <p>
 * Здесь происходит:
 * - Разруливание сценариев (сюжеты, викторины, ответы, переходы между состояниями).
 * - Делегирование в StoryService, если сценарий сложный.
 * <p>
 * Юмор: если напишешь 100 if-ов подряд — Иларион лично напишет тебе в Telegram и добавит багов.
 * <p>
 * Пример:
 * handleMessage(bot, message); // и пусть он сам разбирается
 */
@Service
@RequiredArgsConstructor
public class MessageHandlerService {
    private static final Logger log = LoggerFactory.getLogger(MessageHandlerService.class);
    private final org.example.Service service;
    private final ArrayListStoryService arrayListStoryService;
    public final UserService userService;
    private final PersonageCreationService personageCreationService;
    private final Map<Long, Boolean> theorySent = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final PersonageService personageService;
    private final StoryStartService storyStartService;
    private final ArrayListStory arrayListStory;
    private final AudioService audioService;


    /**
     * Проверяет, есть ли у пользователя персонаж
     *
     * @param user — сущность пользователя
     * @return true, если персонаж уже создан
     */
    private boolean hasCharacter(UserEntity user) {
        return user != null && user.getPersonage() != null && user.getPersonage().getCharacterType() != null && !user.getPersonage().getCharacterType().isEmpty();
    }

    /**
     * Обрабатывает ввод имени персонажа пользователем
     *
     * @param bot           — TelegramLongPollingBot
     * @param chatId        — ID чата
     * @param userId        — ID пользователя
     * @param characterName — имя персонажа
     */
    private void processCharacterNameInput(TelegramLongPollingBot bot, Long chatId, Long userId, String characterName) throws TelegramApiException {
        UserEntity user = userService.getUserByTgId(userId);
        if (user == null) {
            user = new UserEntity();
            user.setTgId(userId);
            userService.saveUser(user);
        }
        if ("AWAITING_CHARACTER_NAME".equals(user.getState())) {
            if (hasCharacter(user)) {
                SendMessage alreadyCreated = new SendMessage(chatId.toString(), "Вы уже создали персонажа, изменить его нельзя.");

                bot.execute(alreadyCreated);
                user.setState(null);
                userService.saveUser(user);
                log.info("Попытка повторного создания персонажа для пользователя: {}", user.getTgId());
                return;
            }
            // Создание персонажа
            if (user.getPersonage() == null) {
                user.setPersonage(new org.example.model.entity.PersonageEntity());
            }
            PersonageBase personage = PersonageBase.getRandomPersonage();
            personage.setName(characterName);
            user.getPersonage().setCharacterType(personage.getClass().getSimpleName());
            user.getPersonage().setName("*" + characterName + "*");
            user.getPersonage().setEnergy(8);
            userService.saveUser(user);
            log.info("Создан персонаж: {} для пользователя: {}", user.getPersonage() != null ? user.getPersonage().getCharacterType() : "<нет персонажа>", user.getTgId());
            SendPhoto photo = null;
            if (personage instanceof Personage1) {
                photo = ((Personage1) personage).getSendPhotoTheory(chatId);
            } else if (personage instanceof Personage2) {
                photo = ((Personage2) personage).PhotoTheoryFloy(chatId);
            } else if (personage instanceof Personage3) {
                photo = ((Personage3) personage).PhotoTheoryGeks(chatId);
            }
            if (photo != null) {
                bot.execute(photo);
            }
        }
    }

    /**
     * Главный обработчик всех входящих сообщений (кроме /start и создания персонажа)
     *
     * @param bot     — TelegramLongPollingBot
     * @param message — объект Message от Telegram
     *                <p>
     *                Пример:
     *                messageHandlerService.handleMessage(bot, message);
     */
    public void handleMessage(TelegramLongPollingBot bot, Message message) throws TelegramApiException {
        log.info("MessageHandlerService.handleMessage() — ВХОД В МЕТОД");
        
        String text = message.getText();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        log.info("handleMessage() — получено сообщение: '{}' от userId={}, chatId={}", text, userId, chatId);

        // 1. Начальная сюжетная ветка
        if (storyStartService.canHandle(text)) {
            log.info("MessageHandlerService: делегируем команду '{}' в StoryStartService", text);
            storyStartService.handle(bot, message);
            return;
        }

        // 2. Ветка ArrayList (и другие коллекции)
        log.info("MessageHandlerService: Проверяем canHandle для ArrayListStory с текстом '{}'", text);
        if (arrayListStory.canHandle(text)) {
            log.info("MessageHandlerService: делегируем команду '{}' в ArrayListStory", text);
            arrayListStory.handle(bot, message);
            return;
        } else {
            log.warn("MessageHandlerService: ArrayListStory не может обработать команду '{}'", text);
        }

        // 3. Остальные команды (например, processCommand)
        // Удалить метод processCommand полностью
        
        log.info("MessageHandlerService.handleMessage() — ВЫХОД ИЗ МЕТОДА");
    }




    // --- Советы по расширению ---
    // 1. Для новых сценариев (LinkedList, Set и т.д.) делай отдельные StoryService и вызывай их отсюда.
    // 2. Не пиши 100 if-ов подряд — делегируй логику!
    // 3. Для новых команд — добавляй case в switch, но не пихай бизнес-логику прямо сюда.
    // 4. Если логика повторяется — выноси в абстрактные классы/интерфейсы.
    // 5. Если добавишь обработку без комментария — Иларион лично напишет тебе в Telegram.
} 