package org.example.service.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

// 🔥 ЗДЕСЬ БУДЕТ ЭПИЧЕСКАЯ МАГИЯ!
@Slf4j
@Service
@RequiredArgsConstructor
public class EpicQuizTimerService {
    
    private final StatService statService; // Для применения наград/штрафов
    /**
     * 🔄 КАК ЭТО РАБОТАЕТ ВМЕСТЕ:
     * Пользователь начинает викторину → создается QuizSession
     * Сессия добавляется в activeSessions → map.put(chatId, session)
     * Один из 10 работников scheduler'а берет задачу таймера
     * Каждую секунду этот работник обновляет сообщение
     * Когда викторина заканчивается → сессия удаляется из карты
     */
    /**
     * Зачем 10 работников:
     * У тебя может быть 50 пользователей одновременно играющих в викторину
     * Каждому нужен свой таймер
     * 10 работников могут обслужить много таймеров параллельно
     * Если один таймер "тормозит" — остальные продолжают работать
     * Аналогия: Как ресторан — один повар vs 10 поваров. 10 поваров обслужат больше столиков одновременно.
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    /**
     * Для чего в эпик-версии: StatService
     * Применяем штрафы за тайм-аут
     * Даем бонусы за скорость ответа
     * Сохраняем изменения в БД
     */
    /**
     * Это "журнал регистрации" всех активных викторин.
     * Структура:
     * Ключ (Long): chatId пользователя (123456789)
     * Значение (QuizSession): данные викторины (вопрос, время, правильный ответ)
     * ==============================================================================
     * // ПЛОХО - обычная HashMap:
     * Map<Long, QuizSession> sessions = new HashMap<>();
     * // Поток 1: добавляет сессию
     * // Поток 2: одновременно читает сессии
     * // HashMap может сломаться! 💥
     * <p>
     * // ХОРОШО - потокобезопасная:
     * Map<Long, QuizSession> sessions = new ConcurrentHashMap<>();
     * // Безопасно для многопоточности! ✅
     */
    private final Map<Long, QuizSession> activeSessions = new ConcurrentHashMap<>();

    /**
     * 🎮 ЗАПУСКАЕТ ЭПИЧЕСКУЮ ВИКТОРИНУ С КОМБО-ТАЙМЕРОМ
     *
     * @param bot           - объект бота для отправки/изменения сообщений
     * @param chatId        - ID чата пользователя
     * @param question      - текст вопроса викторины
     * @param options       - варианты ответов (например: ["🅰️ Вариант A", "🅱️ Вариант B"])
     * @param correctAnswer - правильный ответ из списка options
     */
    public void startEpicQuizWithTimer(TelegramLongPollingBot bot, Long chatId, String question, List<String> options, String correctAnswer) {
        log.info("EpicQuizTimer: 🎮 Запуск эпической викторины для chatId={}", chatId);
        log.debug("EpicQuizTimer: Вопрос: {}, варианты: {}, правильный ответ: {}",
                question, options, correctAnswer);

        int timeLimit = 30; // секунд на ответ
        QuizSession session = new QuizSession(question, options, correctAnswer, timeLimit);
        session.setTicketNumber(9); // ← ПОМЕЧАЕМ ЧТО ЭТО БИЛЕТ 9!
        activeSessions.put(chatId, session);

        try {
            // 1. Создаем эпическое сообщение (метод создадим на следующем шаге!)
            SendMessage quizMessage = createEpicQuizMessage(chatId, session);

            // 2. Отправляем и сохраняем messageId
            Message sentMessage = bot.execute(quizMessage);
            session.setMessageId(sentMessage.getMessageId());

            // 3. Запускаем таймер
            startEpicTimer(bot, chatId, session);

            log.info("EpicQuizTimer: ✅ Таймер успешно запущен для chatId={}", chatId);

        } catch (TelegramApiException e) {
            log.error("EpicQuizTimer: ❌ Ошибка отправки эпической викторины для chatId={}: {}",
                    chatId, e.getMessage());
            activeSessions.remove(chatId);
        }
    }


