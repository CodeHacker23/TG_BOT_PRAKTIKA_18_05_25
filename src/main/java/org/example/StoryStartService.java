package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * StoryStartService — сервис для управления началом сюжетной линии бота:
 * - обработка команды /start
 * - отправка приветственного фото и сообщения
 * - отправка сообщения с кнопкой "Создать персонажа"
 * - проверка наличия персонажа
 * - ожидание имени персонажа и создание персонажа
 */
@Service
@RequiredArgsConstructor
public class StoryStartService {
    // Сервис с методами для отправки фото и теории
    private final org.example.Service service;
    // Сервис для централизованной логики создания персонажа
    private final PersonageCreationService personageCreationService;
    // Сервис для работы с пользователями
    public final UserService userService;

    /**
     * Отправка стартового фото с приветствием от БайтФорджа
     */
    public SendPhoto photoStart(Long chatId) {
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
     */
    public void handleStart(TelegramLongPollingBot bot, Long chatId, Long userId) {
        // 1. Отправить фото-приветствие
        try {
            SendPhoto photo = this.photoStart(chatId);
            bot.execute(photo);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки фото на старте: " + e.getMessage());
        }

        // 2. Отправить приветственное сообщение с кнопкой "Создать персонажа"
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("*БайтФордж*\nЯ здесь, чтобы помочь тебе создать своего героя, разобраться в логике и определить любые вызовы.\nГотов ли ты начать свое путешествие?");
            message.setReplyMarkup(KeyboardService.getCreatePersonageInlineKeyboard());
            message.setParseMode("Markdown");
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки приветственного сообщения: " + e.getMessage());
        }
    }

    /**
     * Обработка нажатия на кнопку "Создать персонажа"
     * Если персонаж уже есть — отправляет предупреждение, иначе переводит пользователя в режим ожидания имени
     */
    public void handleCreatePersonage(TelegramLongPollingBot bot, Long chatId, Long userId) {
        PersonageCreationService.CharacterCreationResult result = personageCreationService.handleCreatePersonageRequest(userId);
        if (!result.canCreate) {
            try {
                bot.execute(new SendMessage(chatId.toString(), "У вас уже есть персонаж, создать нового нельзя."));
            } catch (TelegramApiException e) {
                System.err.println("Ошибка отправки сообщения о наличии персонажа: " + e.getMessage());
            }
            return;
        }
        // Ожидание имени персонажа
        try {
            bot.execute(new SendMessage(chatId.toString(), "Придумайте имя для персонажа:"));
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки запроса имени персонажа: " + e.getMessage());
        }
    }

    /**
     * Обработка ввода имени персонажа пользователем
     * Если персонаж уже есть — отправляет предупреждение
     * Если нет — создаёт персонажа, сохраняет его и отправляет карточку
     */
    public void handleCharacterNameInput(TelegramLongPollingBot bot, Long chatId, Long userId, String characterName) {
        UserEntity user = userService.getUserByTgId(userId);
        if (user == null || personageCreationService.hasCharacter(user)) {
            try {
                bot.execute(new SendMessage(chatId.toString(), "Вы уже создали персонажа, изменить его нельзя."));
            } catch (TelegramApiException e) {
                System.err.println("Ошибка отправки сообщения о невозможности изменить персонажа: " + e.getMessage());
            }
            return;
        }

        // Создаём случайного персонажа
        PersonageBase personage = PersonageBase.getRandomPersonage();
        personage.setName(characterName);

        // Привязываем персонажа к пользователю и сохраняем энергию
        user.setCharacterType(personage.getClass().getSimpleName());
        user.setCharacterName(characterName);
        user.setState(null);
        user.setEnergy(personage.getEnergy());
        userService.saveUser(user);

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
            } catch (TelegramApiException e) {
                System.err.println("Ошибка отправки карточки персонажа: " + e.getMessage());
            }
        }
    }
}



