package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.MarkdownUtil;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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

        // Создаём случайного персонажа
        PersonageBase personage = PersonageBase.getRandomPersonage();
        personage.setName(characterName);
        log.info("handleCharacterNameInput() — создан персонаж типа: {}, имя: {}", personage.getClass().getSimpleName(), characterName);

        // Привязываем персонажа к пользователю и сохраняем энергию
        user.setCharacterType(personage.getClass().getSimpleName());
        user.setCharacterName(characterName);
        user.setState(null);
        user.setEnergy(personage.getEnergy());
        userService.saveUser(user);
        log.info("handleCharacterNameInput() — пользователь сохранён с новым персонажем.");

        // Отправляем карточку персонажа
        SendPhoto photo = null;
        switch (personage) {
            case Personage1 personage1 -> {
                photo = personage1.getSendPhotoTheory(chatId);//отправялем персонажа 1
                //метод отправки отложеного  фото Байта с вертолетом на 4 сек
                scheduler.schedule(() -> {
                    SendPhoto photoWithKeyboard = sendByteFordj(chatId);
                    photoWithKeyboard.setReplyMarkup(KeyboardService.KeyboardHelicopter( ));
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

    //метод отправки фото
    public SendPhoto sendByteFordj(Long chatId) {
        log.info("sendByteFordj() — вызывается для chatId={}", chatId);
        log.info("sendByteFordj() — подготовка фото Форджа с вертолетом.");
        return photoService.getByteFordj(chatId);
    }


    public static SendMessage ByteFordjProgrammer(Long chatId){
        log.info("ByteFordjProgrammer() — вызывается для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("\uD83D\uDCBE Перед запуском своей первой программы ты должен понять, что такое структура.\n" +
                "⚔\uFE0F Именно поэтому тебя ждёт особая симуляция.");
        sendMessage.setReplyMarkup(KeyboardService.KeyboardPlot());
        log.info("ByteFordjProgrammer() — сообщение подготовлено и возвращается.");
        return sendMessage;
    }

    //отправка ответа на сообщение от пользвателя кнопки (какую?)
    public static SendMessage sendWhich(Long chatId){
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
        log.info("sendWhich() — сообщение подготовлено и возвращается.");
        return sendMessage;
    }

   

    // Если добавишь новый метод без комментария — Доктор БайтФордж лично напишет тебе в Telegram.
}



