package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.ArrayListStoryService.getArrayListInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MessageHandlerService — главный обработчик всех входящих сообщений пользователя (кроме /start и создания персонажа).
 * Здесь происходит разруливание сценариев: теория, викторины, ответы, переходы между состояниями.
 *
 * Почему нельзя лепить всё в Bot? Потому что иначе твой код быстро превратится в ад для дебага.
 *
 * Пример расширения:
 *   - Хочешь добавить новый сценарий (например, LinkedList)? Делай отдельный StoryService и вызывай его отсюда.
 *   - Для новых команд — добавляй case в switch, но делегируй логику!
 *
 * Юмор: если начнёшь писать 100 if-ов подряд — Архитектор лично напишет тебе в Telegram.
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


    /**
     * Проверяет, есть ли у пользователя персонаж
     * @param user — сущность пользователя
     * @return true, если персонаж уже создан
     */
    private boolean hasCharacter(UserEntity user) {
        return user != null && user.getCharacterType() != null && !user.getCharacterType().isEmpty();
    }

    /**
     * Обрабатывает ввод имени персонажа пользователем
     * @param bot — TelegramLongPollingBot
     * @param chatId — ID чата
     * @param userId — ID пользователя
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
            PersonageBase personage = PersonageBase.getRandomPersonage();
            personage.setName(characterName);
            user.setCharacterType(personage.getClass().getSimpleName());
            user.setCharacterName("*" + characterName + "*");
            user.setState(null);
            user.setEnergy(8);
            userService.saveUser(user);
            log.info("Создан персонаж: {} для пользователя: {}", user.getCharacterType(), user.getTgId());
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
     * @param bot — TelegramLongPollingBot
     * @param message — объект Message от Telegram
     *
     * Пример:
     *   messageHandlerService.handleMessage(bot, message);
     */
    public void handleMessage(TelegramLongPollingBot bot, Message message) throws TelegramApiException {
        String text = message.getText();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        log.info("handleMessage() — получено сообщение: '{}' от userId={}, chatId={}", text, userId, chatId);

        // Проверяем статус пользователя
        UserEntity user = userService.getUserByTgId(userId);
        if (user == null) {
            log.info("Новый пользователь, создаём UserEntity для userId={}", userId);
            user = new UserEntity();
            user.setTgId(userId);
            userService.saveUser(user);
        }
        // Проверка персонажа на состояние пользователя
        if (user != null && "AWAITING_CHARACTER_NAME".equals(user.getState())) {
            if(hasCharacter(user)){
                log.info("Попытка повторного создания персонажа для пользователя: {}", user.getTgId());
                SendMessage alreadyCreated = new SendMessage(chatId.toString(), "Вы уже создали персонажа, изменить его нельзя.");
                bot.execute(alreadyCreated);
                // Сбросить состояние, если вдруг оно осталось
                user.setState(null);
                userService.saveUser(user);
                return;
            }
            // Пользователь должен ввести имя персонажа
            String characterName = text;
            log.info("Пользователь вводит имя персонажа: '{}'", characterName);
            // Получаем случайного персонажа
            processCharacterNameInput(bot, chatId, userId, characterName);
            return;
        }

        try {
            switch (text) {
                case "Да" -> {
                    log.info("Пользователь выбрал 'Да' — отправляем теорию по ArrayList.");
                    arrayListStoryService.sendTheory(bot, chatId);
                }
                case "Нет" -> {
                    log.info("Пользователь выбрал 'Нет' — отправляем викторину по ArrayList.");
                    bot.execute(arrayListStoryService.getArrayListSuperQuiz(chatId));
                    arrayListStoryService.sendWithKeyboard(bot, chatId, "Уверен, что не хочешь перечитать теорию?");
                }
                case "Я изучаю пайтон" -> {
                    log.info("Пользователь выбрал 'Я изучаю пайтон' — отправляем Python-фото.");
                    bot.execute(arrayListStoryService.getPythonPhoto(chatId));
                    arrayListStoryService.sendWithKeyboard(bot, chatId, "Согласен?!");
                }
                case "/ArrayList" -> {
                    log.info("Пользователь отправил /ArrayList — отправляем теорию.");
                    processCommand(bot, text, chatId);
                }
                case "Создать персонажа" -> {
                    log.info("Пользователь выбрал 'Создать персонажа'.");
                    PersonageCreationService.CharacterCreationResult result = personageCreationService.handleCreatePersonageRequest(userId);
                    if (!result.canCreate) {
                        log.info("У пользователя уже есть персонаж, создание нового запрещено.");
                        bot.execute(new SendMessage(chatId.toString(), "У вас уже есть персонаж, создать нового нельзя."));

                        return;
                    }
                    log.info("Просим пользователя ввести имя персонажа.");
                    bot.execute(new SendMessage(chatId.toString(), "Придумайте имя для персонажа:"));
                }
                case "✅ Продолжить путь программиста" -> {
                    log.info("Пользователь выбрал 'Продолжить путь программиста'.");
                    chatId = message.getChatId();
                    bot.execute( StoryStartService.ByteFordjProgrammer(chatId));
                }
                case "Какая?" -> {
                    log.info("Пользователь выбрал 'Какая?'.");
                    chatId = message.getChatId();
                    bot.execute(StoryStartService.sendWhich(chatId));
                    // Убираем клавиатуру после ответа
                    SendMessage remove = new SendMessage();
                    remove.setChatId(chatId);
                    remove.setText(" ");
                    remove.setReplyMarkup(KeyboardService.removeKeyboard());
                    bot.execute(remove);
                }

                case "Я готов✅" ->{
                    log.info("Пользователь выбрал 'Я готов ✅'");
                    chatId = message.getChatId();
                    bot.execute(StoryStartService.ByteFordjParting(chatId));
                    // Убираем клавиатуру после ответа
                    SendMessage remove = new SendMessage();
                    remove.setChatId(chatId);
                    remove.setText(" ");
                    remove.setReplyMarkup(KeyboardService.removeKeyboard());
                    bot.execute(remove);

                }
                default -> {
                    log.info("Неизвестная команда, пробуем обработать через processCommand().");
                    processCommand(bot, text, chatId);
                }
            }
        } catch (TelegramApiException e) {
            log.error("Ошибка обработки сообщения для chatId {}: {}", chatId, e.getMessage());
        }
    }

    /**
     * Обрабатывает команды, не относящиеся к основным сценариям
     * @param bot — TelegramLongPollingBot
     * @param text — текст команды
     * @param chatId — ID чата
     */
    public void processCommand(TelegramLongPollingBot bot, String text, Long chatId) {
        log.info("processCommand() — text='{}', chatId={}", text, chatId);
        try {
            String result = service.getWay(text);
            if (result != null && !result.trim().isEmpty()) {
                log.info("Отправляем результат команды: '{}'", result);
                SendMessage sendMessage = new   SendMessage(chatId.toString(), result);
                sendMessage.setParseMode("Markdown");
                theorySent.compute(chatId, (k, v) -> true);
                Message response = bot.execute(sendMessage);
                if (result.equals(getArrayListInfo())) {
                    arrayListStoryService.scheduleMessageDeletion(bot, chatId, response.getMessageId());
                }
            } else {
                log.info("Пустой результат для команды: {}", text);
            }
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения для chatId {}: {}", chatId, e.getMessage());
        }
    }

    // --- Советы по расширению ---
    // 1. Для новых сценариев (LinkedList, Set и т.д.) делай отдельные StoryService и вызывай их отсюда.
    // 2. Не пиши 100 if-ов подряд — делегируй логику!
    // 3. Для новых команд — добавляй case в switch, но не пихай бизнес-логику прямо сюда.
    // 4. Если логика повторяется — выноси в абстрактные классы/интерфейсы.
    // 5. Если добавишь обработку без комментария — Архитектор лично напишет тебе в Telegram.
} 