    /**
     * 🎨 СОЗДАЕТ ЭПИЧЕСКОЕ СООБЩЕНИЕ С ТАЙМЕРОМ
     * <p>
     * Этот метод формирует красивое сообщение которое будет изменяться каждую секунду
     *
     * @param chatId  - ID чата для отправки
     * @param session - данные викторины (вопрос, время, варианты)
     * @return SendMessage - готовое сообщение для отправки в Telegram
     */
    private SendMessage createEpicQuizMessage(Long chatId, QuizSession session) {
        log.debug("EpicQuizTimer: Создание эпического сообщения для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText(buildEpicQuizText(session, null));
        sendMessage.setReplyMarkup(createQuizKeyboard(session.getOptions()));
        return sendMessage;

    }

    /**
     * 🔥 СТРОИТ ЕБИЧЕСКИ КРУТОЙ ТЕКСТ С ЭФФЕКТАМИ
     * <p>
     * Этот метод собирает все части сообщения:
     * - Динамический заголовок (зависит от оставшегося времени)
     * - Отображение времени с анимированными эмодзи
     * - Цветной прогресс-бар (зеленый → желтый → красный)
     * - Текст вопроса с вариантами ответов
     * - Мотивационные фразы
     * - Предупреждения о времени (если есть)
     *
     * @param session - данные викторины с текущим временем
     * @param warningText - текст предупреждения (null если нет предупреждения)
     * @return String - готовый текст сообщения
     */
    private String buildEpicQuizText(QuizSession session, String warningText) {

        /**
         * Зачем оба значения:
         * timeLeft — для отображения "00:15"
         * totalTime — для расчета процента (15/30 = 50%)
         */
        int timeLeft = session.getTimeLeft();
        int totalTime = session.getTotalTime();

        // 🎭 Заголовок в зависимости от времени
        //getEpicHeader(timeLeft, totalTime) — динамический заголовок:
        // При 80% времени: "🎯 *ЭПИЧЕСКАЯ ВИКТОРИНА РАУНД 2*"
        // При 30% времени: "⚡ *ВРЕМЯ НА ИСХОДЕ!*"
        // При 10% времени: "💀 *ПОСЛЕДНИЕ СЕКУНДЫ!* 💀"
        String header = getEpicHeader(timeLeft, totalTime, session.getTicketNumber()); // Текущее время (30→29→28→...→0) + номер билета

        // ⏰ Время с эмодзи
        // "🕐 Время: 00:27"
        // "🔥 Время: 00:10" (последние секунды)
        String timeDisplay = getTimeDisplay(timeLeft);// Изначальное время (30)  в  private int timeLeft;

        // 🎨 Прогресс-бар с цветами
        //🟩🟩🟩🟩🟩🟩🟩🟨🟨🟨  [70%]
        //🟩🟩🟩🟥🟥🟥🟥🟥🟥🟥  [30%]
        String progressBar = getColoredProgressBar(timeLeft, totalTime);

        // 💫 Мотивационный текст
        // "⚡ Быстрый ответ = больше наград!"
        // "🚨 *ПОСЛЕДНИЙ ШАНС!* 🚨"
        String motivation = getMotivationalText(timeLeft, totalTime);

        // 📊 Вопрос с вариантами
        // "❓ Сколько кофе нужно программисту?
        //
        //  🅰️ Одну чашку
        //  🅱️ Три чашки
        //  🅲️ Всю кофеварку"
        String questionBlock = formatQuestionWithOptions(session);

        // Добавляем предупреждение если оно есть
        String result = String.format(
                "%s\n\n" +      // header + 2 переноса строки
                        "%s\n" +        // timeDisplay + 1 перенос
                        "%s\n\n" +      // progressBar + 2 переноса
                        "%s\n\n" +      // questionBlock + 2 переноса
                        "%s",           // motivation
                header, timeDisplay, progressBar, questionBlock, motivation
        );
        
        // Если есть предупреждение - добавляем его в конец
        if (warningText != null && !warningText.isEmpty()) {
            result += "\n\n" + warningText;
        }
        
        return result;
    }

    /**
     * 🎭 ЭПИЧЕСКИЕ ЗАГОЛОВКИ В ЗАВИСИМОСТИ ОТ ВРЕМЕНИ
     * <p>
     * Логика: чем меньше времени, тем драматичнее заголовок!
     * Создает эффект нарастающего напряжения
     *
     * @param timeLeft  - оставшееся время в секундах
     * @param totalTime - изначальное время
     * @return String - заголовок с эмодзи и форматированием
     */
    private String getEpicHeader(int timeLeft, int totalTime, int ticketNumber) {

        // Вычисляем соотношение оставшегося времени к общему Зачем (double):
        /**
         * // БЕЗ приведения типов (ПЛОХО):
         * int timeLeft = 25, totalTime = 30;
         * double bad = timeLeft / totalTime;     // Результат: 0.0 (целочисленное деление!)
         *
         * // С приведением типов (ХОРОШО):
         * double good = (double) timeLeft / totalTime; // Результат: 0.8333...
         */
        double ratio = (double) timeLeft / totalTime;


        /**
         * Проверяем условия от большего к меньшему
         * Примеры расчетов:
         * При старте: 30 / 30 = 1.0 (100% времени)
         * В середине: 15 / 30 = 0.5 (50% времени)
         * В конце: 3 / 30 = 0.1 (10% времени)
         *
         * 2. Логика условий if:
         * Порядок проверки ВАЖЕН!
         * 
         * 3. Номер билета:
         * ticketNumber=9 → "Билет 9: ВИКТОРИНИЩЕ"
         * ticketNumber=10 → "Билет 10: ЭКСПЕРТНАЯ ВИКТОРИНА"
         */
        String ticketText = (ticketNumber == 10) ? "ЭКСПЕРТНАЯ ВИКТОРИНА" : "ВИКТОРИНИЩЕ";
        
        if (ratio > 0.8) return String.format("🎯 *Билет %d: %s*", ticketNumber, ticketText);  // 80-100% времени
        if (ratio > 0.6) return "🔥 *ВИКТОРИНА - УСКОРЯЕМСЯ!*";           // 60-80% времени
        if (ratio > 0.3) return "⚡ *ВРЕМЯ НА ИСХОДЕ!*";                   // 30-60% времени
        if (ratio > 0.1) return "🚨 *КРИТИЧЕСКОЕ ВРЕМЯ!* 🚨";             // 10-30% времени
        return "💀 *ПОСЛЕДНИЕ СЕКУНДЫ!* 💀";                              // 0-10% времени
    }

    /**
     * ⏰ ОТОБРАЖЕНИЕ ВРЕМЕНИ С АНИМИРОВАННЫМИ ЭМОДЗИ
     * <p>
     * Показывает время в формате MM:SS с динамическими эмодзи
     * В критические моменты добавляет мигающий эффект
     *
     * @param timeLeft - оставшееся время в секундах
     * @return String - форматированное время с эмодзи
     */
    private String getTimeDisplay(int timeLeft) {
        // Получаем анимированное эмодзи в зависимости от времени
        String timeEmoji = getAnimatedTimeEmoji(timeLeft);


        /*
          Конвертируем секунды в минуты и секунды
          Примеры расчетов:
          127 секунд: 127 / 60 = 2 минуты, 127 % 60 = 7 секунд → 02:07
          65 секунд: 65 / 60 = 1 минута, 65 % 60 = 5 секунд → 01:05
          30 секунд: 30 / 60 = 0 минут, 30 % 60 = 30 секунд → 00:30
         */
        int minutes = timeLeft / 60;        // 127 секунд / 60 = 2 минуты // Целочисленное деление
        int seconds = timeLeft % 60;        // 127 секунд % 60 = 7 секунд (остаток)  // Остаток от деления


        /** Критический режим - мигающий эффект для последних 5 секунд
         * Что происходит:
         * Эмодзи ДО и ПОСЛЕ времени
         * Время в жирном шрифте (*Время: 00:03*)
         * Привлекает максимум внимания
         * Пример результата:
         * 🔥 *Время: 00:03* 🔥  /Четная секунда
         * ⏰ *Время: 00:02* ⏰  /Нечетная секунда
         * 🔥 *Время: 00:01* 🔥  /Четная секунда
         */
        if (timeLeft <= 5) {
            return String.format("%s *Время: %02d:%02d* %s",
                    timeEmoji, minutes, seconds, timeEmoji);
        }

        /** Обычный режим
         * Что означает %02d:
         * %d — целое число
         * 0 — заполнитель (ведущий символ)
         * 2 — минимальная ширина (2 символа)
         * Примеры:
         * 5 → 05 (добавляется ведущий ноль)
         * 12 → 12 (уже 2 символа)
         * 123 → 123 (больше 2 символов — остается как есть)
         *
         */
        return String.format("%s Время: %02d:%02d", timeEmoji, minutes, seconds);
    }

    /**
     * 🎭 АНИМИРОВАННЫЕ ЭМОДЗИ ДЛЯ ВРЕМЕНИ
     * <p>
     * Меняются в зависимости от оставшегося времени
     * Создают визуальный эффект ускоряющегося времени
     *
     * @param timeLeft - оставшееся время в секундах
     * @return String - эмодзи для текущего времени
     */
    private String getAnimatedTimeEmoji(int timeLeft) {
        // Последние 5 секунд - мигающий эффект
        if (timeLeft <= 5) {
            // Четные секунды - огонь, нечетные - часы
            return timeLeft % 2 == 0 ? "⏰" : "🔥";
        }

        // 6-15 секунд - критическое время
        if (timeLeft <= 15) {
            return "🔥";  // Огонь - время горит!
        }

        // 16-30 секунд - обычные часы
        return "🕐";
    }

    /**
     * 🎨 ЦВЕТНОЙ ПРОГРЕСС-БАР С ДИНАМИЧЕСКИМИ ЦВЕТАМИ
     * <p>
     * Создает визуальную полоску которая показывает оставшееся время
     * Цвета меняются от зеленого к красному по мере убывания времени
     *
     * @param timeLeft  - оставшееся время в секундах
     * @param totalTime - изначальное время в секундах
     * @return String - цветная полоска с процентами
     */
    private String getColoredProgressBar(int timeLeft, int totalTime) {
        // Вычисляем долю оставшегося времени (от 0.0 до 1.0)
        double ratio = (double) timeLeft / totalTime;
        // Количество сегментов в полоске (10 квадратиков)
        int segments = 10;

        // Сколько сегментов должно быть заполнено
        int filledSegments = (int) (ratio * segments);

        // Строим полоску символ за символом
        /**
         * Зачем StringBuilder:
         * Эффективнее чем String + String
         * Можем добавлять символы по одному
         * Создается один объект вместо 10 промежуточных строк
         */
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < segments; i++) {
            if (i < filledSegments) {
                // Заполненный сегмент - выбираем цвет по времени
                if (ratio > 0.6) bar.append("🟩");      // Зеленый - много времени (60-100%)
                else if (ratio > 0.3) bar.append("🟨"); // Желтый - средне (30-60%)
                else bar.append("🟧");                   // Оранжевый - мало времени (0-30%)
            } else {
                // Пустой сегмент - всегда красный
                bar.append("🟥");
            }
        }

        // Добавляем процентное отображение
        int percentage = (int) (ratio * 100);
        return String.format("%s  [%d%%]", bar.toString(), percentage);
    }

