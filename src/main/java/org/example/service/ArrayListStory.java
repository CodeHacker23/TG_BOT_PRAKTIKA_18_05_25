package org.example.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.bot.KeyboardService.KeyboardReam;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.PhotoService.PhotoReam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.example.bot.KeyboardService.KeyboardReam.BattlArreyn;
import org.example.MarkdownUtil;

/**
 * ArrayListStoryService — сервис для сюжетной линии по ArrayList.
 * Здесь вся теория, фотки, викторины, удаление сообщений и прочий интерактив по ArrayList.
 * <p>
 * === ИНСТРУКЦИЯ ДЛЯ ЧАЙНИКОВ (и не только) ===
 * <p>
 * Как сделать красивую маршрутизацию команд (без кейсов и if-else), как в StoryStartService:
 * <p>
 * 1. Создай Map<String, BiConsumer<TelegramLongPollingBot, Message>> arrayListCommands = new HashMap<>();
 * // Ключ — команда/кнопка, значение — обработчик
 * <p>
 * 2. В @PostConstruct (или конструкторе) заполни эту Map:
 * arrayListCommands.put("/ArrayList", (bot, msg) -> sendTheory(bot, msg.getChatId()));
 * arrayListCommands.put("Да", (bot, msg) -> sendTheory(bot, msg.getChatId()));
 * arrayListCommands.put("Нет", (bot, msg) -> sendQuiz(bot, msg.getChatId()));
 * // ... и так далее для всех команд ветки ArrayList
 * <p>
 * 3. Сделай методы:
 * public boolean canHandle(String text) { return arrayListCommands.containsKey(text); }
 * public void handle(TelegramLongPollingBot bot, Message message) {
 * String text = message.getText();
 * if (arrayListCommands.containsKey(text)) {
 * log.info("ArrayListStoryService: обработка команды '{}', chatId={}, userId={}", text, message.getChatId(), message.getFrom().getId());
 * arrayListCommands.get(text).accept(bot, message);
 * } else {
 * log.warn("ArrayListStoryService: команда '{}' не найдена в Map", text);
 * }
 * }
 * <p>
 * 4. В MessageHandlerService добавь делегирование:
 * if (arrayListStoryService.canHandle(text)) {
 * arrayListStoryService.handle(bot, message);
 * return;
 * }
 * <p>
 * 5. PROFIT! Теперь твоя ветка не превратится в лапшу, а Архитектор не придёт ночью.
 * <p>
 * === ЧЁРНЫЙ ЮМОР ===
 * - Если ты добавишь 100 if-ов — твой проект станет дипломом по SpaghettiCode.
 * - Если забудешь логирование — баги будут прятаться в твоём коде, как NullPointerException в try/catch.
 * - Если не добавишь комментарии — Архитектор лично напишет тебе в Telegram (и не только).
 * <p>
 * === ПРИМЕРЫ ===
 * // В Map:
 * arrayListCommands.put("/ArrayList", (bot, msg) -> sendTheory(bot, msg.getChatId()));
 * arrayListCommands.put("Да", (bot, msg) -> sendTheory(bot, msg.getChatId()));
 * arrayListCommands.put("Нет", (bot, msg) -> sendQuiz(bot, msg.getChatId()));
 * <p>
 * // В MessageHandlerService:
 * if (arrayListStoryService.canHandle(text)) {
 * arrayListStoryService.handle(bot, message);
 * return;
 * }
 * <p>
 * // В каждом обработчике — логируй, иначе баги будут жить вечно!
 * <p>
 * Удачи! Если что-то не работает — смотри логи, пей чай и не забывай про дебаг.
 */

@RequiredArgsConstructor
@Service
public class ArrayListStory { //наша ветка по сюжетке Array
    private static final Logger log = LoggerFactory.getLogger(StoryStartService.class);

    // Сервис для централизованной логики создания персонажа
    private final PersonageCreationService personageCreationService;
    // Сервис для работы с пользователями
    private final UserService userService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, BiConsumer<TelegramLongPollingBot, Message>> CommandsReam = new HashMap<>();

