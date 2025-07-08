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

@Service
@RequiredArgsConstructor
public class MessageHandlerService {
    private final org.example.Service service;
    private final ArrayListStoryService arrayListStoryService;
    public final UserService userService;
    private final Map<Long, Boolean> theorySent = new ConcurrentHashMap<>();

    public void handleMessage(TelegramLongPollingBot bot, Message message) throws TelegramApiException {
        String text = message.getText();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();

        // Проверяем статус пользователя
        UserEntity user = userService.getUserById(userId);
        if (user == null) {
            user = new UserEntity();
            user.setTgId(userId);
            userService.saveUser(user);
        }
        if (user != null && "AWAITING_CHARACTER_NAME".equals(user.getState())) {
            // Пользователь должен ввести имя персонажа
            String characterName = text;
            // Получаем случайного персонажа
            PersonageBase personage = PersonageBase.getRandomPersonage();
            personage.setName(characterName);
            // Привязываем персонажа к пользователю
            user.setCharacterType(personage.getClass().getSimpleName());
            user.setCharacterName("*" + characterName + "*");
            user.setState(null); // сбрасываем статус
            userService.saveUser(user);
            // Отправляем фото соответствующего персонажа
            SendPhoto photo = null;
            if (personage instanceof Personage1) {
                photo = ((Personage1) personage).getSendPhotoTheory(chatId);
            } else if (personage instanceof Personage2) {
                photo = ((Personage2) personage).PhotoTheoryFloy(chatId);
            }
            if (photo != null) {
                bot.execute(photo);
            }
            return;
        }

        try {
            switch (text) {
                case "Да" -> {  //отправляется теория с фотографиее о том как работает ArrayList
                    arrayListStoryService.sendTheory(bot, chatId);
                }
                case "Нет" -> { // тут должна запуститься викторина по теме, раз пользователь уверен в своих силах, нужно проверить его по максимум, задать такой эдакий вопрос что бы у него закрались сомнения
                    System.out.println("Вызов superPool для chatId: " + chatId); // Логируем вызов
                    bot.execute(arrayListStoryService.getArrayListSuperQuiz(chatId));
                    arrayListStoryService.sendWithKeyboard(bot, chatId, "Уверен, что не хочешь перечитать теорию?");
                }
                case "Я изучаю пайтон" -> {
                    bot.execute(arrayListStoryService.getPythonPhoto(chatId));
                    arrayListStoryService.sendWithKeyboard(bot, chatId, "Согласен?!");
                }
                case "/ArrayList" -> {
                    processCommand(bot, text, chatId);
                }

                case "Создать персонажа" -> {
                    // Установить состояние пользователя
                    user.setState("AWAITING_CHARACTER_NAME");
                    userService.saveUser(user);
                    // Отправить сообщение с просьбой ввести имя
                    bot.execute(new SendMessage(chatId.toString(), "Придумайте имя для персонажа:"));
                }

                default -> {
                    processCommand(bot, text, chatId);
                }
            }
        } catch (TelegramApiException e) {
            System.err.println("Ошибка обработки сообщения для chatId " + chatId + ": " + e.getMessage());
            // Не бросаем исключение дальше, чтобы бот продолжал работать
        }
    }

    public void processCommand(TelegramLongPollingBot bot, String text, Long chatId) {
        try {
            String result = service.getWay(text);
            if (result != null && !result.trim().isEmpty()) {
                org.telegram.telegrambots.meta.api.methods.send.SendMessage sendMessage =
                        new org.telegram.telegrambots.meta.api.methods.send.SendMessage(chatId.toString(), result);
                sendMessage.setParseMode("Markdown"); // Активируем Markdown
                theorySent.compute(chatId, (k, v) -> true);
                Message response = bot.execute(sendMessage);
                if (result.equals(service.getArrayListInfo())) {
                    arrayListStoryService.scheduleMessageDeletion(bot, chatId, response.getMessageId());
                }
            } else {
                System.out.println("Пустой результат для команды: " + text);
            }
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения для chatId " + chatId + ": " + e.getMessage());
            // Не бросаем исключение, чтобы бот продолжал работать
        }
    }

} 