    /**
     * 💫 МОТИВАЦИОННЫЕ ТЕКСТЫ В ЗАВИСИМОСТИ ОТ ВРЕМЕНИ
     * <p>
     * Подбадривает или создает напряжение в зависимости от ситуации
     * Меняется от спокойных советов до паники последних секунд
     *
     * @param timeLeft  - оставшееся время в секундах
     * @param totalTime - изначальное время в секундах
     * @return String - мотивационное сообщение с эмодзи
     */
    private String getMotivationalText(int timeLeft, int totalTime) {
        // Вычисляем долю оставшегося времени
        double ratio = (double) timeLeft / totalTime;

        // Подбираем мотивашку в зависимости от критичности ситуации
        if (ratio > 0.8) return "⚡ Быстрый ответ = больше наград!";
        if (ratio > 0.6) return "🔥 Торопись, бонусы тают как мороженое!";
        if (ratio > 0.3) return "⏰ Время на исходе, думай быстрее!";
        if (ratio > 0.1) return "🚨 *ПОСЛЕДНИЙ ШАНС!* 🚨";
        return "💀 *ТИК-ТАК, ТИК-ТАК!* 💀";
        /**
         * Секунда 30: ⚡ Быстрый ответ = больше наград!
         * Секунда 20: 🔥 Торопись, бонусы тают как мороженое!
         * Секунда 12: ⏰ Время на исходе, думай быстрее!
         * Секунда 5:  🚨 *ПОСЛЕДНИЙ ШАНС!* 🚨
         * Секунда 2:  💀 *ТИК-ТАК, ТИК-ТАК!* 💀
         */
    }

    /**
     * 📊 ФОРМАТИРОВАНИЕ ВОПРОСА С ВАРИАНТАМИ ОТВЕТОВ
     * <p>
     * Красиво оформляет блок с вопросом и вариантами для викторины
     * Добавляет эмодзи и правильное форматирование
     *
     * @param session - сессия викторины с вопросом и вариантами
     * @return String - отформатированный блок вопрос + варианты
     */
    private String formatQuestionWithOptions(QuizSession session) {
        // Получаем данные из сессии
        String question = session.getQuestion(); // "☕ Сколько кофе нужно программисту?"
        List<String> options = session.getOptions(); //["🅰️ Одну чашку", "🅱️ Три чашки", ...]

        // Начинаем строить блок с вопросом
        StringBuilder questionBlock = new StringBuilder();

        // Добавляем вопрос с эмодзи
        questionBlock.append("❓ ").append(question).append("\n");

        // Добавляем каждый вариант ответа
        for (String option : options) {
            questionBlock.append("\n").append(option);
        }

        return questionBlock.toString();
    }

    /**
     * 🎮 СОЗДАНИЕ КЛАВИАТУРЫ С КНОПКАМИ ДЛЯ ОТВЕТОВ
     * <p>
     * Превращает варианты ответов в красивые инлайн-кнопки
     * Каждая кнопка содержит callbackData для обработки нажатий
     *
     * @param options - список вариантов ответов ["🅰️ Вариант A", "🅱️ Вариант B"]
     * @return InlineKeyboardMarkup - готовая клавиатура для Telegram
     */
    private InlineKeyboardMarkup createQuizKeyboard(List<String> options) {
        log.debug("EpicQuizTimer: Создание клавиатуры с {} вариантами", options.size());

        // Создаем объект клавиатуры
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        // Список строк кнопок (каждая строка = ряд кнопок)
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Создаем кнопку для каждого варианта ответа
        for (int i = 0; i < options.size(); i++) {
            String optionText = options.get(i);              // Текст на кнопке: "🅰️ Вариант A"
            String callbackData = "quiz_answer_" + i;        // Данные для обработки: "quiz_answer_0"

            // Создаем кнопку
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(optionText);
            button.setCallbackData(callbackData);

            // Создаем ряд с одной кнопкой (каждая кнопка в отдельной строке)
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            rows.add(row);

            log.debug("EpicQuizTimer: Создана кнопка '{}' с callbackData '{}'", optionText, callbackData);
        }

        // Устанавливаем строки кнопок в клавиатуру
        keyboard.setKeyboard(rows);

        log.debug("EpicQuizTimer: Клавиатура создана с {} строками кнопок", rows.size());
        return keyboard;
    }

    /**
     * ⏰ ЭПИЧЕСКИЙ ТАЙМЕР СО ВСЕМИ ЭФФЕКТАМИ
     * <p>
     * Это главный метод который создает "живое" сообщение:
     * - Каждую секунду обновляет текст сообщения
     * - Меняет заголовки, время, прогресс-бар, мотивашки
     * - Отправляет спецэффекты в критические моменты
     * - Обрабатывает тайм-аут когда время заканчивается
     *
     * @param bot     - объект бота для отправки/изменения сообщений
     * @param chatId  - ID чата пользователя
     * @param session - сессия викторины с данными
     */
    private void startEpicTimer(TelegramLongPollingBot bot, Long chatId, QuizSession session) {
        log.info("EpicQuizTimer: ⏰ Запуск эпического таймера для chatId={}", chatId);

        // Запускаем задачу которая выполняется каждую секунду
        session.setTimerTask(scheduler.scheduleAtFixedRate(() -> {
            try {
                // 1. Уменьшаем время на 1 секунду
                session.decrementTime();
                int timeLeft = session.getTimeLeft();

                log.debug("EpicQuizTimer: Тик-так! Осталось {} секунд для chatId={}", timeLeft, chatId);

                // 2. Обновляем основное сообщение каждую секунду
                updateQuizMessage(bot, chatId, session);

                // 3. 🎭 СПЕЦЭФФЕКТЫ В КРИТИЧЕСКИЕ МОМЕНТЫ
                handleSpecialMoments(bot, chatId, timeLeft);

                // 4. ⏰ ВРЕМЯ ВЫШЛО!
                if (timeLeft <= 0) {
                    log.warn("EpicQuizTimer: 💀 Время вышло для chatId={}", chatId);
                    handleEpicTimeOut(bot, chatId, session);

                    // Останавливаем таймер и очищаем сессию
                    session.getTimerTask().cancel(false);
                    activeSessions.remove(chatId);

                    log.info("EpicQuizTimer: ✅ Таймер остановлен и сессия очищена для chatId={}", chatId);
                }

            } catch (Exception e) {
                log.error("EpicQuizTimer: ❌ Ошибка в эпическом таймере для chatId={}: {}", chatId, e.getMessage(), e);
                // Если произошла ошибка - останавливаем таймер
                session.getTimerTask().cancel(false);
                activeSessions.remove(chatId);
            }
        }, 1, 1, TimeUnit.SECONDS));
        //  ^  ^  ^^^^^^^^^^^^^^^
        //  |  |  Единицы времени (секунды)
        //  |  Период повторения (каждую секунду)
        //  Начальная задержка (через 1 секунду)
    }

