package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.UserService;
import org.example.service.PersonageStatManager;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * QuizService — универсальный сервис для работы с викторинами.
 * <p>
 * Этот класс объединяет функциональность ArrayListQuiz и ArrayListQuizHandler.
 * Теперь вместо двух классов у нас один — меньше файлов, меньше проблем!
 * <p>
 * Что делает:
 * - Создает викторины по ArrayList
 * - Обрабатывает ответы пользователей
 * - Начисляет/снимает очки за ответы
 * - Отправляет персонализированные сообщения
 * <p>
 * Связи с другими классами:
 * - Использует StatService для изменения статов персонажей
 * - Используется в ArrayListStory для отправки викторин
 * - Используется в Bot для обработки ответов на викторины
 * <p>
 * Автор: Архитектор (который знает, что меньше кода = меньше багов)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final StatService statService;
    private final UserService userService;
    private final ArrayListTheoryService arrayListTheoryService;
    private final PersonageStatManager personageStatManager;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    // 🗺️ MAP ДЛЯ ОТСЛЕЖИВАНИЯ ТИПОВ ВИКТОРИН: chatId -> тип последней викторины
    private final Map<Long, String> lastQuizType = new HashMap<>();

    /**
     * Создает викторину по ArrayList.
     * 
     * @param chatId — ID чата Telegram
     * @return SendPoll — объект викторины
     *
     * Пример:
     *   SendPoll poll = quizService.createQuiz(chatId);
     *   bot.execute(poll);
     */
    public SendPoll createQuiz(Long chatId) {
        log.info("QuizService: Создание викторины по ArrayList для chatId={}", chatId);
        
        SendPoll poll = new SendPoll();
        poll.setIsAnonymous(false); // НЕАНОНИМНАЯ викторина для отслеживания ответов
        
        poll.setChatId(chatId);
        poll.setQuestion(QuizConstants.QUIZ_QUESTION);
        poll.setOptions(QuizConstants.QUIZ_OPTIONS);
        poll.setCorrectOptionId(QuizConstants.CORRECT_ANSWER);
        poll.setType("quiz");
        poll.setExplanation("БЛЯТЬ");
        
        // 🏷️ Сохраняем тип викторины для правильной обработки ответов
        lastQuizType.put(chatId, "normal");
        
        log.info("QuizService: Обычная викторина создана для chatId={}, правильный ответ: {}", chatId, QuizConstants.CORRECT_ANSWER);
        return poll;
    }

    /**
     * ☕ СОЗДАЕТ КОФЕ-ВИКТОРИНУ С NULL
     * 
     * Специальная викторина для кофе-брейка про добавление null в ArrayList.
     * 
     * @param chatId — ID чата Telegram
     * @return SendPoll — объект кофе-викторины
     *
     * Пример:
     *   SendPoll coffeeQuiz = quizService.createCoffeeQuiz(chatId);
     *   bot.execute(coffeeQuiz);
     */
    public SendPoll createCoffeeQuiz(Long chatId) {
        log.info("QuizService: Создание кофе-викторины для chatId={}", chatId);
        
        SendPoll poll = new SendPoll();
        poll.setIsAnonymous(false); // НЕАНОНИМНАЯ викторина для отслеживания ответов
        
        poll.setChatId(chatId);
        poll.setQuestion(QuizConstants.COFFEE_QUIZ_QUESTION);
        poll.setOptions(QuizConstants.COFFEE_QUIZ_OPTIONS);
        poll.setCorrectOptionId(QuizConstants.COFFEE_CORRECT_ANSWER);
        poll.setType("quiz");
        poll.setExplanation("ArrayList может хранить null значения!");
        
        // ☕ Сохраняем тип кофе-викторины для правильной обработки ответов  
        lastQuizType.put(chatId, "coffee");
        
        log.info("QuizService: Кофе-викторина создана для chatId={}, правильный ответ: {}", chatId, QuizConstants.COFFEE_CORRECT_ANSWER);
        return poll;
    }

    /**
     * 🎯 УНИВЕРСАЛЬНЫЙ ОБРАБОТЧИК ВИКТОРИН
     * 
     * Автоматически определяет тип викторины (обычная или кофе-викторина)
     * и вызывает соответствующий обработчик.
     * 
     * @param pollAnswer — ответ пользователя на викторину
     * @param bot        — Telegram бот для отправки ответа
     */
    public void handleAnyQuizAnswer(PollAnswer pollAnswer, TelegramLongPollingBot bot) {
        Long chatId = pollAnswer.getUser().getId();
        String quizType = lastQuizType.get(chatId);
        
        log.info("QuizService: Получен ответ на викторину для chatId={}, тип викторины: {}", chatId, quizType);
        
        if ("coffee".equals(quizType)) {
            // ☕ Это кофе-викторина - используем специальный обработчик (БЕЗ запуска раунда 2)
            log.info("QuizService: Обрабатываем ответ на КОФЕ-викторину для chatId={}", chatId);
            handleCoffeeQuizAnswer(pollAnswer, bot);
        } else if ("normal".equals(quizType)) {
            // 📝 Это обычная викторина - используем стандартный обработчик (С запуском раунда 2)
            log.info("QuizService: Обрабатываем ответ на ОБЫЧНУЮ викторину для chatId={}", chatId);
            handleQuizAnswer(pollAnswer, bot);
        } else {
            // ❓ Неизвестный тип викторины - логируем и используем стандартный обработчик
            log.warn("QuizService: Неизвестный тип викторины '{}' для chatId={}, используем стандартный обработчик", quizType, chatId);
            handleQuizAnswer(pollAnswer, bot);
        }
        
        // 🧹 Очищаем тип викторины после обработки
        lastQuizType.remove(chatId);
    }

    /**
     * Обрабатывает ответ пользователя на викторину.
     * 
     * @param pollAnswer — ответ пользователя на викторину
     * @param bot — TelegramLongPollingBot для отправки сообщений
     *
     * Логика работы:
     * 1. Проверяем правильность ответа
     * 2. Начисляем/снимаем очки
     * 3. Отправляем персонализированное сообщение
     */
    public void handleQuizAnswer(PollAnswer pollAnswer, TelegramLongPollingBot bot) {
        Long chatId = pollAnswer.getUser().getId();
        int selectedOption = pollAnswer.getOptionIds().get(0);
        boolean isCorrect = selectedOption == QuizConstants.CORRECT_ANSWER;
        
        log.info("QuizService: Обработка ответа на викторину для chatId={}, выбранный вариант: {}, правильный: {}, результат: {}", 
                chatId, selectedOption, QuizConstants.CORRECT_ANSWER, isCorrect ? "ПРАВИЛЬНО" : "НЕПРАВИЛЬНО");

        // Начисляем/снимаем очки в зависимости от правильности ответа
        int points = isCorrect ? QuizConstants.CORRECT_REWARD : QuizConstants.WRONG_PENALTY;
        Map<String, Integer> statChanges = Map.of("achievement_points", points);
        statService.applyStatChanges(chatId, statChanges);
        
        // Отправляем персонализированное сообщение
        sendQuizResult(bot, chatId, isCorrect);
        
        // После викторины запускаем раунд 2 через 5 секунд
        startRound2AfterQuiz(bot, chatId);
    }

    /**
     * ☕ ОБРАБАТЫВАЕТ ОТВЕТ НА КОФЕ-ВИКТОРИНУ
     * 
     * Специальная обработка для кофе-викторины с null.
     * 
     * @param pollAnswer — ответ пользователя на кофе-викторину
     * @param bot — TelegramLongPollingBot для отправки сообщений
     */
    public void handleCoffeeQuizAnswer(PollAnswer pollAnswer, TelegramLongPollingBot bot) {
        Long chatId = pollAnswer.getUser().getId();
        int selectedOption = pollAnswer.getOptionIds().get(0);
        boolean isCorrect = selectedOption == QuizConstants.COFFEE_CORRECT_ANSWER;
        
        log.info("QuizService: Обработка ответа на КОФЕ-викторину для chatId={}, выбранный вариант: {}, правильный: {}, результат: {}", 
                chatId, selectedOption, QuizConstants.COFFEE_CORRECT_ANSWER, isCorrect ? "ПРАВИЛЬНО" : "НЕПРАВИЛЬНО");

        // Начисляем/снимаем очки через PersonageStatManager
        PersonageStatManager.StatUpdateResult result;
        if (isCorrect) {
            result = personageStatManager.updateRandomRewards(chatId, 
                QuizConstants.COFFEE_CORRECT_REWARD, QuizConstants.COFFEE_CORRECT_REWARD, 0, 0);
        } else {
            result = personageStatManager.updateRandomRewards(chatId, 
                QuizConstants.COFFEE_WRONG_PENALTY, QuizConstants.COFFEE_WRONG_PENALTY, 0, 0);
        }
        
        // Отправляем результат кофе-викторины
        sendCoffeeQuizResult(bot, chatId, isCorrect);
    }

    /**
     * ☕ ОТПРАВЛЯЕТ РЕЗУЛЬТАТ КОФЕ-ВИКТОРИНЫ
     * 
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     * @param isCorrect — правильно ли ответил пользователь
     */
    private void sendCoffeeQuizResult(TelegramLongPollingBot bot, Long chatId, boolean isCorrect) {
        try {
            SendMessage message = createCoffeeQuizResultMessage(chatId, isCorrect);
            bot.execute(message);
            
            String result = isCorrect ? "ПРАВИЛЬНО" : "НЕПРАВИЛЬНО";
            int points = isCorrect ? QuizConstants.COFFEE_CORRECT_REWARD : QuizConstants.COFFEE_WRONG_PENALTY;
            log.info("QuizService: Результат КОФЕ-викторины отправлен для chatId={}, результат: {}, очки: {}", chatId, result, points);
            
        } catch (TelegramApiException e) {
            log.error("QuizService: Ошибка отправки результата кофе-викторины для chatId={}", chatId, e);
        }
    }

    /**
     * Отправляет результат викторины пользователю.
     * 
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     * @param isCorrect — правильно ли ответил пользователь
     */
    private void sendQuizResult(TelegramLongPollingBot bot, Long chatId, boolean isCorrect) {
        try {
            SendMessage message = createQuizResultMessage(chatId, isCorrect);
            bot.execute(message);
            
            String result = isCorrect ? "ПРАВИЛЬНО" : "НЕПРАВИЛЬНО";
            int points = isCorrect ? QuizConstants.CORRECT_REWARD : QuizConstants.WRONG_PENALTY;
            log.info("QuizService: Результат викторины отправлен для chatId={}, результат: {}, очки: {}", chatId, result, points);
            
            // Через 3 секунды отправляем реплику Итераториуса о раунде 2
            scheduler.schedule(() -> {
                try {
                    log.info("QuizService: Отправляем реплику Итераториуса о раунде 2 для chatId={}", chatId);
                    
                    MessageServiceRound2 messageServiceRound2 = new MessageServiceRound2(userService, arrayListTheoryService, personageStatManager, this);
                    SendMessage round2IntroMessage = messageServiceRound2.createIteratoriusRound2Intro(chatId);
                    bot.execute(round2IntroMessage);
                    
                    log.info("QuizService: Реплика Итераториуса о раунде 2 отправлена для chatId={}", chatId);
                    
                } catch (TelegramApiException e) {
                    log.error("QuizService: Ошибка отправки реплики Итераториуса о раунде 2 для chatId={}", chatId, e);
                } catch (Exception e) {
                    log.error("QuizService: Неожиданная ошибка при отправке реплики о раунде 2 для chatId={}", chatId, e);
                }
            }, 3, TimeUnit.SECONDS);
            
        } catch (TelegramApiException e) {
            log.error("QuizService: Ошибка отправки результата викторины для chatId={}", chatId, e);
        }
    }

    /**
     * Создает сообщение с результатом викторины.
     * 
     * @param chatId — ID чата пользователя
     * @param isCorrect — правильно ли ответил пользователь
     * @return SendMessage — сообщение с результатом
     */
    private SendMessage createQuizResultMessage(Long chatId, boolean isCorrect) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        
        // Выбираем сообщение в зависимости от правильности ответа
        String messageText = isCorrect ? QuizConstants.CORRECT_MESSAGE : QuizConstants.WRONG_MESSAGE;
        sendMessage.setText(messageText);
        
        return sendMessage;
    }

    /**
     * ☕ СОЗДАЕТ СООБЩЕНИЕ С РЕЗУЛЬТАТОМ КОФЕ-ВИКТОРИНЫ
     * 
     * @param chatId — ID чата пользователя
     * @param isCorrect — правильно ли ответил пользователь
     * @return SendMessage — сообщение с результатом кофе-викторины
     */
    private SendMessage createCoffeeQuizResultMessage(Long chatId, boolean isCorrect) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        
        // Выбираем сообщение в зависимости от правильности ответа
        String messageText = isCorrect ? QuizConstants.COFFEE_CORRECT_MESSAGE : QuizConstants.COFFEE_WRONG_MESSAGE;
        sendMessage.setText(messageText);
        
        return sendMessage;
    }

    /**
     * Создает сообщение от Итераториуса перед викториной.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение от Итераториуса
     */
    public SendMessage createQuizIntroMessage(Long chatId) {
        log.debug("QuizService: Создание вступительного сообщения для викторины chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText(QuizConstants.QUIZ_INTRO_MESSAGE);
        
        return sendMessage;
    }
    
    /**
     * 🚀 ЗАПУСК РАУНДА 2 ПОСЛЕ ВИКТОРИНЫ
     * 
     * Простой способ перехода к раунду 2 после завершения викторины.
     * Вызывается автоматически после ответа на викторину с задержкой 6 секунд
     * (3 сек после результата викторины + 3 сек после реплики Итераториуса).
     * 
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
    private void startRound2AfterQuiz(TelegramLongPollingBot bot, Long chatId) {
        log.info("QuizService: Планируется запуск раунда 2 через 6 секунд для chatId={}", chatId);
        
        scheduler.schedule(() -> {
            try {
                log.info("QuizService: Запускаем ПОЛНУЮ ЦЕПОЧКУ раунда 2 для chatId={}", chatId);
                
                // 🚀 ЗАПУСКАЕМ ПОЛНУЮ ЦЕПОЧКУ: фото + 3 сообщения с задержками
                MessageServiceRound2 messageServiceRound2 = new MessageServiceRound2(userService, arrayListTheoryService, personageStatManager, this);
                messageServiceRound2.startRound2Sequence(bot, chatId);
                
                log.info("QuizService: ✅ Полная цепочка раунда 2 ЗАПЛАНИРОВАНА для chatId={}", chatId);
                
            } catch (Exception e) {
                log.error("QuizService: Неожиданная ошибка при запуске раунда 2 для chatId={}", chatId, e);
            }
        }, 6, TimeUnit.SECONDS);
    }
} 