package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.MarkdownUtil;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.model.personage.PersonageBase;
import org.example.repository.PersonageRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.example.bot.KeyboardService;
import org.example.model.personage.Personage1;
import org.example.model.personage.Personage2;
import org.example.model.personage.Personage3;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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
    public final PhotoService photoService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final PersonageRepository personageRepository;

    /**
     * Отправка стартового фото с приветствием от БайтФорджа
     *
     * @param chatId — ID чата Telegram
     * @return SendPhoto — стартовая фотка
     * <p>
     * Пример:
     * SendPhoto photo = storyStartService.photoStart(chatId);
     * bot.execute(photo);
     */
    public SendPhoto photoStart(Long chatId) {
        log.info("photoStart() — отправляем стартовую фотку. Пользователь только что зашёл в мультивселенную.");
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile("https://ltdfoto.ru/image/soXapU"))
                .caption("Вот и ты здесь, новичок. \n" +
                        "Я — *Доктор БайтФордж* , архитектор программных миров и кузнец идей. \n"
                        + "Ты в мультивселенной по Java...\n" +
                        "Где каждая строка — это шаг,а баг — это урок.")
                .parseMode("Markdown")
                .build();
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
            SendPhoto photo = this.photoStart(chatId);
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
        return photoService.getByteFordj(chatId);
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