    /**
     * 🔄 ОБНОВЛЕНИЕ СООБЩЕНИЯ В РЕАЛЬНОМ ВРЕМЕНИ
     * <p>
     * Этот метод создает эффект "живого" сообщения:
     * - Берет существующее сообщение по messageId
     * - Генерирует новый текст с обновленным временем
     * - Отправляет EditMessageText в Telegram
     * - Сообщение изменяется прямо на экране пользователя!
     *
     * @param bot     - объект бота для отправки изменений
     * @param chatId  - ID чата пользователя
     * @param session - сессия с данными викторины и messageId
     */
    private void updateQuizMessage(TelegramLongPollingBot bot, Long chatId, QuizSession session) {
        try {
            // Создаем объект для изменения существующего сообщения
            EditMessageText editMessage = new EditMessageText();

            // Указываем ГДЕ изменять (чат и конкретное сообщение)
            editMessage.setChatId(chatId);
            editMessage.setMessageId(session.getMessageId());

            // Указываем ЧТО изменять (новый текст и клавиатура)
            editMessage.setParseMode("Markdown");
            editMessage.setText(buildEpicQuizText(session, null));  // Генерируем новый текст с обновленным временем
            editMessage.setReplyMarkup(createQuizKeyboard(session.getOptions()));

            // Отправляем изменение в Telegram
            bot.execute(editMessage);

            log.debug("EpicQuizTimer: ✅ Сообщение обновлено для chatId={}, осталось {} сек",
                    chatId, session.getTimeLeft());

        } catch (TelegramApiException e) {
            log.error("EpicQuizTimer: ❌ Ошибка обновления сообщения для chatId={}: {}",
                    chatId, e.getMessage());

            // Если не можем обновить сообщение - останавливаем таймер
            // (возможно пользователь удалил сообщение или заблокировал бота)
            if (session.getTimerTask() != null) {
                session.getTimerTask().cancel(false);
                activeSessions.remove(chatId);
                log.warn("EpicQuizTimer: ⚠️ Таймер остановлен из-за ошибки обновления для chatId={}", chatId);
            }
        }
    }

    /**
     * 🎭 ОБРАБАТЫВАЕТ КРИТИЧЕСКИЕ МОМЕНТЫ ТАЙМЕРА И ДОБАВЛЯЕТ ПРЕДУПРЕЖДЕНИЯ В ВИКТОРИНУ
     *
     * ЗАЧЕМ ЭТОТ МЕТОД:
     * - Вызывается каждую секунду из основного таймера 
     * - Проверяет, не пора ли показать предупреждение пользователю
     * - Вместо отправки отдельных сообщений, ИЗМЕНЯЕТ основное сообщение викторины
     * - Так пользователь видит предупреждения прямо в викторине, а не где-то внизу
     *
     * КАК ЭТО РАБОТАЕТ:
     * - Получаем активную сессию викторины пользователя
     * - Смотрим сколько времени осталось (timeLeft)
     * - Если время критическое - добавляем предупреждение в основной текст викторины
     * - Используем EditMessageText чтобы изменить существующее сообщение
     *
     * ПОЧЕМУ ИМЕННО ЭТИ ВРЕМЕННЫЕ ТОЧКИ:
     * - 10 сек: еще можно спокойно выбрать, но пора торопиться
     * - 5 сек: критично, нужна паника чтобы пользователь не тупил
     * - 3-1 сек: мигающие эффекты для максимального напряжения
     *
     * @param bot - объект бота для редактирования сообщений в Telegram  
     * @param chatId - ID чата пользователя (где викторина)
     * @param timeLeft - сколько секунд осталось до конца (передается из таймера)
     */
    private void handleSpecialMoments(TelegramLongPollingBot bot, Long chatId, int timeLeft) {
        try {
            // ПОЛУЧАЕМ ДАННЫЕ ВИКТОРИНЫ ЭТОГО ПОЛЬЗОВАТЕЛЯ
            // activeSessions - это Map<Long, QuizSession> где хранятся все активные викторины
            // Ключ = chatId, значение = данные викторины (вопрос, время, messageId и т.д.)
            QuizSession session = activeSessions.get(chatId);
            if (session == null) {
                // Если сессии нет - значит викторина уже завершена или была ошибка
                // Просто выходим, нечего обрабатывать
                return;
            }

            // ПРОВЕРЯЕМ КРИТИЧЕСКИЕ ВРЕМЕННЫЕ ТОЧКИ И ДОБАВЛЯЕМ ПРЕДУПРЕЖДЕНИЯ

                        // 🚨 ПЕРВОЕ ПРЕДУПРЕЖДЕНИЕ НА 10 СЕКУНДАХ
            if (timeLeft == 10) {
                log.info("EpicQuizTimer: 🚨 Добавление предупреждения на 10 сек для chatId={}", chatId);
                updateQuizMessageWithWarning(bot, chatId, session, "🚨 10 СЕКУНД! ТОРОПИСЬ! 🚨");
            }

            // 💀 КРИТИЧЕСКОЕ ПРЕДУПРЕЖДЕНИЕ НА 5 СЕКУНДАХ  
            else if (timeLeft == 5) {
                log.info("EpicQuizTimer: 💀 Добавление паники на 5 сек для chatId={}", chatId);
                updateQuizMessageWithWarning(bot, chatId, session, "💀 5 СЕКУНД! КРИТИЧНО! 💀");
            }

            // ⏰ ТИК-ТАК НА ПОСЛЕДНИХ 3 СЕКУНДАХ (ПО ОДНОЙ ФРАЗЕ НА СЕКУНДУ)
            else if (timeLeft == 3) {
                log.info("EpicQuizTimer: ⏰ Тик-так на 3 сек для chatId={}", chatId);
                updateQuizMessageWithWarning(bot, chatId, session, "⏰ ТИК-ТАК... 3 ⏰");
            }
            else if (timeLeft == 2) {
                log.info("EpicQuizTimer: ⏰ Тик-так на 2 сек для chatId={}", chatId);
                updateQuizMessageWithWarning(bot, chatId, session, "🔥 ТИК-ТАК... 2 🔥");
            }
            else if (timeLeft == 1) {
                log.info("EpicQuizTimer: ⏰ Тик-так на 1 сек для chatId={}", chatId);
                updateQuizMessageWithWarning(bot, chatId, session, "💥 ПОСЛЕДНИЙ ШАНС! 💥");
            }

        } catch (Exception e) {
            // ЛЮБЫЕ ОШИБКИ В СПЕЦЭФФЕКТАХ НЕ ДОЛЖНЫ ЛОМАТЬ ОСНОВНОЙ ТАЙМЕР
            log.error("EpicQuizTimer: ❌ Ошибка спецэффектов для chatId={}: {}", chatId, e.getMessage());
            // Продолжаем работу таймера даже если предупреждения не работают
        }
    }



