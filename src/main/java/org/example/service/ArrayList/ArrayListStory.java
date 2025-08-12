package org.example.service.ArrayList;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * ArrayListStory — основной сервис для маршрутизации команд в изучении ArrayList.
 * <p>
 * Этот класс отвечает за:
 * - Маршрутизацию команд пользователя к соответствующим сервисам
 * - Прямую обработку боевых действий (try-catch, анализ, вставка)
 * - Управление жизненным циклом изучения ArrayList
 * - Координацию между различными сервисами
 * <p>
 * Связи с другими классами:
 * - Использует ArrayListTheoryService для работы с теорией и контентом
 * - Использует ArrayListSchedulerService для планирования событий
 * - Использует MessageService для создания сообщений
 * - Использует StatService для работы со статами персонажей
 * - Использует UserService для работы с пользователями
 * - Интегрируется с MessageHandlerService для получения команд
 * <p>
 * Принцип работы:
 * 1. Получает команду от MessageHandlerService
 * 2. Проверяет, может ли обработать команду
 * 3. Выполняет прямую обработку боевых действий
 * 4. Координирует работу других сервисов
 * 5. Логирует все действия для отладки
 * <p>
 * Боевые действия обрабатываются напрямую:
 * - "🛡 Блокировать (try-catch)" — защита с наградами
 * - "🔍 Уклониться и проанализировать" — анализ с наградами
 * - "📜 Получить боевой свиток" — запуск теории с автоудалением
 * <p>
 * Автор: Иларион (который знает, что маршрутизация — это искусство)
 * <p>
 * Пример использования:
 *
 * @Autowired private ArrayListStory arrayListStory;
 * <p>
 * if (arrayListStory.canHandle(text)) {
 *     arrayListStory.handle(bot, message);
 * }
 */
@RequiredArgsConstructor
@Service
public class ArrayListStory {
    private static final Logger log = LoggerFactory.getLogger(ArrayListStory.class);

    // Специализированные сервисы для разных аспектов ArrayList
    private final ArrayListTheoryService theoryService;
    private final ArrayListSchedulerService schedulerService;
    private final StatService statService;
    private final UserService userService;
    private final QuizService quizService;
   private final MessageServiceRound2 messageSrviceRound2;

    // Карта команд для маршрутизации
    private final Map<String, BiConsumer<TelegramLongPollingBot, Message>> commandsMap = new HashMap<>();
    private final MessageServiceRound1 messageServiceRound1;


