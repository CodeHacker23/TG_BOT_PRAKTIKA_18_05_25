package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Bot extends TelegramLongPollingBot {
    private Service service;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);  // добаввил метод который удаляет в тг смс
    private final Map<Long, Integer> correctAnswers = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> theorySent = new ConcurrentHashMap<>();

    public Bot(String botToken) {
        super(botToken);
        this.service = new Service();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (!(update.hasMessage() && update.getMessage().hasText())) return;


        //get info request
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        //get answer in text
        String result = service.getWay(text);

        // builder object for answer
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(result);
        sendMessage.setChatId(chatId);

        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        }

    }


    private void handleMessage(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();

        if (text.equals("Да")) {
            sendTheory(chatId);
        } else processCommand(text, chatId);
    }


    private void processCommand(String text, Long chatId) {
        String result = service.getWay(text);
        SendMessage sendMessage = new SendMessage(chatId.toString(), result);

        theorySent.compute(chatId, (k, v) -> true);
        try {
            Message response = execute(sendMessage);
            if (result.equals(service.getArrayListInfo())) {
                scheduleMessageDeletion(chatId, response.getMessageId());
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private void sendTheory(Long chatId) {
        SendMessage theory = new SendMessage();
        theory.setText(service.getArrayListInfo());
        theory.setChatId(chatId);
        try {
            execute(theory);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void scheduleMessageDeletion(Long chatId, Integer messageId) {//метод по удалению сообщения
        scheduler.schedule(() -> {
            try {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId.toString());
                deleteMessage.setMessageId(messageId);
                execute(deleteMessage); // тут мы удаляем наше смс
                try {
                    // Получаем объект SendPhoto из сервиса и отправляем его через execute() а тут мы после удаления смс о теории мы отправляем фото сразу
                    SendPhoto sendPhoto = service.getPhoto(chatId);
                    execute(sendPhoto);
                    pool(chatId);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } catch (TelegramApiException e) {
                System.err.println("Ошибка удаления сообщения: " + e.getMessage());
            }

        }, 10, TimeUnit.SECONDS);
    }


    private void pool(Long chatId) throws TelegramApiException {
        SendPoll poll = new SendPoll();
        int correctOption = 2;

        poll.setChatId(chatId);
        poll.setQuestion("Какой метод добавляет элемент в ArrayList?");
        poll.setOptions(Arrays.asList("abb()", "insert()", "add()", "push()"));// Варианты ответов
        poll.setCorrectOptionId(correctOption);// индекс правильного варианта
        poll.setType("quiz"); //тип викторины выбираем
        poll.setExplanation("БЛЯДЬ"); // ответ если пользователь ответил не правильно

        correctAnswers.put(chatId, correctOption);
        execute(poll);


        sendWithKeyboard(chatId, "Хотите прочитать теорию о Arraylist?");
    }

    private void sendWithKeyboard(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(getStartKeyboard(chatId));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Сообщение не отправлено, кнопки не запустились ");
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup getStartKeyboard(Long chatId) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Да"));
        row.add(new KeyboardButton("Нет"));
        row.add(new KeyboardButton("Я изучаю пайтон"));
        keyboard.setKeyboard(List.of(row));

        return keyboard;
    }

    @Override
    public String getBotUsername() {
        return "Collection_bot";
    }
}

