package org.example.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.MarkdownUtil;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.model.personage.PersonageBase;
import org.example.repository.PersonageRepository;
import org.example.service.PhotoService.PhotoStart;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.example.bot.KeyboardService.KeyboardService;
import org.example.model.personage.Personage1;
import org.example.model.personage.Personage2;
import org.example.model.personage.Personage3;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;


/**
 * StoryStartService — дирижёр старта и главный сценарист вступления.
 * Здесь происходит вся магия первого контакта пользователя с ботом:
 * - обработка команды /start
 * - отправка приветственного фото и сообщения
 * - отправка сообщения с кнопкой "Создать персонажа"
 * - проверка наличия персонажа
 * - ожидание имени персонажа и создание персонажа
 * <p>
 * Почему это вынесено в отдельный сервис? Потому что если ты начнёшь размазывать стартовую логику по разным классам — твой проект станет похож на лапшу быстрого приготовления: быстро, дёшево, но есть невозможно.
 * <p>
 * Пример использования:
 * storyStartService.handleStart(bot, chatId, userId);
 * storyStartService.handleCreatePersonage(bot, chatId, userId);
 * storyStartService.handleCharacterNameInput(bot, chatId, userId, "Вася");
 * <p>
 * Если забудешь добавить логику сюда — Архитектор придёт ночью и перепишет твой код на Brainfuck.
 */
@Service
@RequiredArgsConstructor
public class StoryStartService {
    private static final Logger log = LoggerFactory.getLogger(StoryStartService.class);
    // Сервис с методами для отправки фото и теории (название "Service" — это боль, не повторяй так)
    private final org.example.Service service;
    // Сервис для централизованной логики создания персонажа
    private final PersonageCreationService personageCreationService;
    // Сервис для работы с пользователями
    public final UserService userService;
    public final PhotoStart photoStart;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final PersonageRepository personageRepository;
    private final PersonageService personageService;

    // === Мапа для маршрутизации команд начальной сюжетной ветки ===
    // Ключ — текст команды, значение — обработчик (BiConsumer<бот, сообщение>)
    private final Map<String, BiConsumer<TelegramLongPollingBot, Message>> startCommands = new HashMap<>();

