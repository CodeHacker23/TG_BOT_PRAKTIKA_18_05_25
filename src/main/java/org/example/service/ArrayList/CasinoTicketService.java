package org.example.service.ArrayList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;



/**
 * CasinoTicketService — главный дирижёр всего, что связано с казино в сюжете ArrayList.
 *
 * Тут мы:
 *  - применяем статовые изменения при выборе билетов,
 *  - снимаем клавиатуры, чтобы игрок не спамил по сто раз,
 *  - запускаем викторины с задержками,
 *  - планируем реплики Итераториуса и подготовку к раунду 3.
 *
 * Если что-то в казино работает не так — с вероятностью 99% нужно ковырять этот класс.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CasinoTicketService {
    private final UserService userService;           // Для получения юзера из БД по Telegram ID
    private final StatService statService;          // Для изменения статов персонажа (деньги, опыт)
    private final EpicQuizTimerService epicQuizTimerService;
    private final ScheduledExecutorService replyScheduler = Executors.newSingleThreadScheduledExecutor();
    private static final long ITERATORIUS_DELAY_SECONDS = 4; // кастомное значение через которое должен ответить наш Итераториус после отправки билета

    /**
     * BUTTON_TO_TICKET — это наш внутренний «гугл-переводчик» с эмодзи на вменяемые цифры.
     *
     * Почему без него всё развалится:
     *  • Telegram присылает нам нажатия ровно теми символами, что мы рисовали на клавиатуре (1⃣, 2⃣, …, 🔟).
     *  • Пихать эти пиктограммы в switch/if — боль и страдания, плюс их фиг прочтёшь в логах.
     *  • Хотим поменять кнопки → меняем только эту мапу. Все остальные методы даже не поймут,
     *    что дизайнера снова накрыло вдохновение.
     *
     * Итог: пользователь жмёт «7⃣», а весь код дальше живёт с обычным int 7 и не матерится.
     */
    private static final Map<String, Integer> BUTTON_TO_TICKET = Map.of(
            "1⃣", 1,
            "2⃣", 2,
            "3⃣", 3,
            "4⃣", 4,
            "5⃣", 5,
            "6⃣", 6,
            "7⃣", 7,
            "8⃣", 8,
            "9⃣", 9,
            "🔟", 10
    );


    /**
     * TicketResult — компактная коробка с двумя ништяками:
     *  1. message() — готовый SendMessage, который сразу летит в bot.execute().
     *  2. ticketNumber() — номер билета, чтобы дальше запланировать реплику Итераториуса,
     *     перейти к раунду 3, накидать мемов и вообще понимать, что только что нажали.
     *
     * Без этого рекорда пришлось бы тащить глобальные переменные или дёргать базу —
     * а так всё лежит рядом и не бесит.
     */
    public record TicketResult(SendMessage message, int ticketNumber) {}


    /**
     * iteratoriusReplies — карта "номер билета → реплика Итераториуса".
     *
     * В зависимости от результата (плюс/минус/джекпот/викторина) выбираем нужный текст,
     * чтобы через пару секунд командор откомментировал выбор игрока.
     */
    /**
     * 🗺️ КАРТА РЕПЛИК ИТЕРАТОРИУСА ПО БИЛЕТАМ
     * 
     * КЛЮЧИ: номер билета (1-10)
     * ЗНАЧЕНИЯ: функция, которая создаёт реплику Итераториуса для этого билета
     * 
     * ⚠️ ВАЖНО: билеты 9 и 10 НЕ включены в эту мапу, потому что:
     * - Они запускают викторины через EpicQuizTimerService
     * - Реплика Итераториуса приходит ПОСЛЕ викторины (в методе sendResultComments)
     * - Если добавить их сюда, реплика вылетит ДО викторины, что ломает сюжет
     * 
     * Если добавишь новый билет без викторины - добавь его сюда, иначе defaultReply выстрелит.
     */
    private final Map<Integer, Function<Long, SendMessage>> iteratoriusReplies = Map.of(
            1, this::positiveReply,
            2, this::negativeReply,
            3, this::negativeReply,
            4, this::negativeReply,
            5, this::positiveReply,
            6, this::negativeReply,
            7, this::positiveReply,
            8, this::jackpotReply
            // 9 и 10 НЕ ТУТ - у них реплика после викторины!
    );

    /**
     * scheduleIteratoriusReply — планировщик сарказма.
     *
     * Пользователь получает описание билета, читает, расслабляется... и тут через ITERATORIUS_DELAY_SECONDS
     * вваливается Итераториус с мотивацией/токсичностью. Вся магия строится на replyScheduler:
     *  1. Отбираем нужный шаблон реплики из iteratoriusReplies (или defaultReply, если кто-то забыл дописать логику).
     *  2. Планируем задачу на будущее, чтобы не блокировать основной поток бота.
     *  3. В нужный момент bot.execute(...) шлёт реплику, а в логах видно, что всё прошло по плану.
     *
     * Это позволяет держать единый темп: билет → пауза → комментарий → дальше можно начинать раунд 3, слать мемы и т.д.
     */
    public void scheduleIteratoriusReply(TelegramLongPollingBot bot, Long chatId, int ticketNumber) {
        // ⚠️ БИЛЕТЫ 9 И 10 НЕ ДОЛЖНЫ ИМЕТЬ РЕПЛИКУ ДО ВИКТОРИНЫ!
        // У них реплика приходит ПОСЛЕ викторины через EpicQuizTimerService.sendResultComments()
        if (ticketNumber == 9 || ticketNumber == 10) {
            log.debug("scheduleIteratoriusReply: билет {} пропускаем — реплика будет после викторины, chatId={}", ticketNumber, chatId);
            return;
        }
        
        Function<Long, SendMessage> replyFactory = iteratoriusReplies.getOrDefault(ticketNumber, this::defaultReply);

        log.debug("scheduleIteratoriusReply: планируем реплику Итераториуса, билет={}, chatId={}, задержка={}с",
                ticketNumber, chatId, ITERATORIUS_DELAY_SECONDS);

        replyScheduler.schedule(() -> {
            try {
                log.info("scheduleIteratoriusReply: Итераториус выходит на связь для chatId={}, билет={}", chatId, ticketNumber);
                bot.execute(replyFactory.apply(chatId));
                log.info("scheduleIteratoriusReply: Реплика Итераториуса успешно отправлена для chatId={}", chatId);
            } catch (TelegramApiException e) {
                log.error("scheduleIteratoriusReply: Итераториус захлебнулся ошибкой для chatId={}: {}", chatId, e.getMessage(), e);
            }
        }, ITERATORIUS_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * defaultReply — запасной выход, когда кто-то добавил новый билет, но забыл прикрутить реплику.
     * Вместо молчания мозготрепный Итераториус объяснит, что кнопки жать надо с мозгами.
     */
    private SendMessage defaultReply(Long chatId) {
        log.warn("defaultReply: билет не найден в iteratoriusReplies, chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус*  \n\n" +
                "_Ты нажал что-то новое, а я к этому не подготовился._ \n" +
                "Считай это экспериментом: в следующий раз предупреди, прежде чем ломать сценарии.");
        return sendMessage;
    }

    //оптимистичный ответ Итераториуса на вытаскивание билетика и подготовка к 3 раунду
    private SendMessage positiveReply(Long chatId) {
        log.debug("positiveReply: собираем радостную реплику для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(chatId);
            sendMessage.setParseMode("Markdown");
            sendMessage.setText("*Итераториус*  \n\n" +
                    "_Улыбнись. Ты только что вытащил билет, который не пытается тебя убить._ \n" +
                    "Забирай награду и не задерживайся: такие моменты — как быстрый билд без фейлов, длятся секунд тридцать.");
            return sendMessage;
    }

    //Пессеместичный ответ на вытаскивание такого себе билетика
    public SendMessage negativeReply(Long chatId) {
        log.debug("negativeReply: собираем токсичную реплику для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус*  \n\n" +
                "Поздравляю, ты официально наступил на ```RuntimeException``` \n\n" +
                "Был шанс остановиться, но ты пошёл дальше — в минуса. \n" +
                "*Это не проигрыш, это новая ачивка: «люблю страдать».*\n" +
                "\n" +
                "_Дыши глубже. Эти штрафы — просто напоминание, что кнопки тоже кусаются. Следующий раз бей по клаве с мозгом, а не с закрытыми глазами._\n");
        return sendMessage;
    }

    //ответ на джекпот
    public SendMessage jackpotReply(Long chatId) {
        log.debug("jackpotReply: собираем джекпотную реплику для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус*  \n\n" +
                "_Джекпот, малец. Это подтверждение, что ты можешь вытаскивать невозможное!_ \n\n" +
                "Запомни этот момент: пока остальные боятся ArrayList, ты уже вытряхиваешь из него золото. \n " +
                "Держи курс — и следующий джекпот ты не найдёшь, а построишь его сам!");

        return sendMessage;
    }


    //билет 1
    public SendMessage ticket1(Long chatId) {
        log.debug("ticket1: Создание результата билета 1 для chatId={}", chatId);

        // ПРИМЕНЯЕМ ИЗМЕНЕНИЯ СТАТОВ ЧЕРЕЗ StatService  
        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", 100,
                "currency", 300
        ));


        // СОЗДАЕМ СООБЩЕНИЕ С ПОДДЕРЖКОЙ MARKDOWN РАЗМЕТКИ
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode("Markdown");  // ← ВОТ ЭТО ВКЛЮЧАЕТ ОБРАБОТКУ *, _, ``` и т.д.
        message.setText(
                "🎁 БИЛЕТ 1: ```NullPointerException```\n" +
                        "+300 💲 денег, +100 ⭐️ опыта\n\n" +
                        "*Ха! Null оказался не таким уж плохим!*\n" +
                        "_Иногда даже отсутствие данных — это подарок!_"
        );

        // TODO: на время тестирования оставляем клавиатуру — потом раскомментируем строки ниже
        // ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        // message.setReplyMarkup(remove);

        return message;
    }

    //Билет 2
    public SendMessage ticket2(Long chatId) {
        log.debug("ticket2: Создание результата билета 2 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", -50,
                "currency", -200
        ));

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode("Markdown");
        message.setText(
                "💀 БИЛЕТ 2: ```OutOfMemoryError```\n" +
                        "-200 💲 денег, -50 ⭐️ опыта\n\n" +
                        "*Память закончилась... как и твои деньги!*\n" +
                        "_Welcome to the real world, малец!_"
        );

        // TODO: на время тестирования оставляем клавиатуру — потом раскомментируем строки ниже
        // ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        // message.setReplyMarkup(remove);

        return message;
    }

    //билет 3
    public SendMessage ticket3(Long chatId) {
        log.debug("ticket3: Создание результата билета 3 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", -100,
                "currency", -250
        ));

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode("Markdown");
        message.setText(
                "💀 БИЛЕТ 3: ```StackOverflowError```\n" +
                        "-250 💲 денег, -100 ⭐️ опыта\n\n" +
                        "*Стек переполнился... как и твои проблемы!*\n" +
                        "_Рекурсия — это как алкоголизм, только для кода!_"
        );

        // TODO: на время тестирования оставляем клавиатуру — потом раскомментируем строки ниже
        // ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        // message.setReplyMarkup(remove);
        return message;
    }

    //билет 4
    public SendMessage ticket4(Long chatId) {
        log.debug("ticket4: Создание результата билета 4 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", -120,
                "currency", -300
        ));

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode("Markdown");
        message.setText(
                "💀 БИЛЕТ 4: ```ClassCastException```\n" +
                        "-300 💲 денег, -120 ⭐️ опыта\n\n" +
                        "*Нельзя привести String к Integer... как и твои надежды!*\n" +
                        "_Иногда лучше остаться собой, чем пытаться быть кем-то другим!_"
        );

        // TODO: на время тестирования оставляем клавиатуру — потом раскомментируем строки ниже
        // ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        // message.setReplyMarkup(remove);
        return message;
    }


    //билет 5
    public SendMessage ticket5(Long chatId) {
        log.debug("ticket5: Создание результата билета 5 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", 200,
                "currency", 500
        ));

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode("Markdown");
        message.setText(
                "🎁 БИЛЕТ 5: ```IllegalArgumentException```\n" +
                        "+500 💲 денег, +200 ⭐️ опыта\n\n" +
                        "*Аргумент был нелегальным, но награда — легальная!*\n" +
                        "_В программировании, как в жизни — всё относительно!_"
        );

        // TODO: на время тестирования оставляем клавиатуру — потом раскомментируем строки ниже
        // ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        // message.setReplyMarkup(remove);
        return message;
    }

    //билет 6
    public SendMessage ticket6(Long chatId) {
        log.debug("ticket6: Создание результата билета 6 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", -50,
                "currency", -150
        ));

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode("Markdown");
        message.setText(
                "💀 БИЛЕТ 6: ```ConcurrentModificationException```\n" +
                        "-150 💲 денег, -50 ⭐️ опыта\n\n" +
                        "*Попытался изменить список во время итерации... классика!*\n" +
                        "_Это как пытаться перекрасить машину во время езды!_"
        );

        // TODO: на время тестирования оставляем клавиатуру — потом раскомментируем строки ниже
        // ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        // message.setReplyMarkup(remove);
        return message;
    }

    //билет 7
    public SendMessage ticket7(Long chatId) {
        log.debug("ticket7: Создание результата билета 7 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", 150,
                "currency", 400
        ));

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode("Markdown");
        message.setText(
                "🎁 БИЛЕТ 7: ```ArrayIndexOutOfBoundsException```\n" +
                        "+400 💲 денег, +150 ⭐️ опыта\n\n" +
                        "*Иногда выйти за границы — значит найти новые возможности!*\n" +
                        "_Главное, чтобы прод это не увидел!_"
        );

        // TODO: на время тестирования оставляем клавиатуру — потом раскомментируем строки ниже
        // ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        // message.setReplyMarkup(remove);
        return message;
    }

    //билет 8
    public SendMessage ticket8(Long chatId) {
        log.debug("ticket8: Создание результата билета 8 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", 500,
                "currency", 1000
        ));

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode("Markdown");
        message.setText(
                "🚀🎉 БИЛЕТ 8: ```ДЖЕКПОТ - Редкое исключение!```\n" +
                        "+1000 💲 денег, +500 ⭐️ опыта 🔥\n\n" +
                        "*ВАУ! Ты вытащил ДЖЕКПОТ!*\n\n" +
                        "*Это как найти золотой баг в продакшене!*\n" +
                        "_Теперь ты можешь купить себе новый MacBook!_\n" +
                        "_Или хотя бы доширак на неделю!_"
        );

        // TODO: на время тестирования оставляем клавиатуру — потом раскомментируем строки ниже
        // ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        // message.setReplyMarkup(remove);
        return message;
    }

    /**
     * 🎲 БИЛЕТ 9: ЭПИЧЕСКАЯ ВИКТОРИНА С ТАЙМЕРОМ
     * <p>
     * ЭТАПЫ БИЛЕТА 9:
     * 1. Показываем информационное сообщение о том что это особый билет
     * 2. Пауза 6 секунд чтобы пользователь прочитал
     * 3. Запускаем эпическую викторину с таймером в отдельном потоке
     *
     * @param bot    - объект бота для запуска викторины
     * @param chatId - ID чата пользователя
     * @return SendMessage - информационное сообщение (этап 1)
     */
    public SendMessage ticket9(TelegramLongPollingBot bot, Long chatId) {
        log.info("CasinoTicketService: 🎲 Билет 9 - запуск эпической викторины для chatId={}", chatId);

        // Проверяем что пользователь существует
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("CasinoTicketService: Пользователь или персонаж не найден для chatId={}", chatId);
            return new SendMessage(chatId.toString(), "❌ Ошибка: персонаж не найден!");
        }

        // 🎯 ЗАПУСКАЕМ ВИКТОРИНУ В ОТДЕЛЬНОМ ПОТОКЕ С ЗАДЕРЖКОЙ 6 СЕКУНД
        startQuizWithDelay(bot, chatId);

        // Возвращаем ПЕРВОЕ информационное сообщение (показывается сразу)
        SendMessage infoMessage = new SendMessage();
        infoMessage.setChatId(chatId);
        infoMessage.setParseMode("Markdown");
        infoMessage.setText(
                "🎲 *БИЛЕТ 9: \"ЭПИЧЕСКАЯ ВИКТОРИНА\"*\n\n" +
                        "🎮 *Поздравляем!* Ты вытащил особый билет!\n\n" +
                        "Вместо обычной награды тебя ждет:\n" +
                        "🕐 **Интерактивная викторина с таймером**\n" +
                        "⚡ **Бонусы за скорость ответа**\n" +
                        "🏆 **Увеличенные награды за правильный ответ**\n\n" +
                        "💡 *Совет:* Читай внимательно, время ограничено!"
        );


        // TODO: на время тестирования оставляем клавиатуру — потом раскомментируем строки ниже
        // ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        // infoMessage.setReplyMarkup(remove);
        log.info("CasinoTicketService: ✅ Информационное сообщение отправлено, викторина запустится через 11 секунд для chatId={}", chatId);
        return infoMessage;
    }

    /**
     * 🕐 ЗАПУСКАЕТ ВИКТОРИНУ С ЗАДЕРЖКОЙ 11 СЕКУНД
     * <p>
     * ЗАЧЕМ ЭТОТ МЕТОД НУЖЕН:
     * - Пользователь нажимает кнопку 9⃣ и СРАЗУ получает информационное сообщение
     * - Параллельно запускается этот метод в отдельном потоке
     * - Он ждет 11 секунд чтобы пользователь успел прочитать информацию
     * - Только потом запускает саму викторину с таймером
     * <p>
     * ПОЧЕМУ ОТДЕЛЬНЫЙ ПОТОК:
     * - Если бы мы делали Thread.sleep() в основном потоке, бот "завис" бы на 11 секунд
     * - Пользователь не смог бы нажимать другие кнопки, бот не отвечал бы на команды
     * - Отдельный поток позволяет боту работать нормально, пока идет ожидание
     *
     * @param bot    - объект бота (нужен для запуска викторины через 11 сек)
     * @param chatId - ID чата пользователя (куда отправлять викторину)
     */
    private void startQuizWithDelay(TelegramLongPollingBot bot, Long chatId) {

        // СОЗДАЕМ НОВЫЙ ПОТОК (НЕ БЛОКИРУЕМ ОСНОВНОЙ ПОТОК БОТА)
        // Каждый раз когда кто-то нажимает билет 9, создается отдельный поток для ожидания
        Thread delayedQuizThread = new Thread(() -> {
            try {
                log.debug("CasinoTicketService: Ждем 11 секунд перед запуском викторины для chatId={}", chatId);

                // ПАУЗА 11 СЕКУНД - ПОЛЬЗОВАТЕЛЬ ЧИТАЕТ ИНФОРМАЦИЮ О БИЛЕТЕ
                // Thread.sleep() останавливает ТОЛЬКО ЭТОТ поток, основной бот продолжает работать
                Thread.sleep(11000); // 11000 миллисекунд = 11 секунд

                log.info("CasinoTicketService: Пауза завершена, запускаем викторину для chatId={}", chatId);

                // ПОДГОТАВЛИВАЕМ ДАННЫЕ ВИКТОРИНЫ
                // Это будет передано в EpicQuizTimerService для создания интерактивной викторины
                String question = "🎯 ArrayList.add(0, element) - что происходит под капотом?";
                List<String> options = Arrays.asList(
                        "🅰️ Элемент добавляется мгновенно O(1)",        // Неправильно - будет штраф
                        "🅱️ Все элементы сдвигаются вправо O(n)",       // Правильно - будет награда  
                        "🅲️ Массив пересоздается заново O(n)",          // Неправильно - будет штраф
                        "🅳️ Выбрасывается IndexOutOfBoundsException"    // Неправильно - будет штраф
                );
                String correctAnswer = "🅱️ Все элементы сдвигаются вправо O(n)";

                // ПЕРЕДАЕМ УПРАВЛЕНИЕ В EpicQuizTimerService 
                // Он создаст сообщение с таймером, кнопками и всеми эффектами
                epicQuizTimerService.startEpicQuizWithTimer(bot, chatId, question, options, correctAnswer);

            } catch (InterruptedException e) {
                // ЕСЛИ ПОТОК ПРЕРВАН (например при остановке бота)
                log.error("CasinoTicketService: Поток викторины был прерван для chatId={}: {}", chatId, e.getMessage());
                Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания для корректного завершения
            } catch (Exception e) {
                // ЛЮБЫЕ ДРУГИЕ ОШИБКИ (проблемы с ботом, базой данных и т.д.)
                log.error("CasinoTicketService: Ошибка запуска викторины для chatId={}: {}", chatId, e.getMessage());
            }
        });

        // ДАЕМ ПОТОКУ ПОНЯТНОЕ ИМЯ ДЛЯ ОТЛАДКИ
        // В логах будет видно "QuizDelay-123456789" вместо "Thread-1"
        delayedQuizThread.setName("QuizDelay-" + chatId);

        // ЗАПУСКАЕМ ПОТОК (он начинает выполняться параллельно с основным кодом)
        delayedQuizThread.start();

        log.debug("CasinoTicketService: Поток задержки викторины запущен для chatId={}", chatId);
    }

    /**
     * 🏆 БИЛЕТ 10: ЭКСПЕРТНАЯ ВИКТОРИНА
     * <p>
     * ЭТАПЫ БИЛЕТА 10:
     * 1. Показываем информационное сообщение о том что это ЭКСПЕРТНАЯ викторина
     * 2. Пауза 11 секунд чтобы пользователь прочитал и приготовился
     * 3. Запускаем СЛОЖНУЮ викторину с таймером в отдельном потоке
     * <p>
     * ОСОБЕННОСТИ ЭКСПЕРТНОЙ ВИКТОРИНЫ:
     * - Вопрос гораздо сложнее чем в билете 9
     * - Увеличенные награды (но пользователь не знает сколько именно)
     * - Увеличенные штрафы (интрига сохраняется)
     * - Требует глубокого понимания ArrayList
     *
     * @param bot    - объект бота для запуска викторины через 11 сек
     * @param chatId - ID чата пользователя (куда отправлять викторину)
     * @return SendMessage - информационное сообщение об экспертной викторине
     */
    public SendMessage ticket10(TelegramLongPollingBot bot, Long chatId) {
        log.info("CasinoTicketService: 🏆 Билет 10 - запуск ЭКСПЕРТНОЙ викторины для chatId={}", chatId);

        // Проверяем что пользователь существует
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("CasinoTicketService: Пользователь или персонаж не найден для chatId={}", chatId);
            return new SendMessage(chatId.toString(), "❌ Ошибка: персонаж не найден!");
        }

        // 🎯 ЗАПУСКАЕМ СЛОЖНУЮ ЭКСПЕРТНУЮ ВИКТОРИНУ В ОТДЕЛЬНОМ ПОТОКЕ С ЗАДЕРЖКОЙ 11 СЕКУНД
        startJackpotQuizWithDelay(bot, chatId);

        // Возвращаем информационное сообщение об экспертной викторине (показывается сразу)
        SendMessage infoMessage = new SendMessage();
        infoMessage.setChatId(chatId);
        infoMessage.setParseMode("Markdown");
        infoMessage.setText(
                "🏆 *БИЛЕТ 10: \"ЭКСПЕРТНАЯ ВИКТОРИНА\"* 🏆\n\n" +
                        "🎓 *ПОЗДРАВЛЯЕМ! ТЫ ПОЛУЧИЛ ИСПЫТАНИЕ!* 🎓\n\n" +
                        "Тебя ждет *МАКСИМАЛЬНО СЛОЖНАЯ* викторина:\n" +
                        "🧠 **Экспертный вопрос по ArrayList**\n" +
                        "⚡ **Повышенная сложность**\n" +
                        "💰 **Увеличенные награды за знания**\n" +
                        "💀 **Серьезные штрафы за ошибки**\n\n" +
                        "⚠️ *ВНИМАНИЕ:* Этот вопрос могут решить только *настоящие эксперты!*\n" +
                        "💡 *Совет:* Думай как компилятор Java!\n" +
                        "🤔 *Интрига:* Награда зависит от твоих знаний..."
        );

        // TODO: на время тестирования оставляем клавиатуру — потом раскомментируем строки ниже
        // ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        // infoMessage.setReplyMarkup(remove);

        log.info("CasinoTicketService: ✅ Экспертная викторина сообщение отправлено, викторина запустится через 11 секунд для chatId={}", chatId);
        return infoMessage;
    }

    /**
     * 🏆 ЗАПУСКАЕТ ЭКСПЕРТНУЮ ВИКТОРИНУ С ЗАДЕРЖКОЙ 11 СЕКУНД
     * <p>
     * ЗАЧЕМ ОТДЕЛЬНЫЙ МЕТОД ДЛЯ ЭКСПЕРТНОЙ ВИКТОРИНЫ:
     * - Экспертная викторина имеет другой вопрос (сложнее)
     * - Другие награды/штрафы (больше, но пользователь не знает сколько)
     * - Может иметь другое время (пока 30 сек, но можно изменить)
     * - Логика такая же как в ticket9, но данные другие
     *
     * @param bot    - объект бота (нужен для запуска викторины через 11 сек)
     * @param chatId - ID чата пользователя (куда отправлять викторину)
     */
    private void startJackpotQuizWithDelay(TelegramLongPollingBot bot, Long chatId) {

        // СОЗДАЕМ НОВЫЙ ПОТОК ДЛЯ ЭКСПЕРТНОЙ ВИКТОРИНЫ (НЕ БЛОКИРУЕМ ОСНОВНОЙ ПОТОК БОТА)
        Thread delayedExpertThread = new Thread(() -> {
            try {
                log.debug("CasinoTicketService: Ждем 11 секунд перед запуском ЭКСПЕРТНОЙ викторины для chatId={}", chatId);

                // ПАУЗА 11 СЕКУНД - ПОЛЬЗОВАТЕЛЬ ГОТОВИТСЯ К СЛОЖНОМУ ВОПРОСУ
                Thread.sleep(11000); // 11000 миллисекунд = 11 секунд

                log.info("CasinoTicketService: Пауза завершена, запускаем ЭКСПЕРТНУЮ викторину для chatId={}", chatId);

                // ПОДГОТАВЛИВАЕМ ДАННЫЕ СЛОЖНОЙ ЭКСПЕРТНОЙ ВИКТОРИНЫ
                String question = "🧠 ArrayList capacity=10, size=8. Вызов add(element). Что произойдет с внутренним массивом?";
                List<String> options = Arrays.asList(
                        "🅰️ Массив расширится до capacity=20",         // Неправильно - будет увеличенный штраф  
                        "🅱️ Массив останется 10, добавится элемент",   // Правильно - будет увеличенная награда
                        "🅲️ Массив расширится до capacity=15 (1.5x)",  // Неправильно - будет увеличенный штраф
                        "🅳️ Массив пересоздастся с capacity=16"        // Неправильно - будет увеличенный штраф
                );
                String correctAnswer = "🅱️ Массив останется 10, добавится элемент";

                // ПЕРЕДАЕМ УПРАВЛЕНИЕ В EpicQuizTimerService С ОСОБЫМИ ПАРАМЕТРАМИ ДЛЯ ЭКСПЕРТНОЙ ВИКТОРИНЫ
                // Он создаст сообщение с таймером, но будет знать что это экспертная викторина
                epicQuizTimerService.startJackpotQuizWithTimer(bot, chatId, question, options, correctAnswer);

            } catch (InterruptedException e) {
                // ЕСЛИ ПОТОК ПРЕРВАН (например при остановке бота)
                log.error("CasinoTicketService: Поток ЭКСПЕРТНОЙ викторины был прерван для chatId={}: {}", chatId, e.getMessage());
                Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания для корректного завершения
            } catch (Exception e) {
                // ЛЮБЫЕ ДРУГИЕ ОШИБКИ (проблемы с ботом, базой данных и т.д.)
                log.error("CasinoTicketService: Ошибка запуска ЭКСПЕРТНОЙ викторины для chatId={}: {}", chatId, e.getMessage());
            }
        });

        // ДАЕМ ПОТОКУ ПОНЯТНОЕ ИМЯ ДЛЯ ОТЛАДКИ
        // В логах будет видно "ExpertQuizDelay-123456789" вместо "Thread-1"
        delayedExpertThread.setName("ExpertQuizDelay-" + chatId);

        // ЗАПУСКАЕМ ПОТОК (он начинает выполняться параллельно с основным кодом)
        delayedExpertThread.start();

        log.debug("CasinoTicketService: Поток задержки ЭКСПЕРТНОЙ викторины запущен для chatId={}", chatId);
    }

    /**
     * handleTicketSelection — главный диспетчер билетов.
     * Переводит эмодзи в номера, поднимает нужный сценарий и возвращает TicketResult.
     */
    /**
     * handleTicketSelection — мозг казино и главный диспетчер кнопок.
     *
     * Что происходит под капотом:
     *  1. Берём сырое значение кнопки (эмодзи) и прогоняем через BUTTON_TO_TICKET.
     *  2. Если кнопка неизвестна — честно говорим об этом пользователю и логируем его подвиг.
     *  3. Для валидных билетов дергаем ticketN(...) и собираем TicketResult,
     *     чтобы выше по стеку было понятно, что отправлять и какую саркастическую ленту запускать.
     *
     * Важный момент: ничего тут не отправляется. Метод только готовит данные, а bot.execute(...)
     * происходит уже в MessageHandlerService — там же мы планируем реплику Итераториуса.
     */
    public TicketResult handleTicketSelection(TelegramLongPollingBot bot, Long chatId, String buttonText) {
        log.info("handleTicketSelection: пользователь ткнул '{}', chatId={}", buttonText, chatId);

        Integer ticketNumber = BUTTON_TO_TICKET.get(buttonText);
        if (ticketNumber == null) {
            log.warn("handleTicketSelection: неизвестный билет '{}', chatId={}", buttonText, chatId);
            SendMessage unknown = new SendMessage(chatId.toString(), "❌ Неизвестный билет!");
            return new TicketResult(unknown, -1);
        }

        return switch (ticketNumber) {
            case 1 -> buildTicketResult(chatId, 1, this::ticket1);
            case 2 -> buildTicketResult(chatId, 2, this::ticket2);
            case 3 -> buildTicketResult(chatId, 3, this::ticket3);
            case 4 -> buildTicketResult(chatId, 4, this::ticket4);
            case 5 -> buildTicketResult(chatId, 5, this::ticket5);
            case 6 -> buildTicketResult(chatId, 6, this::ticket6);
            case 7 -> buildTicketResult(chatId, 7, this::ticket7);
            case 8 -> buildTicketResult(chatId, 8, this::ticket8);
            case 9 -> new TicketResult(ticket9(bot, chatId), 9);
            case 10 -> new TicketResult(ticket10(bot, chatId), 10);
            default -> new TicketResult(new SendMessage(chatId.toString(), "❌ Пока не придумал, что это за билет"), ticketNumber);
        };
    }

    /**
     * buildTicketResult — мини-фабрика TicketResult, чтобы switch не превратился в простыню копипасты.
     *
     * Что делает:
     *  • вызывает нужный ticketN(chatId), который возвращает SendMessage с описанием и удалением клавы;
     *  • логирует факт сборки, чтобы в логах была хронология;
     *  • упаковывает сообщение и номер билета в TicketResult.
     *
     * Добавляем новый билет → просто передаём его сюда, и всё продолжает работать как часы.
     */
    private TicketResult buildTicketResult(Long chatId, int ticketNumber, Function<Long, SendMessage> ticketFactory) {
        SendMessage message = ticketFactory.apply(chatId);
        log.debug("buildTicketResult: билет {} готов для chatId={}", ticketNumber, chatId);
        return new TicketResult(message, ticketNumber);
    }

}