    /**
     * 💀 ЭПИЧНЫЙ ТАЙМ-АУТ КОГДА ВРЕМЯ ЗАКОНЧИЛОСЬ
     * <p>
     * Обрабатывает ситуацию когда пользователь не успел ответить:
     * - Изменяет основное сообщение на финальное
     * - Показывает правильный ответ
     * - Применяет штрафы к персонажу
     * - Отправляет эпичные комментарии от Итераториуса
     * - Может добавить немного юмора для компенсации
     *
     * @param bot     - объект бота для отправки сообщений
     * @param chatId  - ID чата пользователя
     * @param session - сессия викторины с данными
     */
    private void handleEpicTimeOut(TelegramLongPollingBot bot, Long chatId, QuizSession session) {
        log.info("EpicQuizTimer: 💀 Обработка эпичного тайм-аута для chatId={}", chatId);

        try {
            // 1. ИЗМЕНЯЕМ ОСНОВНОЕ СООБЩЕНИЕ НА ФИНАЛЬНОЕ
            updateMessageToTimeOut(bot, chatId, session);

            // 2. ПРИМЕНЯЕМ ШТРАФЫ И БОНУСЫ К ПЕРСОНАЖУ
            applyTimeOutPenalties(chatId);

            // 3. ОТПРАВЛЯЕМ ЭПИЧНЫЕ КОММЕНТАРИИ ОТ ИТЕРАТОРИУСА
            sendIteratoriusComments(bot, chatId);

            log.info("EpicQuizTimer: ✅ Тайм-аут обработан для chatId={}", chatId);

        } catch (Exception e) {
            log.error("EpicQuizTimer: ❌ Ошибка обработки тайм-аута для chatId={}: {}", chatId, e.getMessage());
        }
    }

    /**
     * 🔄 ИЗМЕНЕНИЕ ОСНОВНОГО СООБЩЕНИЯ НА ФИНАЛЬНОЕ
     * <p>
     * Меняет живое сообщение с таймером на статичное сообщение с результатом
     */
    private void updateMessageToTimeOut(TelegramLongPollingBot bot, Long chatId, QuizSession session) {
        try {
            EditMessageText timeOutMessage = new EditMessageText();
            timeOutMessage.setChatId(chatId);
            timeOutMessage.setMessageId(session.getMessageId());
            timeOutMessage.setParseMode("Markdown");

            // Формируем финальный текст с результатом
            String finalText = String.format(
                    "💀 *ВРЕМЯ ВЫШЛО!* 💀\n\n" +

                            "⏰ Время: 00:00\n" +
                            "🟥🟥🟥🟥🟥🟥🟥🟥🟥🟥  [0%%]\n\n" +

                            "❓ %s\n\n" +

                            "❌ *Правильный ответ был:*\n" +
                            "✅ %s\n\n" +

                            "🧠 *Итераториус*: \"Медленно думаешь, как Internet Explorer!\n" +
                            "В следующий раз кофе пей перед викториной, а не после!\"\n\n" +

                            "💸 *Изменения статов:*\n" +
                            "  -350 💲 к Деньгам (штраф за медлительность)\n" +
                            "  -150 ⭐️ к Опыту (не успел ответить вовремя)",

                    session.getQuestion(),      // Вставляем вопрос
                    session.getCorrectAnswer()  // Вставляем правильный ответ
            );

            timeOutMessage.setText(finalText);
            // Убираем кнопки - викторина закончена
            timeOutMessage.setReplyMarkup(null);

            bot.execute(timeOutMessage);
            log.debug("EpicQuizTimer: ✅ Основное сообщение изменено на тайм-аут для chatId={}", chatId);

        } catch (TelegramApiException e) {
            log.error("EpicQuizTimer: ❌ Ошибка изменения сообщения на тайм-аут для chatId={}: {}",
                    chatId, e.getMessage());
        }
    }

    /**
     * 💸 ПРИМЕНЕНИЕ ШТРАФОВ ЗА ТАЙМ-АУТ
     * <p>
     * Обычная викторина: -350 денег, -150 очков опыта
     * Экспертная викторина: -500 денег, -250 очков опыта (пользователь не знает суммы)
     */
    private void applyTimeOutPenalties(Long chatId) {
        try {
            // ПОЛУЧАЕМ СЕССИЮ ЧТОБЫ ПОНЯТЬ ЭКСПЕРТНАЯ ЭТО ВИКТОРИНА ИЛИ НЕТ
            QuizSession session = activeSessions.get(chatId);
            Map<String, Integer> penalties;
            
            if (session != null && session.isJackpot()) {
                // 💀 ЭКСПЕРТНАЯ ТАЙМ-АУТ: -500 денег, -250 очков опыта (но пользователь не знает сколько!)
                penalties = Map.of(
                        "currency", -500,
                        "achievement_points", -250
                );
                log.info("EpicQuizTimer: 💀 Применяем ЭКСПЕРТНЫЕ штрафы за тайм-аут для chatId={}", chatId);
            } else {
                // ❌ ОБЫЧНЫЙ ТАЙМ-АУТ: -350 денег, -150 очков опыта
                penalties = Map.of(
                        "currency", -350,
                        "achievement_points", -150
                );
                log.info("EpicQuizTimer: ❌ Применяем обычные штрафы за тайм-аут для chatId={}", chatId);
            }

            // Применяем через наш StatService
            statService.applyStatChanges(chatId, penalties);

            log.info("EpicQuizTimer: ✅ Штрафы за тайм-аут применены для chatId={}: {}", chatId, penalties);

        } catch (Exception e) {
            log.error("EpicQuizTimer: ❌ Ошибка применения штрафов для chatId={}: {}", chatId, e.getMessage());
        }
    }

    /**
     * 🎭 ОТПРАВКА ЭПИЧНЫХ КОММЕНТАРИЕВ ОТ ИТЕРАТОРИУСА
     * <p>
     * Отправляет дополнительное сообщение с издевательствами и советами
     * Через несколько секунд после основного тайм-аута
     */
    private void sendIteratoriusComments(TelegramLongPollingBot bot, Long chatId) {
        // Отправляем комментарий через 3 секунды для драматического эффекта
        scheduler.schedule(() -> {
            try {
                SendMessage epicRoast = new SendMessage();
                epicRoast.setChatId(chatId);
                epicRoast.setParseMode("Markdown");
                epicRoast.setText(
                        "🎭 *Итераториус продолжает издеваться:*\n\n" +
                                "\"Знаешь, что работает быстрее твоих ответов?\n" +
                                "• Dial-up интернет 📞\n" +
                                "• Компиляция Maven проекта ⏳\n" +
                                "• Моя бабушка с печатной машинкой 👵\n" +
                                "• Обновление Windows до 100% 🐌\n\n" +

                                "Но не расстраивайся! В программировании важна не скорость,\n" +
                                "а правильность... хотя в викторинах важна именно скорость! 😄\n\n" +

                                "💡 *Совет на будущее:*\n" +
                                "Читай варианты ответов ДО того, как время начнет тикать!\""
                );

                bot.execute(epicRoast);
                log.debug("EpicQuizTimer: ✅ Эпичные комментарии отправлены для chatId={}", chatId);

            } catch (Exception e) {
                log.error("EpicQuizTimer: ❌ Ошибка отправки комментариев для chatId={}: {}", chatId, e.getMessage());
            }
        }, 3, TimeUnit.SECONDS);
    }