    // === Инициализация мапы команд ===
    @PostConstruct
    public void init() {
        // Приветствие
        startCommands.put("/start", (bot, msg) -> handleStart(bot, msg.getChatId(), msg.getFrom().getId()));
        // Кнопка "Создать персонажа"
        startCommands.put("Создать персонажа", (bot, msg) -> handleCreatePersonage(bot, msg.getChatId(), msg.getFrom().getId()));
        // Продолжить путь программиста
        startCommands.put("✅ Продолжить путь программиста", (bot, msg) -> sendMsg(bot, ByteFordjProgrammer(msg.getChatId())));
        // Какая?
        startCommands.put("Какая?", (bot, msg) -> sendMsg(bot, sendWhich(msg.getChatId())));
        // Я готов
        startCommands.put("Я готов✅", (bot, msg) -> sendMsg(bot, ByteFordjParting(msg.getChatId())));
        // Что будет со мной?
        startCommands.put("❓ Но что будет со мной?", (bot, msg) -> sendMsg(bot, ByteFordjAnswerTwo(msg.getChatId())));
        // Сбежать от компиляции — отправляем текст, а через 2 секунды фото с мемом
        startCommands.put("❌ Сбежать от компиляции", (bot, msg) -> {
            // 1. Сразу отправляем первое сообщение
            sendMsg(bot, sendEscapeTwoText(msg.getChatId()));
            // 2. Через 2 секунды отправляем фото с подписью 'Симуляция Sys.exit(0)...'
            scheduler.schedule(() -> {
                try {
                    bot.execute(PhotoStart.photoJava6(msg.getChatId()));
                } catch (TelegramApiException e) {
                    log.error("Ошибка отправки фото 'Симуляция Sys.exit(0)': {}", e.getMessage());
                }
            }, 2, TimeUnit.SECONDS);
        });
        // Обновить IDE — отправляем два сообщения с задержкой и кнопкой
        startCommands.put("Я лучше пойду обновлю IDE", (bot, msg) -> {
            // 1. Сразу отправляем первое сообщение
            sendMsg(bot, IDEtext(msg.getChatId()));
            // 2. Через 2 секунды отправляем второе сообщение с кнопкой
            scheduler.schedule(() -> {
                sendMsg(bot, IDEtext2(msg.getChatId()));
            }, 2, TimeUnit.SECONDS);
        });
        // Принять судьбу программиста
        startCommands.put("✅ Принять судьбу программиста", (bot, msg) -> sendMsg(bot, ByteFordjProgrammer(msg.getChatId())));
        // Вернуться и скомпилироваться
        startCommands.put("\uD83D\uDCCE Вернуться и скомпилироваться", (bot, msg) -> sendMsg(bot, ByteFordjProgrammer(msg.getChatId())));
        // К чёрту NetBeans. Я готов к Риму! — отправляем фото персонажа с обновлёнными характеристиками
        startCommands.put("☕️ К чёрту NetBeans. Я готов к Риму!", (bot, msg) -> {
            Long chatId = msg.getChatId();
            Long userId = msg.getFrom().getId();
            log.info("Пользователь выбрал 'К чёрту NetBeans. Я готов к Риму!' chatId={}, userId={}", chatId, userId);
            UserEntity user = userService.getUserByTgId(userId);
            if (user == null) {
                sendMsg(bot, new SendMessage(chatId.toString(), "Ошибка: пользователь не найден!"));
                return;
            }
            PersonageEntity personage = user.getPersonage();
            if (personage == null) {
                sendMsg(bot, new SendMessage(chatId.toString(), "Ошибка: персонаж не найден!"));
                return;
            }
            // Определяем тип персонажа
            String type = personage.getCharacterType();
            // Сохраняем старое значение аналитики/коммуникаций/оптимизации
            int oldAnalytics = personage.getAnalytics() != null ? personage.getAnalytics() : 0;
            int oldCommunication = personage.getCommunication() != null ? personage.getCommunication() : 0;
            int oldOptimization = personage.getOptimization() != null ? personage.getOptimization() : 0;
            // Обновляем характеристики через personageService (рандом для аналитики)
            personageService.updateStats(
                    personage,
                    1,      // levelDelta
                    50,     // achievementDelta
                    450.0,  // currencyDelta
                    27,     // analyticsDelta (min)
                    40,     // analyticsMax (max)
                    true    // randomAnalytics
            );
            // Считаем дельту
            int analyticsDelta = (personage.getAnalytics() != null ? personage.getAnalytics() : 0) - oldAnalytics;
            int communicationDelta = (personage.getCommunication() != null ? personage.getCommunication() : 0) - oldCommunication;
            int optimizationDelta = (personage.getOptimization() != null ? personage.getOptimization() : 0) - oldOptimization;
            // Создаём объект нужного персонажа и заполняем его из сущности
            try {
                if ("Personage1".equals(type)) {
                    Personage1 p1 = new Personage1();
                    p1.fillFromEntity(personage);
                    bot.execute(p1.getRomanArmorCard(chatId, analyticsDelta));
                } else if ("Personage2".equals(type)) {
                    Personage2 p2 = new Personage2();
                    p2.fillFromEntity(personage);
                    bot.execute(p2.getRomanFloy(chatId, communicationDelta));
                } else if ("Personage3".equals(type)) {
                    Personage3 p3 = new Personage3();
                    p3.fillFromEntity(personage);
                    bot.execute(p3.getRomanPersonage3(chatId, optimizationDelta));
                } else {
                    sendMsg(bot, new SendMessage(chatId.toString(), "Ошибка: неизвестный тип персонажа!"));
                }
            } catch (TelegramApiException e) {
                log.error("Ошибка отправки фото персонажа для Рима: {}", e.getMessage());
            }
        });
        // ... добавь остальные команды начальной ветки по аналогии
    }