    /**
     * Инициализирует карту команд для маршрутизации.
     * <p>
     * Этот метод заполняет карту команд, связывая текстовые команды
     * с соответствующими обработчиками в специализированных сервисах.
     * <p>
     * Команды:
     * - "📜 Получить боевой свиток" → отправка теории с автоудалением
     * - "🛡 Блокировать (try-catch)" → обработка боевого действия
     * <p>
     * Принцип работы:
     * 1. Каждая команда связывается с лямбда-функцией
     * 2. Лямбда-функция делегирует обработку в соответствующий сервис
     * 3. Все действия логируются для отладки
     */
    @PostConstruct
    public void initCommands() {
        log.info("ArrayListStory: Инициализация карты команд");

        // Команда для получения теории
        commandsMap.put("📜 Получить боевой свиток", (bot, msg) -> {
            log.info("ArrayListStory: Обработка команды '📜 Получить боевой свиток' для chatId={}", msg.getChatId());
            schedulerService.sendTheoryWithAutoDelete(bot, msg.getChatId());
        });

        // Команда для боевого действия try-catch
        commandsMap.put("\uD83D\uDEE1 Блокировать \n (try-catch)", (bot, msg) -> {
            log.info("ArrayListStory: Обработка команды '🛡 Блокировать (try-catch)' для chatId={}", msg.getChatId());
            
            SendMessage defenseMessage = messageServiceRound1.createTryCatchDefenseMessage(msg.getChatId());
            executeBattleAction(bot, msg.getChatId(), defenseMessage, "try-catch", 50, 70, 200, 300, false);
        });

        // Команда для боевого действия Анализировать
        commandsMap.put("\uD83D\uDD0D Уклониться \n и \nпроанализировать", (bot, msg) -> {
            log.info("ArrayListStory: Обработка команды '🔍 Уклониться и проанализировать' для chatId={}", msg.getChatId());
            
            SendMessage analysisMessage = messageServiceRound1.createAnalysisMessage(msg.getChatId());
            executeBattleAction(bot, msg.getChatId(), analysisMessage, "анализ", 60, 85, 250, 350, false);
        });

        commandsMap.put("\uD83D\uDEA8 Отразить \n" +
                "вставкой \n" +
                " в начало", (bot, msg) -> {
            log.info("ArrayListStory: Обработка команды 'Отразить' для chatId={}", msg.getChatId());
            
            SendMessage insertMessage = messageServiceRound1.createInsertBeginningMessage(msg.getChatId());
            executeBattleAction(bot, msg.getChatId(), insertMessage, "атака", 40, 60, -250, -100, true); // Отрицательные значения для уменьшения денег
        });

        commandsMap.put("☠\uFE0F Добить",((bot, msg) -> {
            log.info("ArrayListStory: Обработка команды '☠\uFE0F Добить' для chatId={}", msg.getChatId());
            SendMessage finishOff = messageSrviceRound2.messageFinishOff(bot, msg.getChatId());
            try {
                bot.execute(finishOff);
            } catch (TelegramApiException e) {
                log.error("ошибка отправки сообщения на кнопку ДОБИТЬ");
            }
        }));

        commandsMap.put("\uD83E\uDDE0 .ensureCapacity()",(bot,msg)->{
            log.info("ArrayListStory: Обработка команды '\uD83E\uDDE0 ensureCapacity' для chatId={}", msg.getChatId());
            
            // 🚀 Запускаем цепочку: основное сообщение → ответ Аррейна → ответ Итераториуса
            messageSrviceRound2.messageEnsureCapacityRound2(bot, msg.getChatId());

        });

        commandsMap.put("☕Кофе пауза",(bot,msg) ->{
            log.info("ArrayListStory: Обработка команды '☕Кофе пауза' для chatId={}", msg.getChatId());
            
            // 🚀 Запускаем кофе-сценарий: фото с кодом → викторина → ответ Итераториуса
            messageSrviceRound2.messageCoffeRound2(bot, msg.getChatId());
        });


        log.info("ArrayListStory: Карта команд инициализирована, количество команд: {}", commandsMap.size());
    }

    /**
     * Проверяет, может ли этот сервис обработать данную команду.
     * <p>
     * Этот метод проверяет наличие команды в карте маршрутизации.
     * Используется в MessageHandlerService для определения, какой сервис
     * должен обработать команду пользователя.
     *
     * @param text — текст команды от пользователя
     * @return boolean — true если команда может быть обработана
     * <p>
     * Пример использования:
     * if (arrayListStory.canHandle(text)) {
     * arrayListStory.handle(bot, message);
     * }
     */
    public boolean canHandle(String text) {
        boolean canHandle = commandsMap.containsKey(text);
        log.info("ArrayListStory: Проверка команды '{}' - canHandle: {}", text, canHandle);

        // Дополнительное логирование для отладки
        if (!canHandle) {
            log.warn("ArrayListStory: Команда '{}' не найдена в карте. Доступные команды: {}",
                    text, commandsMap.keySet());
        }

        return canHandle;
    }

    /**
     * Обрабатывает команду пользователя, делегируя её в соответствующий сервис.
     * <p>
     * Этот метод является центральной точкой маршрутизации для ArrayList.
     * Он получает команду и делегирует её обработку в специализированный сервис.
     * <p>
     * Процесс обработки:
     * 1. Проверяет наличие команды в карте
     * 2. Логирует информацию о команде
     * 3. Вызывает соответствующий обработчик
     * 4. Логирует результат обработки
     *
     * @param bot     — TelegramLongPollingBot для отправки сообщений
     * @param message — сообщение от пользователя
     *                <p>
     *                Пример использования:
     *                arrayListStory.handle(bot, message);
     */
    public void handle(TelegramLongPollingBot bot, Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();

        log.info("ArrayListStory: Обработка команды '{}' для chatId={}, userId={}", text, chatId, userId);

        if (commandsMap.containsKey(text)) {
            try {
                // Вызываем соответствующий обработчик
                commandsMap.get(text).accept(bot, message);
                log.info("ArrayListStory: Команда '{}' успешно обработана", text);
            } catch (Exception e) {
                log.error("ArrayListStory: Ошибка при обработке команды '{}' для chatId={}", text, chatId, e);
            }
        } else {
            log.warn("ArrayListStory: Команда '{}' не найдена в карте маршрутизации", text);
        }
    }

