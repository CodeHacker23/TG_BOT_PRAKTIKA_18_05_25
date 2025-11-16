package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.bot.KeyboardService.KeyboardReam;
import org.example.service.PhotoService.PhotoReam;
import org.example.service.PhotoService.PhotoStart;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.example.bot.KeyboardService.KeyboardService;


@Service
@RequiredArgsConstructor
public class ArrayListStoryService {
    // Сервисы, которые нужны для работы сюжета
    // private final org.example.Service service; // УДАЛЕНО для устранения цикла
    private final KeyboardService keyboardService;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final PhotoStart photoStart;
    private final PhotoReam photoReam;

//    /**
//     * Отправляет теорию по ArrayList в виде SendMessage
//     * @param chatId — ID чата Telegram
//     * @return SendMessage с теорией
//     *
//     * Пример:
//     *   SendMessage theory = arrayListStoryService.getArrayListTheory(chatId);
//     *   bot.execute(theory);
//     */
//    public SendMessage getArrayListTheory(Long chatId) {
//        System.out.println("[ArrayListStoryService] getArrayListTheory() — отправляем теорию по ArrayList для chatId=" + chatId);
//        String theoryText = ArrayListStory.getArrayListInfo(chatId);
//        if (theoryText == null || theoryText.trim().isEmpty()) {
//            System.err.println("Попытка отправить пустую теорию по ArrayList для chatId=" + chatId + "! Сообщение не будет отправлено.");
//            return null;
//        }
//        SendMessage theory = new SendMessage();
//        theory.setParseMode("Markdown");
//        theory.setText(theoryText);
//        theory.setChatId(chatId);
//        return theory;
//    }

    /**
     * Отправляет фото по ArrayList (карточка)
     * @param chatId — ID чата Telegram
     * @return SendPhoto с фото и описанием
     */
    public SendPhoto getArrayListPhotoTheory(Long chatId) {
        System.out.println("[ArrayListStoryService] getArrayListPhotoTheory() — отправляем фото по ArrayList для chatId=" + chatId);
        return PhotoReam.getArrayListTheoryPhoto(chatId);
    }





    /**
     * Отправляет супер-викторину по ArrayList (сложный вопрос)
     * @param chatId — ID чата Telegram
     * @return SendPoll — объект викторины
     */
//    public SendPoll getArrayListSuperQuiz(Long chatId) {
//        System.out.println("[ArrayListStoryService] getArrayListSuperQuiz() — отправляем супер-викторину для chatId=" + chatId);
//        SendPoll superPool = new SendPoll();
//        int correctOption = 3;
//        correctAnswers.put(chatId, correctOption);
//        superPool.setChatId(chatId);
//        superPool.setQuestion("Какое из следующих утверждений о ArrayList является верным?");
//        superPool.setOptions(Arrays.asList(
//                "Размер увеличивается в 2 раза при добавлении.",
//                "Хранение в виде узлов обеспечивает быстрые вставки/удаления.",
//                "Только объекты одного типа<> иначе ошибка компиляции",
//                "Доступ по индексу O(1), поиск по значению O(n)."));
//        superPool.setCorrectOptionId(correctOption);
//        superPool.setType("quiz");
//        superPool.setExplanation("Правильный Ответ: 4\n1.Увеличение на 50% (не в 2 раза)\n2.Узлы — это LinkedList\n3.\n\nБез дженериков — любые объекты\n");
//        return superPool;
//    }


//    public void scheduleMessageDeletion(TelegramLongPollingBot bot, Long chatId, Integer messageId) {
//        System.out.println("[ArrayListStoryService] scheduleMessageDeletion() — планируем удаление сообщения messageId=" + messageId + " для chatId=" + chatId);
//        scheduler.schedule(() -> {
//            try {
//                DeleteMessage deleteMessage = new DeleteMessage();
//                deleteMessage.setChatId(chatId.toString());
//                deleteMessage.setMessageId(messageId);
//                bot.execute(deleteMessage);
//                try {
//                    bot.execute(getArrayListQuiz(chatId));
//                    sendWithKeyboard(bot, chatId, "Хотите прочитать теорию о Arraylist?");
//                } catch (TelegramApiException e) {
//                    System.err.println("Метод встал и не работает ");
//                    e.printStackTrace();
//                }
//            } catch (TelegramApiException e) {
//                System.err.println("Ошибка удаления сообщения: " + e.getMessage());
//            }
//        }, 10, TimeUnit.SECONDS);
//    }

    /**
     * Отправляет теорию и фото по ArrayList
     * @param bot — TelegramLongPollingBot
     * @param chatId — ID чата
     */
//    public void sendTheory (TelegramLongPollingBot bot, Long chatId) {
//        System.out.println("[ArrayListStoryService] sendTheory() — отправляем теорию и фото по ArrayList для chatId=" + chatId);
//        SendMessage theory = getArrayListTheory(chatId);
//        SendPhoto sendPhoto = getArrayListPhotoTheory(chatId);
//        try {
//            bot.execute(sendPhoto);
//            if (theory != null && theory.getText() != null && !theory.getText().trim().isEmpty()) {
//                bot.execute(theory);
//            } else {
//                System.err.println("Попытка отправить пустую теорию по ArrayList для chatId=" + chatId + "! Сообщение не будет отправлено.");
//            }
//        } catch (TelegramApiException e) {
//            System.out.println("Улетел в эксепшн проблема с фото ");
//            e.printStackTrace();
//        }
//    }

    /**
     * Отправляет сообщение с клавиатурой (например, для выбора Да/Нет)
     * @param bot — TelegramLongPollingBot
     * @param chatId — ID чата
     * @param text — текст сообщения
     */
    public void sendWithKeyboard(org.telegram.telegrambots.bots.TelegramLongPollingBot bot, Long chatId, String text) {
        System.out.println("[ArrayListStoryService] sendWithKeyboard() — отправляем сообщение с клавиатурой для chatId=" + chatId + ", текст: " + text);
        if (text == null || text.trim().isEmpty()) {
            System.err.println("Попытка отправить пустое сообщение с клавиатурой для chatId=" + chatId + "! Сообщение не будет отправлено.");
            return;
        }
        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(KeyboardReam.getStartKeyboardStatic());
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Сообщение с клавиатурой не отправлено: " + e.getMessage());
            e.printStackTrace();
        }
    }

//    /**
//     * Получить мапу правильных ответов для викторин (chatId -> номер правильного варианта)
//     * @return Map<Long, Integer>
//     */
//    public Map<Long, Integer> getCorrectAnswers() {
//        return correctAnswers;
//    }

    // --- Советы по расширению ---
    // 1. Хочешь сделать сюжет по LinkedList? Создай LinkedListStoryService по аналогии с этим классом.
    // 2. Все методы для LinkedList — только туда, не смешивай с ArrayListStoryService.
    // 3. Для новых викторин — делай отдельные методы (getLinkedListQuiz и т.д.).
    // 4. Для новых теорий — отдельные методы (getLinkedListTheory и т.д.).
    // 5. Не копипасть, а выноси общее в абстрактные классы/интерфейсы, если логика повторяется.
    // 6. Если добавишь логику без комментария — Иларион лично напишет тебе в Telegram.
} 