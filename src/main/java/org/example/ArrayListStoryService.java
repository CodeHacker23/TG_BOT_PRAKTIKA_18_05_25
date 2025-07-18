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

/**
 * ArrayListStoryService — сервис для сюжетной линии по ArrayList.
 * Здесь вся теория, фотки, викторины, удаление сообщений и прочий интерактив по ArrayList.
 *
 * Почему нельзя лепить всё в одном сервисе? Потому что если ты захочешь добавить LinkedListStoryService — твой код не должен превратиться в кашу.
 *
 * Пример расширения:
 *   - Хочешь сделать сюжет по LinkedList? Создай LinkedListStoryService по аналогии с этим классом.
 *   - Все методы для LinkedList — только туда!
 *
 * Юмор: если начнёшь копипастить методы между сервисами — Архитектор лично напишет тебе в Telegram.
 */
@Service
@RequiredArgsConstructor
public class ArrayListStoryService {
    // Сервисы, которые нужны для работы сюжета
    private final org.example.Service service;
    private final KeyboardService keyboardService;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Long, Integer> correctAnswers = new ConcurrentHashMap<>();
    private final PhotoService photoService;

    /**
     * Отправляет теорию по ArrayList в виде SendMessage
     * @param chatId — ID чата Telegram
     * @return SendMessage с теорией
     *
     * Пример:
     *   SendMessage theory = arrayListStoryService.getArrayListTheory(chatId);
     *   bot.execute(theory);
     */
    public SendMessage getArrayListTheory(Long chatId) {
        System.out.println("[ArrayListStoryService] getArrayListTheory() — отправляем теорию по ArrayList для chatId=" + chatId);
        String theoryText = getArrayListInfo(chatId);
        if (theoryText == null || theoryText.trim().isEmpty()) {
            System.err.println("Попытка отправить пустую теорию по ArrayList для chatId=" + chatId + "! Сообщение не будет отправлено.");
            return null;
        }
        SendMessage theory = new SendMessage();
        theory.setParseMode("Markdown");
        theory.setText(theoryText);
        theory.setChatId(chatId);
        return theory;
    }


    /**
     * Формирует текст теории по ArrayList (вынесено отдельно для переиспользования)
     * @return String — текст теории
     */
    private static String formatArrayListInfo() {
        StringBuilder sb = new StringBuilder();

        // Пример экранирования
        sb.append("ArrayList - это динамический массив, реализующий интерфейс List.\n");
        sb.append("Он автоматически меняет размер на 50% при добавлении/удалении элементов,\n");
        sb.append("но операции вставки/удаления в середине списка могут быть медленными\n");
        sb.append("из-за необходимости копирования элементов.\n\n");

        // Пример создания
        sb.append("Пример создания:\n");
        sb.append("```\n");
        sb.append("ArrayList<String> box = new ArrayList<>();\n");
        sb.append("```\n\n");

        // Методы
        sb.append("Основные методы:\n\n");

        // add()
        sb.append("add(E element) - Добавляет элемент в конец списка.\n");
        sb.append("```\n");
        sb.append("ArrayList<String> toys = new ArrayList<>();\n");
        sb.append("toys.add(\"Машинка\"); // Добавили машинку в коробку\n");
        sb.append("toys.add(\"Кукла\");   // Добавили куклу\n");
        sb.append("```\n\n");

        // get()
        sb.append("get(int index) - Получает элемент по индексу.\n");
        sb.append("```\n");
        sb.append("String firstToy = toys.get(0); // Получаем первую игрушку (индекс 0)\n");
        sb.append("System.out.println(firstToy);  // Выведет: Машинка\n");
        sb.append("```\n\n");

        // set()
        sb.append("set(int index, E element) - Заменяет элемент.\n");
        sb.append("```\n");
        sb.append("toys.set(1, \"Робот\"); // Заменяем куклу на робота\n");
        sb.append("```\n\n");

        // remove()
        sb.append("remove(int index) - Удаляет элемент по индексу.\n");
        sb.append("```\n");
        sb.append("toys.remove(0); // Удаляем машинку (индекс 0)\n");
        sb.append("```\n\n");

        // size()
        sb.append("size() - Возвращает количество элементов.\n\n");
        sb.append("```\n");
        sb.append("int count = toys.size();\n");
        sb.append("System.out.println(\"В коробке \" + count + \" игрушек\");\n");
        sb.append("```\n\n");

        // add(int index, E element)
        sb.append("add(int index, E element) — вставка по индексу. \n");
        sb.append("Позволяет вставить элемент не только в конец , но и в любое место списка.\n");
        sb.append("Например, вставить \"Новую игрушку\" между \"Машинкой\" и \"Куклой\":\n");
        sb.append("```\n");
        sb.append("toys.add(1, \"Новая игрушка\"); // Теперь порядок: Машинка, Новая игрушка, Кукла. ");
        sb.append("```\n\n");
        sb.append("Однако вставка в середину списка требует сдвига всех последующих элементов, что может быть медленным для больших списков!! \n");

        //  System.out.println("Формируемая теория: " + sb.toString());

        return sb.toString();
    }