    /**
     * Отправляет сообщение от Итераториуса и викторину с задержкой.
     * 
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
    private void sendIteratoriusMessageAndQuiz(TelegramLongPollingBot bot, Long chatId) {
        try {
            // Сначала отправляем сообщение от Итераториуса
            SendMessage iteratoriusMessage = quizService.createQuizIntroMessage(chatId);
            bot.execute(iteratoriusMessage);
            
            // Через 1 секунду отправляем викторину
            schedulerService.scheduleEvent(bot, chatId, () -> {
                try {
                    SendPoll quiz = quizService.createQuiz(chatId);
                    bot.execute(quiz);
                    log.info("ArrayListStory: Викторина отправлена после сообщения Итераториуса для chatId={}", chatId);
                } catch (TelegramApiException e) {
                    log.error("ArrayListStory: Ошибка отправки викторины для chatId={}", chatId, e);
                }
            }, 1);
            
        } catch (TelegramApiException e) {
            log.error("ArrayListStory: Ошибка отправки сообщения Итераториуса для chatId={}", chatId, e);
        }
    }

    /**
     * Отправляет сообщение от Итераториуса с кнопкой "📜 Получить боевой свиток".
     * <p>
     * Этот метод отправляет начальное сообщение от Итераториуса:
     * "⚔️ Твоя первая цель — ArrayList. Не дай простоте тебя обмануть..."
     * с кнопкой для получения теории.
     *
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение от Итераториуса с клавиатурой
     */
    public SendMessage sendIteratoriusMessage(Long chatId) {
        log.info("ArrayListStory: Отправка сообщения от Итераториуса для chatId={}", chatId);
        return theoryService.createIteratoriusMessage(chatId);
    }
    
    // ========================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ СОКРАЩЕНИЯ КОДА
    // ========================================
    
    /**
     * 🛠️ УНИВЕРСАЛЬНЫЙ ОБРАБОТЧИК БОЕВЫХ ДЕЙСТВИЙ
     * 
     * Упрощает повторяющуюся логику в initCommands:
     * 1. Отправляет сообщение о действии
     * 2. Через 5 сек показывает результат с наградами
     * 3. Через 5 сек показывает завершение раунда  
     * 4. Через 3 сек запускает викторину
     * 
     * @param bot - Telegram bot
     * @param chatId - ID чата
     * @param actionMessage - сообщение о действии пользователя
     * @param actionType - тип действия для логирования
     * @param expRewardMin - минимальная награда опыта
     * @param expRewardMax - максимальная награда опыта
     * @param moneyRewardMin - минимальная награда денег
     * @param moneyRewardMax - максимальная награда денег
     * @param includePersonalStat - добавить ли персональный стат персонажа
     */
    private void executeBattleAction(TelegramLongPollingBot bot, Long chatId, SendMessage actionMessage, 
                                   String actionType, int expRewardMin, int expRewardMax, 
                                   int moneyRewardMin, int moneyRewardMax, boolean includePersonalStat) {
        
        log.info("ArrayListStory: Выполнение боевого действия '{}' для chatId={}", actionType, chatId);
        
        try {
            // 1. Отправляем сообщение о действии
            bot.execute(actionMessage);
            
            // 2. Через 5 секунд отправляем результат
            schedulerService.scheduleEvent(bot, chatId, () -> {
                try {
                    // Генерируем награды
                    int expReward = statService.generateRandomReward(expRewardMin, expRewardMax);
                    int cashReward = statService.generateRandomReward(moneyRewardMin, moneyRewardMax);
                    
                    // Создаем мапу наград
                    Map<String, Integer> rewards = new java.util.HashMap<>(Map.of(
                        "achievement_points", expReward,
                        "currency", cashReward
                    ));
                    
                    // Добавляем персональный стат если нужно
                    if (includePersonalStat) {
                        addPersonalStatReward(chatId, rewards);
                    }
                    
                    // Применяем награды
                    statService.applyStatChanges(chatId, rewards);
                    
                    // Отправляем результат
                    SendMessage resultMessage;
                    if (includePersonalStat) {
                        resultMessage = messageServiceRound1.createInsertBeginningResultMessage(chatId, rewards);
                    } else {
                        // Выбираем правильное сообщение в зависимости от типа действия
                        switch (actionType) {
                            case "try-catch":
                                resultMessage = messageServiceRound1.createTryCatchResultMessage(chatId, expReward, cashReward);
                                break;
                            case "анализ":
                                resultMessage = messageServiceRound1.createAnalysisResultMessage(chatId, expReward, cashReward);
                                break;
                            default:
                                resultMessage = messageServiceRound1.createTryCatchResultMessage(chatId, expReward, cashReward);
                                break;
                        }
                    }
                    bot.execute(resultMessage);
                    
                    // 3. Через 5 секунд отправляем завершение раунда
                    schedulerService.scheduleEvent(bot, chatId, () -> {
                        try {
                            SendMessage endRoundMessage = messageServiceRound1.endOfRoundOne(chatId);
                            bot.execute(endRoundMessage);
                            
                            // 4. Через 3 секунды запускаем викторину
                            schedulerService.scheduleEvent(bot, chatId, () -> {
                                sendIteratoriusMessageAndQuiz(bot, chatId);
                            }, 3);
                            
                        } catch (TelegramApiException e) {
                            log.error("ArrayListStory: Ошибка отправки завершения раунда '{}' для chatId={}", actionType, chatId, e);
                        }
                    }, 5);
                    
                } catch (TelegramApiException e) {
                    log.error("ArrayListStory: Ошибка отправки результата '{}' для chatId={}", actionType, chatId, e);
                }
            }, 5);
            
        } catch (TelegramApiException e) {
            log.error("ArrayListStory: Ошибка отправки сообщения '{}' для chatId={}", actionType, chatId, e);
        }
    }
    