    /**
     * Формирует текст теории по ArrayList (вынесено отдельно для переиспользования)
     *
     * @return String — текст теории
     */
    private static String formatArrayListInfo() {
        StringBuilder sb = new StringBuilder();

        // Пример экранирования
        sb.append("\uD83E\uDDE0 *Внимание: свиток самоуничтожится через 20 секунд. Успей зацепить главное!*\n\n");
        sb.append("ArrayList - это динамический массив, реализующий интерфейс List.\n");
        sb.append("Он автоматически меняет свой размер на 50 - 100% при добавлении/удалении элементов,\n");
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
        sb.append("Однако вставка в середину/начало списка требует сдвига всех последующих элементов, что может быть медленным для больших списков!! \n");

        //  System.out.println("Формируемая теория: " + sb.toString());

        return sb.toString();
    }

    @PostConstruct
    public void initReam() {
        CommandsReam.put("📜 Получить боевой свиток", (bot, msg) -> {
            sendTheoryWithAutoDelete(bot, msg.getChatId());
        });
        CommandsReam.put("\uD83D\uDEE1 Блокировать \n" +
                " (try-catch)", ((telegramLongPollingBot, message) -> {
            ReamBlock(telegramLongPollingBot, message.getChatId());
        }));

    }


    /**
     * Предупреждающее смс от Итераториуса
     *
     * @param chatId
     * @return sendMessage
     */
    public static SendMessage ReamIteratorius(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*Итераториус* \n\n" +
                "⚔\uFE0F Твоя первая цель — ArrayList.\n\n" +
                "Не дай простоте тебя обмануть.\n" +
                "Он вроде как списочек…\n" +
                "Но стоит переполнить — и тебя отбрасывает в древнюю арену newCapacity().");
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(KeyboardReam.BattleList(chatId));
        return sendMessage;
    }

    /**
     * Итераториус предупреждает что вышел Аррейн
     *
     * @param chatId
     * @return
     */
    public static SendMessage ReamIteratorius2(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*Итераториус* \n\n" +
                "\uD83D\uDDE1Внимание!\n" +
                "Первый противник приближается...\n" +
                "Это он...\n\n" +
                "*Aррейн* — коварный и быстрый.\n" +
                "Он дублирует элементы. Он путает порядок.\n" +
                "И он ненавидит..._(обращение прервано...)_");
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }

    /**
     * Объявление раунда 1
     *
     * @param chatId
     * @return
     */

    public static SendMessage ArenaNewCapacityRound1(Long chatId, UserService userService) {
        SendMessage sendMessage = new SendMessage();
        // Отключаем Markdown-разметку, чтобы не было ошибок Telegram
        // sendMessage.setParseMode("Markdown");
        // Можно явно указать null, но по умолчанию будет PlainText
        sendMessage.setParseMode(null);
        sendMessage.setChatId(chatId);

        // Получаем пользователя и персонажа
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            sendMessage.setText("Ошибка: персонаж не найден.");
            return sendMessage;
        }
        PersonageEntity entity = user.getPersonage();

        // Формируем строку статов без экранирования всей строки
        String statsLine = buildStatsLine(entity);

        sendMessage.setText(
            "Раунд 1 — 'Код под давлением'\n\n" +
            "Твои статы:\n" +
            statsLine
        );
        return sendMessage;
    }


    /**
     * Объявление раунда 2
     *
     * @param chatId
     * @return
     */
    public static SendMessage ArenaNewCapacityRound2(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);
        sendMessage.setText(" \uD83D\uDCDC Раунд 2 — *'На грани слома'* ");
        return sendMessage;
    }

    /**
     * Объявление раунда 3
     *
     * @param chatId
     * @return
     */
    public static SendMessage ArenaNewCapacityRound3(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);
        sendMessage.setText(" \uD83D\uDCDC Раунд 3 — *'Последний выбор'*");
        return sendMessage;
    }


    /**
     * Арейн говорит свои слова и предстовляется
     *
     * @param chatId
     * @return
     */
    public SendMessage messageArrayen(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Аррейн*\n\n" +
                "Думаешь, я просто список? Я — чертов ArrayList, дружище.\n" +
                "И когда ты лезешь ко мне с вставкой по индексу — я пересоздаю себя.\n" +
                "Полностью.\n" +
                "Целиком, мать его.\n" +
                "Потому что в Java всё просто — пока не становится ПИЗ''Ц как сложно.");
        return sendMessage;
    }


    public SendMessage messageArrayenTextInfo(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Аррейн*\n\n" +
                "Я не против, если ты добавишь (Е element) в конец.\n" +
                "Но попробуй вставить в индекс 0 — и ты узнаешь, что такое боль....\" ");
        return sendMessage;

    }


    /**
     * Арейн атакует !!
     *
     * @param chatId
     * @return
     */
    public static SendMessage ARREINAtacka(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("\uD83D\uDCA5 _Аррейн бросает в тебя виртуальный элемент с индексом 0!_\n\n" +
                "\uD83E\uDDE0 *Итераториус* (шепчет):\n" +
                "У тебя есть доля секунды. Реагируй!\n\n ");
        sendMessage.setReplyMarkup(BattlArreyn(chatId));
        sendMessage.setParseMode("Markdown");
        return sendMessage;

    }

    //
    public static SendMessage ReamChoice(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("" +
                "\uD83C\uDFAE Тебе доступны действия: \uD83C\uDFAE");
        sendMessage.setReplyMarkup(BattlArreyn(chatId));
        return sendMessage;
    }


    /**
     * Планирует удаление сообщения через 20 секунды и отправляет клавиатуру
     *
     * @param bot    — TelegramLongPollingBot
     * @param chatId — ID чата
     */
    public void sendTheoryWithAutoDelete(TelegramLongPollingBot bot, Long chatId) {
        String theory = formatArrayListInfo();
        SendMessage sendMessage = new SendMessage(chatId.toString(), theory);
        sendMessage.setParseMode("Markdown");
        try {
            Message sentMsg = bot.execute(sendMessage);
            Integer messageId = sentMsg.getMessageId();
            log.info("[ArrayListStory] Отправлено сообщение с теорией, messageId={}", messageId);
            scheduler.schedule(() -> {
                try {
                    log.info("[ArrayListStory] Пробуем удалить сообщение, messageId={}", messageId);
                    DeleteMessage deleteMessage = new DeleteMessage();
                    deleteMessage.setChatId(chatId.toString());
                    deleteMessage.setMessageId(messageId);
                    bot.execute(deleteMessage);
                    log.info("[ArrayListStory] Удаление сообщения выполнено, messageId={}", messageId);
                    bot.execute(ReamIteratorius2(chatId));
                    scheduler.schedule(() -> {
                        try {
                            log.info("[ArrayListStory] - Отправлено фото Айрена для chatId ={}", chatId);
                            bot.execute(PhotoReam.photoArray(chatId));//TODO отправялем нашу фотку врага
                            bot.execute(messageArrayen(chatId));
                            scheduler.schedule(() -> {
                                try {
                                    log.info("[ArrayListStory] - Отправлена атака Айрена для chatId ={}", chatId);
                                    bot.execute(ArenaNewCapacityRound1(chatId, userService));
                                    bot.execute(ArrayListStory.ARREINAtacka(chatId));
                                } catch (TelegramApiException e) {
                                    log.error("[ArrayListStory] - Ошибка отправки атаки Айрена для chatId ={}", chatId);
                                }
                            }, 4, TimeUnit.SECONDS);
                        } catch (TelegramApiException e) {
                            log.error("[ArrayListStory] sendTheoryWithAutoDelete Фото Айрена не отправлено для chatId = {}", chatId);
                        }

                    }, 5, TimeUnit.SECONDS);

                    // arrayListStoryService.sendWithKeyboard(bot, chatId, "Хотите прочитать теорию о Arraylist?"); старые кнопки
                } catch (Exception e) {
                    log.error("[ArrayListStory] Ошибка при удалении сообщения или отправке клавиатуры", e);
                }
            }, 20, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[ArrayListStory] Ошибка при отправке теории", e);
        }
    }


    public void ReamBlock(TelegramLongPollingBot bot, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        // Не используем Markdown для первого сообщения!

        sendMessage.setText("Нанесен урон Айрену 100(-50)\n\n" +
                "_Ты строишь стену из\n" +
                "``` try { ... } catch (...) { ... } ```\n" +
                "но Аррейон не из тех, кто уважает чужие перехваты._\n\n" +
                "Аррейон выносит с ноги твою защиту, как баги выносят прод после пятничного рефактора.\n\n" +
                "*Ошибка:* ```ArrayIndexOutOfBoundsException``` прорывает блок, словно нож сквозь масло.");
        sendMessage.setParseMode("Markdown");
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Получаем пользователя и персонажа
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) return;
        PersonageEntity entity = user.getPersonage();

        // 2. Проверяем тип персонажа
        String type = entity.getCharacterType();
        int oldPoints = entity.getAchievementPoints();
        double oldMoney = entity.getCurrency();

        // 3. Рандомно начисляем статы
        int exp = 38 + (int) (Math.random() * (55 - 38 + 1));
        int cash = 200 + (int) (Math.random() * (350 - 200 + 1));
        entity.setAchievementPoints(oldPoints + exp);
        entity.setCurrency(oldMoney + cash);

        // 4. Сохраняем изменения в БД
        userService.saveUser(user);

        // 5. Сообщаем пользователю о повышении статов
        scheduler.schedule(() -> {
            SendMessage resultMsg = new SendMessage();
            resultMsg.setChatId(chatId);
            resultMsg.setParseMode("Markdown");
            resultMsg.setText("*Итераториус*:\n\n" +
                    "Молодец, конечно… Только try-catch не вечен.\n" +
                    "Не всё в жизни ловится на костыли.\n" +
                    " _Следующий ход — только защита или анализ. Атаковать нельзя._\n" +
                    "*Навык повышен:*\n" +
                    "  +" + exp + " ⭐️ к Очкам Достижения\n" +
                    "  +" + cash + " \uD83D\uDCB2 к Деньгам.");
            try {
                bot.execute(resultMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 2, TimeUnit.SECONDS);
    }


    public boolean canHandle(String text) {
        return CommandsReam.containsKey(text);
    }

    public void handle(TelegramLongPollingBot bot, Message message) {
        String text = message.getText();
        if (CommandsReam.containsKey(text)) {
            log.info("[ArrayListStory] обработка команды '{}', chatId={}, userId={}", text, message.getChatId(), message.getFrom().getId());
            CommandsReam.get(text).accept(bot, message);
        } else {
            log.warn("[ArrayListStory] команда '{}' не найдена в Map", text);
        }
    }

    /**
     * Статический buildStatsLine для использования в статическом контексте.
     */
    private static String buildStatsLine(PersonageEntity entity) {
        String type = entity.getCharacterType();
        String money = String.valueOf(entity.getCurrency());
        if (money.endsWith(".0")) money = money.substring(0, money.length() - 2); // убираем .0 если не нужно
        switch (type) {
            case "Personage1":
                return "|🏆Level: " + entity.getLevel() +
                        " |⚡️Энергия: " + entity.getEnergy() +
                        " |⭐️Очки достижения: " + entity.getAchievementPoints() +
                        " |💲Деньги: " + money +
                        " |📊 Аналитика: " + (entity.getAnalytics() != null ? entity.getAnalytics() : 0) +
                        " |🛡 Сопротивление дедлайну: " + (entity.getDeadlineResistance() != null ? entity.getDeadlineResistance() : 0);
            case "Personage2":
                return "|🏆Level: " + entity.getLevel() +
                        " |⚡️Энергия: " + entity.getEnergy() +
                        " |⭐️Очки достижения: " + entity.getAchievementPoints() +
                        " |💲Деньги: " + money +
                        " |😁 Юмор: " + (entity.getHumor() != null ? entity.getHumor() : 0) +
                        " |💬 Навыки коммуникации: " + (entity.getCommunication() != null ? entity.getCommunication() : 0);
            case "Personage3":
                return "|🏆Level: " + entity.getLevel() +
                        " |⚡️Энергия: " + entity.getEnergy() +
                        " |⭐️Очки достижения: " + entity.getAchievementPoints() +
                        " |💲Деньги: " + money +
                        " |💾 Точность кода: " + (entity.getCodeAccuracy() != null ? entity.getCodeAccuracy() : 0) +
                        " |⚙️ Оптимизация: " + (entity.getOptimization() != null ? entity.getOptimization() : 0);
            default:
                return "|⚡️Энергия: " + entity.getEnergy();
        }
    }

    /**
     * Хелпер для безопасного вывода Integer (null -> 0)
     */
    private static int safe(Integer value) {
        return value != null ? value : 0;
    }
}