    /**
     * Проверяет, может ли StoryStartService обработать данную команду
     * @param text — текст сообщения пользователя
     * @return true, если команда есть в Map
     */
    public boolean canHandle(String text) {
        return startCommands.containsKey(text);
    }

    /**
     * Обрабатывает команду начальной сюжетной ветки через Map
     * @param bot — TelegramLongPollingBot
     * @param message — объект Message от Telegram
     */
    public void handle(TelegramLongPollingBot bot, org.telegram.telegrambots.meta.api.objects.Message message) {
        String text = message.getText();
        if (startCommands.containsKey(text)) {
            log.info("StoryStartService: обработка команды '{}', chatId={}, userId={}", text, message.getChatId(), message.getFrom().getId());
            startCommands.get(text).accept(bot, message);
        } else {
            log.warn("StoryStartService: команда '{}' не найдена в Map", text);
        }
    }

    /**
     * Вспомогательный метод для отправки сообщений с логированием ошибок
     */
    private void sendMsg(TelegramLongPollingBot bot, SendMessage msg) {
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage());
        }
    }

    /**
     * Обработка команды /start: отправка фото и приветственного сообщения с кнопкой
     *
     * @param bot    — твой TelegramLongPollingBot
     * @param chatId — ID чата
     * @param userId — ID пользователя
     *               <p>
     *               Пример:
     *               storyStartService.handleStart(bot, chatId, userId);
     *               <p>
     *               Если что-то пойдёт не так — лови логи в консоли и готовься к дебагу.
     */
    public void handleStart(TelegramLongPollingBot bot, Long chatId, Long userId) {
        log.info("handleStart() — стартуем! userId={}", userId);
        // 1. Отправить фото-приветствие
        try {
            SendPhoto photo =   PhotoStart.photoStart(chatId);
            bot.execute(photo);
            log.info("handleStart() — стартовая фотка отправлена.");
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки фото на старте: " + e.getMessage());
        }

        // 2. Отправить приветственное сообщение с кнопкой "Создать персонажа"
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("*БайтФордж*\nЯ здесь, чтобы помочь тебе создать своего героя, разобраться в логике и определить любые вызовы.\nГотов ли ты начать свое путешествие?");
            message.setReplyMarkup(KeyboardService.getCreatePersonageInlineKeyboard());
            message.setParseMode("Markdown");
            bot.execute(message);
            log.info("handleStart() — приветственное сообщение с кнопкой отправлено.");
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки приветственного сообщения: " + e.getMessage());
        }
    }



    /**
     * Обработка нажатия на кнопку "Создать персонажа"
     * Если персонаж уже есть — отправляет предупреждение, иначе переводит пользователя в режим ожидания имени
     *
     * @param bot    — твой TelegramLongPollingBot
     * @param chatId — ID чата
     * @param userId — ID пользователя
     *               <p>
     *               Пример:
     *               storyStartService.handleCreatePersonage(bot, chatId, userId);
     *               <p>
     *               Если пользователь уже создал персонажа — не даём ему второй шанс (жизнь — не RPG).
     */
    public void handleCreatePersonage(TelegramLongPollingBot bot, Long chatId, Long userId) {
        log.info("handleCreatePersonage() — пользователь хочет создать персонажа. userId={}", userId);
        PersonageCreationService.CharacterCreationResult result = personageCreationService.handleCreatePersonageRequest(userId);
        if (!result.canCreate) {
            try {
                bot.execute(new SendMessage(chatId.toString(), "У вас уже есть персонаж, создать нового нельзя."));
                log.info("handleCreatePersonage() — персонаж уже существует, отказано.");
            } catch (TelegramApiException e) {
                log.error("Ошибка отправки сообщения о наличии персонажа: " + e.getMessage());
            }
            return;
        }
        // Ожидание имени персонажа
        try {
            bot.execute(new SendMessage(chatId.toString(), "Придумайте имя для персонажа:"));
            log.info("handleCreatePersonage() — запрошено имя персонажа.");
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки запроса имени персонажа: " + e.getMessage());
        }
    }

    /**
     * Обработка ввода имени персонажа пользователем
     * Если персонаж уже есть — отправляет предупреждение
     * Если нет — создаёт персонажа, сохраняет его и отправляет карточку
     *
     * @param bot           — твой TelegramLongPollingBot
     * @param chatId        — ID чата
     * @param userId        — ID пользователя
     * @param characterName — имя персонажа, которое ввёл пользователь
     *                      <p>
     *                      Пример:
     *                      storyStartService.handleCharacterNameInput(bot, chatId, userId, "Вася");
     *                      <p>
     *                      Если пользователь уже создал персонажа — второй раз не получится (даже если очень хочется).
     */
    public void handleCharacterNameInput(TelegramLongPollingBot bot, Long chatId, Long userId, String characterName) {
        log.info("handleCharacterNameInput() — пользователь вводит имя персонажа: {}, userId={}", characterName, userId);
        UserEntity user = userService.getUserByTgId(userId);
        if (user == null || personageCreationService.hasCharacter(user)) {
            try {
                bot.execute(new SendMessage(chatId.toString(), "Вы уже создали персонажа, изменить его нельзя."));
                log.info("handleCharacterNameInput() — персонаж уже существует, отказано.");
            } catch (TelegramApiException e) {
                log.error("Ошибка отправки сообщения о невозможности изменить персонажа: " + e.getMessage());
            }
            return;
        }

        // Создаём случайного персонажа (PersonageBase)
        PersonageBase personageBase = PersonageBase.getRandomPersonage();
        personageBase.setName(characterName);
        log.info("handleCharacterNameInput() — создан персонаж типа: {}, имя: {}", personageBase.getClass().getSimpleName(), characterName);

        // Преобразуем и сохраняем персонажа
        PersonageEntity personageEntity = createPersonageEntityFromBase(personageBase, user, characterName);
        personageRepository.save(personageEntity);

        // Сбросить состояние пользователя после создания персонажа
        user.setState(null);
        userService.saveUser(user);

        log.info("handleCharacterNameInput() — персонаж сохранён в базе данных и состояние пользователя сброшено.");

        // Отправляем карточку персонажа
        SendPhoto photo = null;
        switch (personageBase) {
            case Personage1 personage1 -> {
                photo = personage1.getSendPhotoTheory(chatId);//отправялем персонажа 1
                //метод отправки отложеного  фото Байта с вертолетом на 4 сек
                scheduler.schedule(() -> {
                    SendPhoto photoWithKeyboard = sendByteFordj(chatId);
                    photoWithKeyboard.setReplyMarkup(KeyboardService.KeyboardHelicopter());
                    try {
                        bot.execute(photoWithKeyboard); // отправляем фото с кнопками!
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }, 4, TimeUnit.SECONDS);
            }

            case Personage2 personage2 -> {
                photo = personage2.PhotoTheoryFloy(chatId);
                //метод отправки отложеного  фото Байта с вертолетом на 4 сек
                scheduler.schedule(() -> {
                    SendPhoto photoWithKeyboard = sendByteFordj(chatId);
                    photoWithKeyboard.setReplyMarkup(KeyboardService.KeyboardHelicopter());
                    try {
                        bot.execute(photoWithKeyboard); // отправляем фото с кнопками!
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }, 4, TimeUnit.SECONDS);
            }
            case Personage3 personage3 -> {
                photo = personage3.PhotoTheoryGeks(chatId);
                //метод отправки отложеного  фото Байта с вертолетом на 4 сек
                scheduler.schedule(() -> {
                    SendPhoto photoWithKeyboard = sendByteFordj(chatId);
                    photoWithKeyboard.setReplyMarkup(KeyboardService.KeyboardHelicopter());
                    try {
                        bot.execute(photoWithKeyboard); // отправляем фото с кнопками!
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }, 4, TimeUnit.SECONDS);
            }
            default -> {
            }
        }
        if (photo != null) {
            try {
                bot.execute(photo);
                log.info("handleCharacterNameInput() — карточка персонажа отправлена.");
            } catch (TelegramApiException e) {
                log.error("Ошибка отправки карточки персонажа: " + e.getMessage());
            }
        }

    }

    /**
     * Преобразует PersonageBase в PersonageEntity и привязывает к пользователю
     * @param base — базовый персонаж (Personage1, 2, 3)
     * @param user — пользователь
     * @param characterName — имя персонажа
     * @return PersonageEntity с заполненными полями
     */
    private PersonageEntity createPersonageEntityFromBase(PersonageBase base, UserEntity user, String characterName) {
        PersonageEntity entity = new PersonageEntity();
        entity.setUser(user);
        entity.setTgId(user.getTgId());
        entity.setCharacterType(base.getClass().getSimpleName());
        entity.setName(characterName);
        entity.setLevel(base.getLevel());
        entity.setEnergy(base.getEnergy());
        entity.setAchievementPoints(base.getAchievementPoints());
        entity.setCurrency(base.getCurrency());
        entity.setStatus(base.getStatus());
        // Уникальные поля для каждого типа персонажа
        if (base instanceof Personage1 p1) {
            entity.setDeadlineResistance(p1.getDeadlineResistance());
            entity.setAnalytics(p1.getAnalytics());
        } else if (base instanceof Personage2 p2) {
            entity.setHumor(p2.getHumor());
            entity.setCommunication(p2.getCommunication());
        } else if (base instanceof Personage3 p3) {
            entity.setCodeAccuracy(p3.getCodeAccuracy());
            entity.setOptimization(p3.getOptimization());
        }
        // TODO: добавить обработку уникальных полей для Personage2, Personage3, если появятся
        return entity;
    }

    //метод отправки фото
    public SendPhoto sendByteFordj(Long chatId) {
        log.info("sendByteFordj() — вызывается для chatId={}", chatId);
        log.info("sendByteFordj() — подготовка фото Форджа с вертолетом.");
        return photoStart.getByteFordj(chatId);
    }

    //отправка далее сообщения по 1 сюжету
    public static SendMessage ByteFordjProgrammer(Long chatId) {
        log.info("ByteFordjProgrammer() — вызывается для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("\uD83D\uDCBE Перед запуском своей первой программы ты должен понять, что такое структура.\n" +
                "⚔\uFE0F Именно поэтому тебя ждёт особая симуляция.");
        sendMessage.setReplyMarkup(KeyboardService.KeyboardPlot()); //кнопка 'какая?' вызывается
        log.info("ByteFordjProgrammer() — сообщение подготовлено и возвращается.");
        return sendMessage;
    }

    //отправка ответа на сообщение от пользвателя кнопки (какую?) по 1 сюжету
    public static SendMessage sendWhich(Long chatId) {
        log.info("sendWhich() — вызывается для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("MarkdownV2");
        // Формируем части сообщения с правильным экранированием
        String bold = "*" + MarkdownUtil.escapeMarkdownV2("БайтФордж:") + "*";
        String spoiler = "||" + MarkdownUtil.escapeMarkdownV2("Римскую проекцию Java-машины.") + "||";
        String body = MarkdownUtil.escapeMarkdownV2(
                "Это древний мир, где алгоритмы маскируются под воинов,\n" +
                        "а коллекции — под боевые порядки.\n\n" +
                        "Добро пожаловать в... ");
        String text = bold + "\n\n" + body + spoiler;
        sendMessage.setText(text);
        log.info("KeyboardService.KeyboardReady()  — обработка кнопки 'я готов'.");
        sendMessage.setReplyMarkup(KeyboardService.KeyboardReady());
        log.info("sendWhich() — сообщение подготовлено и возвращается.");

        return sendMessage;
    }

    //неизменяемый текст для двух сюжетных линий что 1 что 2
    public static SendMessage ByteFordjParting(Long chatId) {
        log.info("ByteFordjParting() — вызывается для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("MarkdownV2");
        String name = "*" + "БайтФордж" + "*";
        String cursiv = "_" + MarkdownUtil.escapeMarkdownV2("Сынок…") + "_";
        String body = MarkdownUtil.escapeMarkdownV2(
                "Я полагаю на тебя большие надежды. Не подведи.\n" +
                        "Даже если тебе встретится злой Set, двойной Map или мутировавший LinkedList...\n" +
                        "Просто помни: каждая структура — это инструмент.\n" +
                        "Вопрос в том, кто его держит."
        );
        String text = name + "\n\n" + cursiv + "\n" + body;
        sendMessage.setText(text);

        log.info("ByteFordjParting()  — обработка кнопки 'Войти во врата Рима'. Вызывается для chatId={}", chatId);
        sendMessage.setReplyMarkup(KeyboardService.finalGatesOfRome());

        log.info("ByteFordjParting() — сообщение подготовлено и возвращается.");
        return sendMessage;
    }

    //ответ на 2 линию сюжета если пользователь нажал на кнопку (что будет со мной)
    public static SendMessage ByteFordjAnswerTwo(Long chatId) {
        log.info(" ByteFordjAnswerTwo — вызывается для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*БайтФордж:*\n" +
                "\n" +
                "Невозможно предсказать...\n" +
                "Возможно, ты станешь мастером кода.\n" +
                "А может — попадёшь в NullPointerException вечной практики.\n" +
                "\n" +
                "_Но помни: именно ты компилируешь свой путь._\n" +
                "_Удача любит тех, кто не боится дебага._");
        sendMessage.setReplyMarkup(KeyboardService.userChoice(chatId));
        return sendMessage;
    }


    //обработка кнопки Сбежать с симуляции
    public static SendMessage sendEscape(Long chatId) {
        log.info("sendEscape — вызывается для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*БайтФордж:*\n" +
                "Сбежать?\n" +
                "Когда в тебе уже пульсирует поток?  \n" +
                "Когда GC уже следит за тобой?\n");
        sendMessage.setReplyMarkup(KeyboardService.userChoice(chatId));
        return sendMessage;
    }

    public static SendMessage sendEscapeTwoText(Long chatId) {
        log.info("sendEscapeTwoText — вызывается для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*БайтФордж:*\n" +
                "Ладно… Я дам тебе шанс. \n" +
                "Один… последний… стек отхода, где прячутся те, кто не решился");

        return sendMessage;
    }


    public static SendMessage IDEtext(Long chatId) {
        log.info("IDEtext — вызывается для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус*\n\n" +
                "Идея хорошая.\n" +
                "Только не забудь:\n" +
                "Однажды парень тоже решил «обновить IDE» . \n");
        return sendMessage;
    }

    public static SendMessage IDEtext2(Long finalChatId1) {
        log.info("IDEtext2 — вызывается для chatId={}", finalChatId1);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(finalChatId1);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус*\n\n" +
                "Через 2 часа он оказался в Eclipse\n" +
                "без света, без плагинов… и без веры.\n\n" +
                "Осталась только тьма…\n" +
                "и рабочий стол в *NetBeans.*\n");
        sendMessage.setReplyMarkup(KeyboardService.KeyboardIDE(finalChatId1));
        return sendMessage;
    }



    // Если добавишь новый метод без комментария — Доктор БайтФордж лично напишет тебе в Telegram.
}