    /**
     * 🎭 ДОБАВЛЕНИЕ ПЕРСОНАЛЬНОГО СТАТА ПЕРСОНАЖА
     * 
     * Добавляет индивидуальный стат в зависимости от типа персонажа.
     * Используется для агрессивных действий (вставка в начало).
     */
    private void addPersonalStatReward(Long chatId, Map<String, Integer> rewards) {
        try {
            var user = userService.getUserByTgId(chatId);
            if (user != null && user.getPersonage() != null) {
                String characterType = user.getPersonage().getCharacterType();
                String individualStat = statService.getIndividualStatForCharacter(characterType);
                int personalReward = statService.generateRandomReward(15, 25);
                
                rewards.put(individualStat, personalReward);
                log.debug("ArrayListStory: Добавлен персональный стат '{}' = {} для персонажа '{}'", 
                    individualStat, personalReward, characterType);
            }
        } catch (Exception e) {
            log.warn("ArrayListStory: Ошибка добавления персонального стата для chatId={}", chatId, e);
        }
    }
    
    /**
     * 🚀 ЗАПУСК РАУНДА 2 С ЦЕПОЧКОЙ СООБЩЕНИЙ
     * 
     * ПОСЛЕДОВАТЕЛЬНОСТЬ:
     * 1. Фото с объявлением раунда 2 и статами (сразу)
     * 2. Через 5 сек → sendMessageText2Rond
     * 3. Через 3 сек → messageArreyon2Rond  
     * 4. Через 4 сек → messageIteratorius2Rond
     * 
     * После каждого сообщения пользователь может отвечать на кнопки.
     * 
     * @param bot — Telegram бот для отправки
     * @param chatId — ID чата пользователя
     */
    public void startRound2WithSequence(TelegramLongPollingBot bot, Long chatId) {
        log.info("ArrayListStory: 🚀 Делегирование запуска раунда 2 в MessageServiceRound2 для chatId={}", chatId);
        
        // Используем новую надежную реализацию из MessageServiceRound2
        messageSrviceRound2.startRound2Sequence(bot, chatId);
        
        log.info("ArrayListStory: ✅ Раунд 2 успешно делегирован для chatId={}", chatId);
    }
}
