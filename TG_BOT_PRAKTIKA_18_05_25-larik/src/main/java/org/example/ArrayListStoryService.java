package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ArrayListStoryService { // тест
    private final org.example.Service service;
    private final KeyboardService keyboardService;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Long, Integer> correctAnswers = new ConcurrentHashMap<>();

    public SendMessage getArrayListTheory(Long chatId) {
        SendMessage theory = new SendMessage();
        theory.setParseMode("Markdown");
        theory.setText(service.getArrayListInfo());
        theory.setChatId(chatId);
        return theory;
    }

    public SendPhoto getArrayListPhotoTheory(Long chatId) {
        return service.getPhotoTheory(chatId);
    }

    public SendPhoto getPythonPhoto(Long chatId) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile("https://ltdfoto.ru/images/2025/06/11/IMG_2314.jpg"))
                .build();
    }

    public SendPoll getArrayListQuiz(Long chatId) {
        SendPoll poll = new SendPoll();
        int result = 2;
        poll.setChatId(chatId);
        poll.setQuestion("Какой метод добавляет элемент в ArrayList?");
        poll.setOptions(Arrays.asList("abb()", "insert()", "add()", "push()"));
        poll.setCorrectOptionId(result);
        poll.setType("quiz");
        poll.setExplanation("БЛЯДЬ");
        correctAnswers.put(chatId, result);

        return poll;
    }

    public SendPoll getArrayListSuperQuiz(Long chatId) {
        SendPoll superPool = new SendPoll();
        int correctOption = 3;
        correctAnswers.put(chatId, correctOption);
        superPool.setChatId(chatId);
        superPool.setQuestion("Какое из следующих утверждений о ArrayList является верным?");
        superPool.setOptions(Arrays.asList(
                "Размер увеличивается в 2 раза при добавлении.",
                "Хранение в виде узлов обеспечивает быстрые вставки/удаления.",
                "Только объекты одного типа<> иначе ошибка компиляции",
                "Доступ по индексу O(1), поиск по значению O(n)."));
        superPool.setCorrectOptionId(correctOption);
        superPool.setType("quiz");
        superPool.setExplanation("Правильный Ответ: 4\n1.Увеличение на 50% (не в 2 раза)\n2.Узлы — это LinkedList\n3.\n\nБез дженериков — любые объекты\n");
        return superPool;
    }

    public void scheduleMessageDeletion(org.telegram.telegrambots.bots.TelegramLongPollingBot bot, Long chatId, Integer messageId) {
        scheduler.schedule(() -> {
            try {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId.toString());
                deleteMessage.setMessageId(messageId);
                bot.execute(deleteMessage);
                try {
                    SendPhoto sendPhoto = service.getPhoto(chatId);
                    bot.execute(sendPhoto);
                    bot.execute(getArrayListQuiz(chatId));
                    sendWithKeyboard(bot, chatId, "Хотите прочитать теорию о Arraylist?");
                } catch (TelegramApiException e) {
                    System.err.println("Метод встал и не работает ");
                    e.printStackTrace();
                }
            } catch (TelegramApiException e) {
                System.err.println("Ошибка удаления сообщения: " + e.getMessage());
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void sendTheory(org.telegram.telegrambots.bots.TelegramLongPollingBot bot, Long chatId) {
        SendMessage theory = getArrayListTheory(chatId);
        SendPhoto sendPhoto = getArrayListPhotoTheory(chatId);
        try {
            bot.execute(sendPhoto);
            bot.execute(theory);
        } catch (TelegramApiException e) {
            System.out.println("Улетел в ексепшен проблема с фото ");
            e.printStackTrace();
        }
    }

    public void sendWithKeyboard(org.telegram.telegrambots.bots.TelegramLongPollingBot bot, Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(KeyboardService.getStartKeyboardStatic());
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Сообщение с клавиатурой не отправлено: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<Long, Integer> getCorrectAnswers() {
        return correctAnswers;
    }
} 