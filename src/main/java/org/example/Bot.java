package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
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
        sendMessage.setParseMode("Markdown");

        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                handleMessage(update.getMessage());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

    }


    private void handleMessage(Message message) throws TelegramApiException {
        String text = message.getText();
        Long chatId = message.getChatId();

        switch (text){
            case "Да" -> { //отправляется теория с фотографиее о том как работает ArrayList
                sendTheory(chatId);
            }
            case "Нет" -> { // тут должна запуститься викторина по теме, раз пользователь уверен в своих силах, нужно проверить его по максимум, задать такой эдакий вопрос что бы у него закрались сомнения

            }
            case "Я изучаю пайтон" -> { //отправить фотку


            }
            default -> {
                processCommand(text, chatId);
            }
        }

        if (text.equals("Да")) {

        } else if(text.equals("Нет")){
            //
        }



    }


    private void processCommand(String text, Long chatId) {
        try {
            String result = service.getWay(text);
            SendMessage sendMessage = new SendMessage(chatId.toString(), result);
            sendMessage.setParseMode("Markdown"); // Активируем Markdown
            theorySent.compute(chatId, (k, v) -> true);
            Message response = execute(sendMessage);
            if (result.equals(service.getArrayListInfo())) {
                scheduleMessageDeletion(chatId, response.getMessageId());
            }
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }


    }


    private void sendTheory(Long chatId) {
        SendMessage theory = new SendMessage();
        theory.setParseMode("Markdown");// устанавливаем что бы в сообщении код выделялся
        theory.setText(service.getArrayListInfo());
        theory.setChatId(chatId);
        SendPhoto sendPhoto = service.getPhotoTheory(chatId);
        try {
            execute(sendPhoto);
            execute(theory);
        } catch (TelegramApiException e) {
            System.out.println("Улетел в ексепшен проблема с фото ");
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
                    System.err.println("Метод встал и не работает ");
                    e.printStackTrace();
                }
            } catch (TelegramApiException e) {
                System.err.println("Ошибка удаления сообщения: " + e.getMessage());
            }

        }, 10, TimeUnit.SECONDS);
    }


    private void pool(Long chatId) throws TelegramApiException { //1 викторина которая проверяет пользователя на знание сразу с отвтетным уведомлением
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

