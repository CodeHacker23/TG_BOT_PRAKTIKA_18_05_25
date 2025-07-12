package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * StoryStartService — дирижёр старта и главный сценарист вступления.
 * Здесь происходит вся магия первого контакта пользователя с ботом:
 * - обработка команды /start
 * - отправка приветственного фото и сообщения
 * - отправка сообщения с кнопкой "Создать персонажа"
 * - проверка наличия персонажа
 * - ожидание имени персонажа и создание персонажа
 *
 * Почему это вынесено в отдельный сервис? Потому что если ты начнёшь размазывать стартовую логику по разным классам — твой проект станет похож на лапшу быстрого приготовления: быстро, дёшево, но есть невозможно.
 *
 * Пример использования:
 *   storyStartService.handleStart(bot, chatId, userId);
 *   storyStartService.handleCreatePersonage(bot, chatId, userId);
 *   storyStartService.handleCharacterNameInput(bot, chatId, userId, "Вася");
 *
 * Если забудешь добавить логику сюда — Архитектор придёт ночью и перепишет твой код на Brainfuck.
 */
@Service
@RequiredArgsConstructor
public class StoryStartService {
    // Сервис с методами для отправки фото и теории (название "Service" — это боль, не повторяй так)
    private final org.example.Service service;
    // Сервис для централизованной логики создания персонажа
    private final PersonageCreationService personageCreationService;
    // Сервис для работы с пользователями
    public final UserService userService;

    /**
     * Отправка стартового фото с приветствием от БайтФорджа
     * @param chatId — ID чата Telegram
     * @return SendPhoto — стартовая фотка
     *
     * Пример:
     *   SendPhoto photo = storyStartService.photoStart(chatId);
     *   bot.execute(photo);
     */
    public SendPhoto photoStart(Long chatId) {
        System.out.println("[StoryStartService] photoStart() — отправляем стартовую фотку. Пользователь только что зашёл в мультивселенную.");
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
     * @param bot — твой TelegramLongPollingBot
     * @param chatId — ID чата
     * @param userId — ID пользователя
     *
     * Пример:
     *   storyStartService.handleStart(bot, chatId, userId);
     *
     * Если что-то пойдёт не так — лови логи в консоли и готовься к дебагу.
     */
    public void handleStart(TelegramLongPollingBot bot, Long chatId, Long userId) {
        System.out.println("[StoryStartService] handleStart() — стартуем! userId=" + userId);
        // 1. Отправить фото-приветствие
        try {
            SendPhoto photo = this.photoStart(chatId);
            bot.execute(photo);
            System.out.println("[StoryStartService] handleStart() — стартовая фотка отправлена.");
        } catch (TelegramApiException e) {
            System.err.println("[StoryStartService] Ошибка отправки фото на старте: " + e.getMessage());
        }

        // 2. Отправить приветственное сообщение с кнопкой "Создать персонажа"
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("*БайтФордж*\nЯ здесь, чтобы помочь тебе создать своего героя, разобраться в логике и определить любые вызовы.\nГотов ли ты начать свое путешествие?");
            message.setReplyMarkup(KeyboardService.getCreatePersonageInlineKeyboard());
            message.setParseMode("Markdown");
            bot.execute(message);
            System.out.println("[StoryStartService] handleStart() — приветственное сообщение с кнопкой отправлено.");
        } catch (TelegramApiException e) {
            System.err.println("[StoryStartService] Ошибка отправки приветственного сообщения: " + e.getMessage());
        }
    }