    /**
     * 🎮 ОБРАБОТКА ОТВЕТА ПОЛЬЗОВАТЕЛЯ НА ВИКТОРИНУ
     * 
     * Этот метод вызывается когда пользователь нажимает кнопку с ответом.
     * Он останавливает таймер, проверяет правильность и применяет награды/штрафы.
     *
     * @param bot - объект бота для отправки сообщений
     * @param chatId - ID чата пользователя  
     * @param answerIndex - индекс выбранного ответа (0, 1, 2, 3...)
     * @param messageId - ID сообщения с викториной для редактирования
     */
    public void handleQuizAnswer(TelegramLongPollingBot bot, Long chatId, int answerIndex, Integer messageId) {
        log.info("EpicQuizTimer: 🎮 Обработка ответа пользователя answerIndex={} для chatId={}", answerIndex, chatId);
        
        // 1. Получаем активную сессию викторины
        QuizSession session = activeSessions.get(chatId);
        if (session == null) {
            log.warn("EpicQuizTimer: ❌ Сессия викторины не найдена для chatId={}", chatId);
            return;
        }
        
        // 2. Останавливаем таймер если он активен
        if (session.getTimerTask() != null && !session.getTimerTask().isCancelled()) {
            session.getTimerTask().cancel(true);
            log.debug("EpicQuizTimer: ⏹️ Таймер остановлен для chatId={}", chatId);
        }
        
        // 3. Удаляем сессию из активных (викторина завершена)
        activeSessions.remove(chatId);
        
        // 4. Проверяем правильность ответа и определяем награды
        String selectedAnswer = session.getOptions().get(answerIndex);
        boolean isCorrect = selectedAnswer.equals(session.getCorrectAnswer());
        int timeRemaining = session.getTimeLeft();
        
        log.info("EpicQuizTimer: Выбранный ответ: '{}', правильный: '{}', остаток времени: {}с", 
                 selectedAnswer, session.getCorrectAnswer(), timeRemaining);
        
        // 5. Применяем награды/штрафы через StatService (УЧИТЫВАЕМ СКОРОСТЬ ОТВЕТА!)
        // ⚡ ЧЕМ БЫСТРЕЕ ОТВЕТИЛ (больше timeRemaining) - ТЕМ БОЛЬШЕ НАГРАДА!
        // 🐌 ЧЕМ МЕДЛЕННЕЕ ОТВЕТИЛ (меньше timeRemaining) - ТЕМ МЕНЬШЕ НАГРАДА!
        int currencyReward = 0;
        int achievementReward = 0;
        
        if (isCorrect) {
            if (session.isJackpot()) {
                // 🏆 ЭКСПЕРТНАЯ ВИКТОРИНА (билет 10): награды зависят от скорости
                if (timeRemaining > 20) {
                    // ⚡ Молниеносный ответ: максимальная награда
                    currencyReward = 800;
                    achievementReward = 500;
                    log.info("EpicQuizTimer: 🏆⚡ Молниеносный ответ в экспертной викторине! chatId={}, timeRemaining={}", chatId, timeRemaining);
                } else if (timeRemaining > 10) {
                    // 👍 Быстрый ответ: средняя награда
                    currencyReward = 600;
                    achievementReward = 400;
                    log.info("EpicQuizTimer: 🏆👍 Быстрый ответ в экспертной викторине! chatId={}, timeRemaining={}", chatId, timeRemaining);
                } else {
                    // 😅 Медленный ответ: минимальная награда
                    currencyReward = 400;
                    achievementReward = 300;
                    log.info("EpicQuizTimer: 🏆😅 Медленный ответ в экспертной викторине! chatId={}, timeRemaining={}", chatId, timeRemaining);
                }
            } else {
                // ✅ ОБЫЧНАЯ ВИКТОРИНА (билет 9): награды зависят от скорости
                if (timeRemaining > 10) {
                    // ⚡ Ответил раньше последних 10 секунд: максимальная награда
                    currencyReward = 600;
                    achievementReward = 400;
                    log.info("EpicQuizTimer: ✅⚡ Быстрый ответ в обычной викторине! chatId={}, timeRemaining={}", chatId, timeRemaining);
                } else {
                    // 😅 Ответил в последние 10 секунд: минимальная награда
                    currencyReward = 200;
                    achievementReward = 150;
                    log.info("EpicQuizTimer: ✅😅 Медленный ответ в обычной викторине! chatId={}, timeRemaining={}", chatId, timeRemaining);
                }
            }
            
            statService.applyStatChanges(chatId, Map.of(
                    "currency", currencyReward,
                    "achievement_points", achievementReward
            ));
            log.info("EpicQuizTimer: ✅ Применены награды за правильный ответ: +{}💲 +{}⭐ для chatId={}", currencyReward, achievementReward, chatId);
        } else {
            // ❌ НЕПРАВИЛЬНЫЙ ОТВЕТ: штрафы фиксированные (не зависят от скорости)
            if (session.isJackpot()) {
                // 💀 ЭКСПЕРТНАЯ НЕПРАВИЛЬНЫЙ ОТВЕТ: -500 денег, -250 очков опыта
                statService.applyStatChanges(chatId, Map.of(
                        "currency", -500,
                        "achievement_points", -250
                ));
                log.info("EpicQuizTimer: 💀 Применены ЭКСПЕРТНЫЕ штрафы за неправильный ответ для chatId={}", chatId);
            } else {
                // ❌ ОБЫЧНЫЙ НЕПРАВИЛЬНЫЙ ОТВЕТ: -350 денег, -150 очков опыта  
                statService.applyStatChanges(chatId, Map.of(
                        "currency", -350,
                        "achievement_points", -150
                ));
                log.info("EpicQuizTimer: ❌ Применены штрафы за неправильный ответ для chatId={}", chatId);
            }
        }
        
        // 6. Обновляем сообщение с результатом викторины (передаём реальные награды!)
        updateMessageWithResult(bot, chatId, messageId, session, selectedAnswer, isCorrect, timeRemaining, currencyReward, achievementReward);
        
        // 7. Отправляем комментарий Итераториуса
        sendResultComments(bot, chatId, isCorrect, timeRemaining, session.isJackpot());
        
        log.info("EpicQuizTimer: ✅ Викторина завершена для chatId={}", chatId);
    }
    
