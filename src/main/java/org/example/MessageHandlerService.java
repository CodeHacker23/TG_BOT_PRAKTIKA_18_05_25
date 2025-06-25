package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MessageHandlerService {
    private final org.example.Service service;
    private final ArrayListStoryService arrayListStoryService;
    private final Map<Long, Boolean> theorySent = new ConcurrentHashMap<>();

    public void handleMessage(TelegramLongPollingBot bot, Message message) throws TelegramApiException {
        String text = message.getText();
        Long chatId = message.getChatId();
        
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

    public Map<Long, Boolean> getTheorySent() {
        return theorySent;
    }
} 