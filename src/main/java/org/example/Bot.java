package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


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


@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final Service service;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);  // добаввил метод который удаляет в тг смс
    private final Map<Long, Integer> correctAnswers = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> theorySent = new ConcurrentHashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (!(update.hasMessage() && update.getMessage().hasText())) return;
     //  update.getMessage().  //пользователь отправляет мне сообщение, и получить от юзера имя пользователя, так-же подумать над разбитием разных классов что бы сократить код

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
        switch (text) {
            case "/start" -> {
                try {
                    execute(service.photoStart(chatId));
                } catch (TelegramApiException e) {
                    System.out.println("Ошибка отправки фото на функции старт ");
                    throw new RuntimeException(e);
                }

                try {
                    execute(service.startCommand(chatId));
                } catch (TelegramApiException e) {
                    System.err.println("Сообщение после команды Start не отправлено");
                    throw new RuntimeException(e);
                }
            }

        }

    }




    private void handleMessage(Message message) throws TelegramApiException {
        String text = message.getText();
        Long chatId = message.getChatId();
        switch (text) {
            case "Да" -> {  //отправляется теория с фотографиее о том как работает ArrayList
                sendTheory(chatId);
            }
            case "Нет" -> { // тут должна запуститься викторина по теме, раз пользователь уверен в своих силах, нужно проверить его по максимум, задать такой эдакий вопрос что бы у него закрались сомнения
                System.out.println("Вызов superPool для chatId: " + chatId); // Логируем вызов
                superPool(chatId);
            }
            case "Я изучаю пайтон" -> {
                photoTeory(chatId);
            }
            default -> {
                processCommand(text, chatId);
            }
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

    private SendPhoto photoTeory(Long chatId) throws TelegramApiException { //крч жто фотка на 3 кнопку - ДЛЯ ПИТОНЩИКОВ!
        SendPhoto sendPhoto = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile("https://ltdfoto.ru/images/2025/06/11/IMG_2314.jpg"))
                .build();
        execute(sendPhoto);
        sendWithKeyboard(chatId,"Согласен?!");
        return sendPhoto;
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
        int result = 2; //сохранаем сюда наш правильный ответ на викторину

        poll.setChatId(chatId);
        poll.setQuestion("Какой метод добавляет элемент в ArrayList?");
        poll.setOptions(Arrays.asList("abb()", "insert()", "add()", "push()"));// Варианты ответов
        poll.setCorrectOptionId(result);// индекс правильного варианта
        poll.setType("quiz"); //тип викторины выбираем
        poll.setExplanation("БЛЯДЬ"); // ответ если пользователь ответил не правильно

        correctAnswers.put(chatId, result); //узнать почему?!
        execute(poll);
        sendWithKeyboard(chatId, "Хотите прочитать теорию о Arraylist?"); // Вызываем следующий метод по кнопкам (да, нет, я изучаю пайтон)
    }


    private void superPool(Long chatId) throws TelegramApiException {
        SendPoll superPool = new SendPoll();
        int correctOption = 3; // Индекс правильного ответа
        correctAnswers.put(chatId, correctOption); // Сохраняем правильный ответ
        superPool.setChatId(chatId);
        superPool.setQuestion("Какое из следующих утверждений о ArrayList является верным?");
        superPool .setOptions(Arrays.asList(
                        "Размер увеличивается в 2 раза при добавлении.",
                        "Хранение в виде узлов обеспечивает быстрые вставки/удаления.",
                        "Только объекты одного типа<> иначе ошибка компиляции",
                        "Доступ по индексу O(1), поиск по значению O(n)."));
        superPool .setCorrectOptionId(correctOption);
        superPool.setType("quiz");
        superPool.setExplanation("Неправильно! Ответ: 4\n" +
                        "1.Увеличение на 50% (не в 2 раза)\n" +
                        "2.Узлы — это LinkedList\n" +
                        "3.\n\nБез дженериков — любые объекты\n");



        execute(superPool);
        sendWithKeyboard(chatId, "Уверен, что не хочешь перечитать теорию?");
    }

    private void sendWithKeyboard(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(getStartKeyboard(chatId));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Сообщение с клавиатурой не отправлено: " + e.getMessage());
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

    @Override
    public String getBotToken() {
        return "";
    }
}