    /**
     * 📝 ОБНОВЛЯЕТ СООБЩЕНИЕ С РЕЗУЛЬТАТОМ ВИКТОРИНЫ
     * 
     * Показывает реальные награды/штрафы, которые зависят от скорости ответа.
     * Чем быстрее ответил - тем больше награда, чем медленнее - тем меньше.
     */
    private void updateMessageWithResult(TelegramLongPollingBot bot, Long chatId, Integer messageId, 
                                       QuizSession session, String selectedAnswer, boolean isCorrect, 
                                       int timeRemaining, int currencyReward, int achievementReward) {
        try {
            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);
            editMessage.setParseMode("Markdown");
            
            // Формируем текст результата
            String resultEmoji = isCorrect ? "✅" : "❌";
            String resultText = isCorrect ? "*ПРАВИЛЬНО!*" : "*НЕПРАВИЛЬНО!*";
            
            // Формируем строку с наградами/штрафами (реальные значения из логики наград!)
            String rewardText;
            if (isCorrect) {
                rewardText = String.format("+%d💲 +%d⭐", currencyReward, achievementReward);
            } else {
                // Штрафы фиксированные (не зависят от скорости)
                if (session.isJackpot()) {
                    rewardText = "-500💲 -250⭐";
                } else {
                    rewardText = "-350💲 -150⭐";
                }
            }
            
            String resultMessage = String.format(
                "🎯 *ВИКТОРИНА ЗАВЕРШЕНА* %s\n\n" +
                "*Вопрос:* %s\n\n" +
                "*Твой ответ:* %s\n" +
                "*Правильный ответ:* %s\n\n" +
                "⏱️ *Время ответа:* %d секунд\n" +
                "💰 *Изменение статов:* %s\n\n" +
                "%s",
                resultEmoji,
                session.getQuestion(),
                selectedAnswer,
                session.getCorrectAnswer(),
                session.getTotalTime() - timeRemaining,
                rewardText,
                resultText
            );
            
            editMessage.setText(resultMessage);
            bot.execute(editMessage);
            
        } catch (TelegramApiException e) {
            log.error("EpicQuizTimer: ❌ Ошибка обновления сообщения с результатом для chatId={}: {}", chatId, e.getMessage());
        }
    }
    
    /**
     * 💬 ОТПРАВЛЯЕТ КОММЕНТАРИИ ИТЕРАТОРИУСА О РЕЗУЛЬТАТЕ ВИКТОРИНЫ
     * 
     * Итераториус комментирует результат викторины в зависимости от:
     * - Правильности ответа (правильно/неправильно)
     * - Скорости ответа (быстро/средне/медленно)
     * - Типа викторины (обычная/экспертная)
     * 
     * Комментарии в стиле проекта: с сарказмом, мотивацией и черным юмором.
     */
    private void sendResultComments(TelegramLongPollingBot bot, Long chatId, boolean isCorrect, int timeRemaining, boolean isJackpot) {
        try {
            String comment;
            
            if (isCorrect) {
                // ✅ ПРАВИЛЬНЫЙ ОТВЕТ: комментарий зависит от скорости
                if (timeRemaining > 10) {
                    // ⚡ Быстрый ответ (раньше последних 10 секунд)
                    if (isJackpot) {
                        // Экспертная викторина: различаем очень быстрый (>20) и быстрый (10-20)
                        if (timeRemaining > 20) {
                            comment = "*Итераториус*\n\n" +
                                    "_Черт, ты ответил быстрее чем компилятор Java обрабатывает аннотации._\n\n" +
                                    "Экспертный вопрос, молниеносный ответ — это уже не удача, это мастерство.\n" +
                                    "Такие скорости я видел только у тех, кто действительно понимает ArrayList изнутри.\n\n" +
                                    "💡 *Запомни:* В проде такие знания спасают от дедлайнов.";
                        } else {
                            comment = "*Итераториус*\n\n" +
                                    "_Неплохо, малец. Экспертный вопрос, разумное время._\n\n" +
                                    "Видно, что ты не просто угадываешь — ты действительно понимаешь структуру данных.\n" +
                                    "Такие знания в проде ценятся больше, чем умение гуглить Stack Overflow.\n\n" +
                                    "💡 *Мысль дня:* Понимание > скорость, но скорость + понимание = победа.";
                        }
                    } else {
                        // ✅ Обычная викторина: ответил раньше последних 10 секунд
                        comment = "*Итераториус*\n\n" +
                                "_Чертовски быстро! Ты знаешь толк в ArrayList._\n\n" +
                                "Такие скорости только в дата-центрах видел. Видно, что не первый день с кодом работаешь.\n\n" +
                                "💡 *Совет:* Держи этот темп — и следующий раунд пройдёшь на ура.";
                    }
                } else {
                    // 😅 Медленный ответ (в последние 10 секунд)
                    if (isJackpot) {
                        comment = "*Итераториус*\n\n" +
                                "_Фуух! Еле успел, но правильно — это главное._\n\n" +
                                "Экспертный вопрос требует времени, это нормально. Главное — ты справился.\n" +
                                "В проде такие вопросы решаются не за секунды, а за минуты размышлений.\n\n" +
                                "💡 *Совет:* В следующий раз читай вопрос внимательнее с самого начала — времени будет больше.";
                    } else {
                        // ✅ Обычная викторина: ответил в последние 10 секунд
                        comment = "*Итераториус*\n\n" +
                                "_Фуух! Еле успел, но правильно!_\n\n" +
                                "В следующий раз думай быстрее — время это деньги, а в викторинах ещё и награды.\n" +
                                "Но главное — ответ правильный. Это уже половина успеха.\n\n" +
                                "💡 *Совет:* Тренируйся на скорость — чем быстрее ответишь (раньше последних 10 секунд), тем больше получишь.";
                    }
                }
            } else {
                // ❌ НЕПРАВИЛЬНЫЙ ОТВЕТ
                if (isJackpot) {
                    comment = "*Итераториус*\n\n" +
                            "_Эх, малец... Экспертный вопрос требует экспертных знаний._\n\n" +
                            "Не расстраивайся — такие вопросы решают не все. Главное — ты попробовал.\n" +
                            "Потренируйся ещё, почитай про внутреннее устройство ArrayList, и в следующий раз точно справишься.\n\n" +
                            "💡 *Совет:* В проде такие вопросы задают на собеседованиях — готовься заранее.";
                } else {
                    comment = "*Итераториус*\n\n" +
                            "_Эх, молодежь... ArrayList.add(0, element) сдвигает ВСЕ элементы вправо._\n\n" +
                            "Это O(n) операция, запомни раз и навсегда. Не путай с add(element) — там O(1).\n" +
                            "Потренируйся ещё, а то в проде такие вопросы задают, и там ошибка стоит дороже.\n\n" +
                            "💡 *Совет:* Изучи внутреннее устройство ArrayList — тогда такие вопросы не вызовут проблем.";
                }
            }
            
            SendMessage commentMessage = new SendMessage();
            commentMessage.setChatId(chatId);
            commentMessage.setParseMode("Markdown");
            commentMessage.setText(comment);
            
            bot.execute(commentMessage);
            log.info("EpicQuizTimer: ✅ Комментарий Итераториуса отправлен для chatId={}, правильный={}, время={}с", 
                    chatId, isCorrect, timeRemaining);
            
        } catch (TelegramApiException e) {
            log.error("EpicQuizTimer: ❌ Ошибка отправки комментария Итераториуса для chatId={}: {}", chatId, e.getMessage());
        }
    }

    /**
     * 🔄 ОБНОВЛЯЕТ ОСНОВНОЕ СООБЩЕНИЕ ВИКТОРИНЫ ДОБАВЛЯЯ ПРЕДУПРЕЖДЕНИЕ
     * 
     * ЗАЧЕМ ЭТОТ МЕТОД:
     * - Вместо отправки отдельных сообщений с предупреждениями
     * - ИЗМЕНЯЕТ существующее сообщение викторины добавляя предупреждение в конец
     * - Пользователь видит предупреждения прямо в викторине, а не где-то внизу
     * - Все остается в одном месте: вопрос + варианты + время + предупреждение
     * 
     * КАК ЭТО РАБОТАЕТ:
     * 1. Создаем объект EditMessageText (для изменения существующего сообщения)
     * 2. Указываем какое сообщение менять (chatId + messageId из сессии)
     * 3. Генерируем новый текст через buildEpicQuizText() передав предупреждение
     * 4. Отправляем изменение через bot.execute()
     * 5. Telegram заменяет содержимое сообщения на новое
     * 
     * ПОЧЕМУ НЕ ОТДЕЛЬНЫЕ СООБЩЕНИЯ:
     * - Отдельные сообщения появляются внизу чата
     * - Пользователь может их не заметить пока думает над викториной  
     * - В телеграме много сообщений, предупреждения "тонут"
     * - А изменение основного сообщения - пользователь точно видит
     * 
     * @param bot - объект бота для вызова Telegram API
     * @param chatId - ID чата (куда отправлять изменения) 
     * @param session - данные викторины (нужен messageId и вся остальная инфа)
     * @param warningText - текст предупреждения который добавится в конец викторины
     */
    private void updateQuizMessageWithWarning(TelegramLongPollingBot bot, Long chatId, QuizSession session, String warningText) {
        try {
            // СОЗДАЕМ ОБЪЕКТ ДЛЯ ИЗМЕНЕНИЯ СУЩЕСТВУЮЩЕГО СООБЩЕНИЯ
            EditMessageText editMessage = new EditMessageText();
            
            // УКАЗЫВАЕМ КАКОЕ СООБЩЕНИЕ ИЗМЕНЯТЬ
            editMessage.setChatId(chatId);                    // В каком чате
            editMessage.setMessageId(session.getMessageId()); // Какое сообщение (ID сохранен в сессии)
            editMessage.setParseMode("Markdown");             // Поддержка **жирного** и *курсива*
            
            // ГЕНЕРИРУЕМ НОВЫЙ ТЕКСТ СООБЩЕНИЯ С ПРЕДУПРЕЖДЕНИЕМ
            // buildEpicQuizText() создаст полный текст викторины:
            // - Заголовок с эмодзи
            // - Время в формате "⏰ Время: 00:05"  
            // - Цветной прогресс-бар
            // - Вопрос и варианты ответов
            // - Мотивационный текст
            // - + НАШЕ ПРЕДУПРЕЖДЕНИЕ В КОНЦЕ!
            editMessage.setText(buildEpicQuizText(session, warningText));
            
            // КНОПКИ ОТВЕТОВ ОСТАЮТСЯ ТЕМИ ЖЕ (НЕ ТРОГАЕМ ИХ)
            editMessage.setReplyMarkup(createQuizKeyboard(session.getOptions()));
            
            // ОТПРАВЛЯЕМ ИЗМЕНЕНИЕ В TELEGRAM
            // Telegram API заменит содержимое сообщения на новое
            bot.execute(editMessage);
            
            log.debug("EpicQuizTimer: ✅ Сообщение обновлено с предупреждением для chatId={}", chatId);
            
        } catch (TelegramApiException e) {
            // ЕСЛИ НЕ СМОГЛИ ИЗМЕНИТЬ СООБЩЕНИЕ (пользователь удалил, заблокировал бота)
            log.error("EpicQuizTimer: ❌ Ошибка обновления сообщения с предупреждением для chatId={}: {}", chatId, e.getMessage());
            // Продолжаем работу, предупреждения не критичны для викторины
        }
    }

    /**
     * 🏆 ЗАПУСКАЕТ ЭКСПЕРТНУЮ ВИКТОРИНУ С ЭПИЧЕСКИМ ТАЙМЕРОМ
     * 
     * ОТЛИЧИЯ ОТ ОБЫЧНОЙ ВИКТОРИНЫ:
     * - Используется для билета 10 (экспертная викторина)
     * - Увеличенные награды за правильный ответ (но пользователь не знает сколько)
     * - Увеличенные штрафы за неправильный ответ (интрига сохраняется)
     * - Увеличенные штрафы за тайм-аут (интрига сохраняется)
     * - Вопрос более сложный чем в обычной викторине
     * 
     * ЛОГИКА РАБОТЫ ТАКАЯ ЖЕ КАК В ОБЫЧНОЙ ВИКТОРИНЕ:
     * - Создание сессии, таймера, обработка ответов
     * - Но награды/штрафы больше (скрыто от пользователя)!
     *
     * @param bot - объект бота для отправки/изменения сообщений
     * @param chatId - ID чата пользователя
     * @param question - текст сложного вопроса экспертной викторины
     * @param options - варианты ответов (сложные, требуют экспертных знаний)
     * @param correctAnswer - правильный ответ из списка options
     */
    public void startJackpotQuizWithTimer(TelegramLongPollingBot bot, Long chatId, String question, List<String> options, String correctAnswer) {
        log.info("EpicQuizTimer: 🏆 Запуск ЭКСПЕРТНОЙ викторины для chatId={}", chatId);
        log.debug("EpicQuizTimer: ЭКСПЕРТНЫЙ вопрос: {}, варианты: {}, правильный ответ: {}", question, options, correctAnswer);

        // ВРЕМЯ НА ЭКСПЕРТНУЮ ВИКТОРИНУ (ПОКА ТАКОЕ ЖЕ КАК У ОБЫЧНОЙ)
        int timeLimit = 30; // секунд на ответ
        
        // СОЗДАЕМ СЕССИЮ ЭКСПЕРТНОЙ ВИКТОРИНЫ (ПОМЕЧАЕМ КАК ЭКСПЕРТНУЮ)
        QuizSession session = new QuizSession(question, options, correctAnswer, timeLimit);
        session.setJackpot(true); // ← ПОМЕЧАЕМ ЧТО ЭТО ЭКСПЕРТНАЯ ВИКТОРИНА (поле называется isJackpot, но значит "повышенные награды")!
        session.setTicketNumber(10); // ← ПОМЕЧАЕМ ЧТО ЭТО БИЛЕТ 10!
        activeSessions.put(chatId, session);

        try {
            // ОТПРАВЛЯЕМ СООБЩЕНИЕ ЭКСПЕРТНОЙ ВИКТОРИНЫ (ВЫГЛЯДИТ ТАК ЖЕ КАК ОБЫЧНАЯ)
            SendMessage quizMessage = createEpicQuizMessage(chatId, session);
            Message sentMessage = bot.execute(quizMessage);
            
            // СОХРАНЯЕМ ID СООБЩЕНИЯ ДЛЯ ДАЛЬНЕЙШЕГО РЕДАКТИРОВАНИЯ
            session.setMessageId(sentMessage.getMessageId());

            // 🔥 ЗАПУСКАЕМ ЕБИЧЕСКИЙ ТАЙМЕР ДЛЯ ЭКСПЕРТНОЙ ВИКТОРИНЫ!
            startEpicTimer(bot, chatId, session);
            
            log.info("EpicQuizTimer: ✅ ЭКСПЕРТНЫЙ таймер успешно запущен для chatId={}", chatId);

        } catch (TelegramApiException e) {
            log.error("EpicQuizTimer: ❌ Ошибка отправки ЭКСПЕРТНОЙ викторины для chatId={}: {}", chatId, e.getMessage());
            activeSessions.remove(chatId); // Удаляем сессию если не смогли отправить
        }
    }



}