    /**
     * Получить теорию по ArrayList (для других сервисов)
     * @return String — текст теории
     */
    public static String getArrayListInfo(Long chatId) {
        return formatArrayListInfo();
    }

    /**
     * Отправляет фото по ArrayList (карточка)
     * @param chatId — ID чата Telegram
     * @return SendPhoto с фото и описанием
     */
    public SendPhoto getArrayListPhotoTheory(Long chatId) {
        System.out.println("[ArrayListStoryService] getArrayListPhotoTheory() — отправляем фото по ArrayList для chatId=" + chatId);
        return photoService.getArrayListTheoryPhoto(chatId);
    }

    /**
     * Отправляет фото Python (пасхалка)
     * @param chatId — ID чата Telegram
     * @return SendPhoto с фото Python
     */
    public SendPhoto getPythonPhoto(Long chatId) {
        System.out.println("[ArrayListStoryService] getPythonPhoto() — отправляем Python-фото для chatId=" + chatId);
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile("https://ltdfoto.ru/images/2025/06/11/IMG_2314.jpg"))
                .build();
    }

    /**
     * Отправляет викторину по ArrayList (quiz)
     * @param chatId — ID чата Telegram
     * @return SendPoll — объект викторины
     *
     * Пример:
     *   SendPoll poll = arrayListStoryService.getArrayListQuiz(chatId);
     *   bot.execute(poll);
     */
    public SendPoll getArrayListQuiz(Long chatId) {
        System.out.println("[ArrayListStoryService] getArrayListQuiz() — отправляем quiz по ArrayList для chatId=" + chatId);
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

    /**
     * Отправляет супер-викторину по ArrayList (сложный вопрос)
     * @param chatId — ID чата Telegram
     * @return SendPoll — объект викторины
     */
    public SendPoll getArrayListSuperQuiz(Long chatId) {
        System.out.println("[ArrayListStoryService] getArrayListSuperQuiz() — отправляем супер-викторину для chatId=" + chatId);
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

    /**
     * Планирует удаление сообщения через 50 секунд и отправляет новую теорию/викторину
     * @param bot — TelegramLongPollingBot
     * @param chatId — ID чата
     * @param messageId — ID сообщения для удаления
     */
    public void scheduleMessageDeletion(org.telegram.telegrambots.bots.TelegramLongPollingBot bot, Long chatId, Integer messageId) {
        System.out.println("[ArrayListStoryService] scheduleMessageDeletion() — планируем удаление сообщения messageId=" + messageId + " для chatId=" + chatId);
        scheduler.schedule(() -> {
            try {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId.toString());
                deleteMessage.setMessageId(messageId);
                bot.execute(deleteMessage);
                try {
                    SendPhoto sendPhoto = photoService.getStartPhoto(chatId);
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
        }, 50, TimeUnit.SECONDS);
    }

    /**
     * Отправляет теорию и фото по ArrayList
     * @param bot — TelegramLongPollingBot
     * @param chatId — ID чата
     */
    public void sendTheory(org.telegram.telegrambots.bots.TelegramLongPollingBot bot, Long chatId) {
        System.out.println("[ArrayListStoryService] sendTheory() — отправляем теорию и фото по ArrayList для chatId=" + chatId);
        SendMessage theory = getArrayListTheory(chatId);
        SendPhoto sendPhoto = getArrayListPhotoTheory(chatId);
        try {
            bot.execute(sendPhoto);
            if (theory != null && theory.getText() != null && !theory.getText().trim().isEmpty()) {
                bot.execute(theory);
            } else {
                System.err.println("Попытка отправить пустую теорию по ArrayList для chatId=" + chatId + "! Сообщение не будет отправлено.");
            }
        } catch (TelegramApiException e) {
            System.out.println("Улетел в эксепшн проблема с фото ");
            e.printStackTrace();
        }
    }

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
        message.setReplyMarkup(KeyboardService.getStartKeyboardStatic());
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Сообщение с клавиатурой не отправлено: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Получить мапу правильных ответов для викторин (chatId -> номер правильного варианта)
     * @return Map<Long, Integer>
     */
    public Map<Long, Integer> getCorrectAnswers() {
        return correctAnswers;
    }

    // --- Советы по расширению ---
    // 1. Хочешь сделать сюжет по LinkedList? Создай LinkedListStoryService по аналогии с этим классом.
    // 2. Все методы для LinkedList — только туда, не смешивай с ArrayListStoryService.
    // 3. Для новых викторин — делай отдельные методы (getLinkedListQuiz и т.д.).
    // 4. Для новых теорий — отдельные методы (getLinkedListTheory и т.д.).
    // 5. Не копипасть, а выноси общее в абстрактные классы/интерфейсы, если логика повторяется.
    // 6. Если добавишь логику без комментария — Архитектор лично напишет тебе в Telegram.
} 