    /**
     * Обработка нажатия на кнопку "Создать персонажа"
     * Если персонаж уже есть — отправляет предупреждение, иначе переводит пользователя в режим ожидания имени
     * @param bot — твой TelegramLongPollingBot
     * @param chatId — ID чата
     * @param userId — ID пользователя
     *
     * Пример:
     *   storyStartService.handleCreatePersonage(bot, chatId, userId);
     *
     * Если пользователь уже создал персонажа — не даём ему второй шанс (жизнь — не RPG).
     */
    public void handleCreatePersonage(TelegramLongPollingBot bot, Long chatId, Long userId) {
        System.out.println("[StoryStartService] handleCreatePersonage() — пользователь хочет создать персонажа. userId=" + userId);
        PersonageCreationService.CharacterCreationResult result = personageCreationService.handleCreatePersonageRequest(userId);
        if (!result.canCreate) {
            try {
                bot.execute(new SendMessage(chatId.toString(), "У вас уже есть персонаж, создать нового нельзя."));
                System.out.println("[StoryStartService] handleCreatePersonage() — персонаж уже существует, отказано.");
            } catch (TelegramApiException e) {
                System.err.println("[StoryStartService] Ошибка отправки сообщения о наличии персонажа: " + e.getMessage());
            }
            return;
        }
        // Ожидание имени персонажа
        try {
            bot.execute(new SendMessage(chatId.toString(), "Придумайте имя для персонажа:"));
            System.out.println("[StoryStartService] handleCreatePersonage() — запрошено имя персонажа.");
        } catch (TelegramApiException e) {
            System.err.println("[StoryStartService] Ошибка отправки запроса имени персонажа: " + e.getMessage());
        }
    }

    /**
     * Обработка ввода имени персонажа пользователем
     * Если персонаж уже есть — отправляет предупреждение
     * Если нет — создаёт персонажа, сохраняет его и отправляет карточку
     * @param bot — твой TelegramLongPollingBot
     * @param chatId — ID чата
     * @param userId — ID пользователя
     * @param characterName — имя персонажа, которое ввёл пользователь
     *
     * Пример:
     *   storyStartService.handleCharacterNameInput(bot, chatId, userId, "Вася");
     *
     * Если пользователь уже создал персонажа — второй раз не получится (даже если очень хочется).
     */
    public void handleCharacterNameInput(TelegramLongPollingBot bot, Long chatId, Long userId, String characterName) {
        System.out.println("[StoryStartService] handleCharacterNameInput() — пользователь вводит имя персонажа: " + characterName + ", userId=" + userId);
        UserEntity user = userService.getUserByTgId(userId);
        if (user == null || personageCreationService.hasCharacter(user)) {
            try {
                bot.execute(new SendMessage(chatId.toString(), "Вы уже создали персонажа, изменить его нельзя."));
                System.out.println("[StoryStartService] handleCharacterNameInput() — персонаж уже существует, отказано.");
            } catch (TelegramApiException e) {
                System.err.println("[StoryStartService] Ошибка отправки сообщения о невозможности изменить персонажа: " + e.getMessage());
            }
            return;
        }

        // Создаём случайного персонажа
        PersonageBase personage = PersonageBase.getRandomPersonage();
        personage.setName(characterName);
        System.out.println("[StoryStartService] handleCharacterNameInput() — создан персонаж типа: " + personage.getClass().getSimpleName() + ", имя: " + characterName);

        // Привязываем персонажа к пользователю и сохраняем энергию
        user.setCharacterType(personage.getClass().getSimpleName());
        user.setCharacterName(characterName);
        user.setState(null);
        user.setEnergy(personage.getEnergy());
        userService.saveUser(user);
        System.out.println("[StoryStartService] handleCharacterNameInput() — пользователь сохранён с новым персонажем.");

        // Отправляем карточку персонажа
        SendPhoto photo = null;
        if (personage instanceof Personage1) {
            photo = ((Personage1) personage).getSendPhotoTheory(chatId);
        } else if (personage instanceof Personage2) {
            photo = ((Personage2) personage).PhotoTheoryFloy(chatId);
        } else if (personage instanceof Personage3) {
            photo = ((Personage3) personage).PhotoTheoryGeks(chatId);
        }
        if (photo != null) {
            try {
                bot.execute(photo);
                System.out.println("[StoryStartService] handleCharacterNameInput() — карточка персонажа отправлена.");
            } catch (TelegramApiException e) {
                System.err.println("[StoryStartService] Ошибка отправки карточки персонажа: " + e.getMessage());
            }
        }
    }

    // Если добавишь новый метод без комментария — Доктор БайтФордж лично напишет тебе в Telegram.
}