/**
 * 📋 СЕССИЯ ВИКТОРИНЫ - хранит все данные одной викторины
 * <p>
 * Зачем нужен отдельный класс:
 * - У каждого пользователя свои данные викторины
 * - Нужно хранить вопрос, варианты, правильный ответ
 * - Нужно отслеживать время и ID сообщения для изменения
 * - Нужна ссылка на таймер чтобы его остановить
 */
@Data
@AllArgsConstructor
class QuizSession {
    private String question;                    // Текст вопроса
    private List<String> options;              // Варианты ответов ["A", "B", "C"]
    private String correctAnswer;              // Правильный ответ
    private int totalTime;                     // Изначальное время (30 секунд)
    private int timeLeft;                      // Оставшееся время (30→29→28→...→0)
    private Integer messageId;                 // ID сообщения в Telegram для изменения
    private ScheduledFuture<?> timerTask;      // Ссылка на таймер для остановки
    private boolean isJackpot;                 // Джекпот викторина (увеличенные награды/штрафы)
    private int ticketNumber;                  // Номер билета (9 или 10) для отображения в заголовке

    /**
     * 🏗️ КОНСТРУКТОР БЕЗ messageId И timerTask
     * (они устанавливаются позже)
     */
    public QuizSession(String question, List<String> options, String correctAnswer, int totalTime) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.totalTime = totalTime;
        this.timeLeft = totalTime;  // ← АВТОМАТИЧЕСКИ устанавливаем timeLeft = totalTime
        // messageId и timerTask остаются null до создания
    }


    public void decrementTime() {
        this.timeLeft--;
    }
}
