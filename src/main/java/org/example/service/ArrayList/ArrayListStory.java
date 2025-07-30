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
import java.util.concurrent.TimeUnit;
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
 * Автор: Архитектор (который знает, что маршрутизация — это искусство)
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
    private final MessageService messageService;
    private final StatService statService;
    private final UserService userService;
    private final QuizService quizService;

    // Карта команд для маршрутизации
    private final Map<String, BiConsumer<TelegramLongPollingBot, Message>> commandsMap = new HashMap<>();


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
            try {
                // 1. Отправляем сообщение о защите
                SendMessage defenseMessage = messageService.createTryCatchDefenseMessage(msg.getChatId());
                bot.execute(defenseMessage);

                // 2. Через 5 секунды отправляем результат от Итераториуса
                schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                    try {
                        // Генерируем награды
                        int expReward = statService.generateRandomReward(50, 70);
                        int cashReward = statService.generateRandomReward(200, 300);

                        // Применяем награды
                        Map<String, Integer> rewards = Map.of(
                                "achievement_points", expReward,
                                "currency", cashReward
                        );
                        statService.applyStatChanges(msg.getChatId(), rewards);

                        // Отправляем результат
                        SendMessage resultMessage = messageService.createBattleResultMessage(msg.getChatId(), expReward, cashReward);
                        bot.execute(resultMessage);

                        // 3. Еще через 4 секунды отправляем завершение раунда
                        schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                            try {
                                SendMessage endRoundMessage = messageService.endOfRoundOne(msg.getChatId());
                                bot.execute(endRoundMessage);
                                
                                // 4. Через 2 секунды отправляем сообщение от Итераториуса и викторину
                                schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                                    sendIteratoriusMessageAndQuiz(bot, msg.getChatId());
                                }, 2);
                                
                            } catch (TelegramApiException e) {
                                log.error("ArrayListStory: Ошибка отправки завершения раунда для chatId={}", msg.getChatId(), e);
                            }
                        }, 4);

                    } catch (TelegramApiException e) {
                        log.error("ArrayListStory: Ошибка отправки результата try-catch для chatId={}", msg.getChatId(), e);
                    }
                }, 5);

            } catch (TelegramApiException e) {
                log.error("ArrayListStory: Ошибка отправки сообщения о защите для chatId={}", msg.getChatId(), e);
            }
        });

        // Команда для боевого действия Анализировать
        commandsMap.put("\uD83D\uDD0D Уклониться \n и \nпроанализировать", (bot, msg) -> {
            log.info("ArrayListStory: Обработка команды '🔍 Уклониться и проанализировать' для chatId={}", msg.getChatId());

            try {
                // 1. Отправляем сообщение об анализе
                SendMessage analysisMessage = messageService.createAnalysisMessage(msg.getChatId());
                bot.execute(analysisMessage);

                // 2. Через 3 секунды отправляем результат от Итераториуса
                schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                    try {
                        // Генерируем награды
                        int expReward = statService.generateRandomReward(60, 85);
                        int cashReward = statService.generateRandomReward(250, 350);

                        // Применяем награды
                        Map<String, Integer> rewards = Map.of(
                                "achievement_points", expReward,
                                "currency", cashReward
                        );
                        statService.applyStatChanges(msg.getChatId(), rewards);

                        // Отправляем результат
                        SendMessage resultMessage = messageService.createAnalysisResultMessage(msg.getChatId(), expReward, cashReward);
                        bot.execute(resultMessage);

                        // 3. Еще через 3 секунды отправляем завершение раунда
                        schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                            try {
                                SendMessage endRoundMessage = messageService.endOfRoundOne(msg.getChatId());
                                bot.execute(endRoundMessage);
                                
                                // 4. Через 2 секунды отправляем сообщение от Итераториуса и викторину
                                schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                                    sendIteratoriusMessageAndQuiz(bot, msg.getChatId());
                                }, 2);
                                
                            } catch (TelegramApiException e) {
                                log.error("ArrayListStory: Ошибка отправки завершения раунда для chatId={}", msg.getChatId(), e);
                            }
                        }, 5);

                    } catch (TelegramApiException e) {
                        log.error("ArrayListStory: Ошибка отправки результата анализа для chatId={}", msg.getChatId(), e);
                    }
                },4);

            } catch (TelegramApiException e) {
                log.error("ArrayListStory: Ошибка отправки сообщения об анализе для chatId={}", msg.getChatId(), e);
            }
        });

        commandsMap.put("\uD83D\uDEA8 Отразить \n" +
                "вставкой \n" +
                " в начало", (bot, msg) -> {
            log.info("ArrayListStory: Обработка команды 'Отразить' для chatId={}", msg.getChatId());

            try {
                // 1. Отправляем сообщение о вставке
                SendMessage insertMessage = messageService.createInsertBeginningMessage(msg.getChatId());
                bot.execute(insertMessage);

                // 2. Через 3 секунды отправляем результат от Итераториуса
                schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                    try {
                        // Генерируем изменения статов для вставки в начало
                        Map<String, Integer> statChanges = statService.generateCustomStatChanges();
                        var user = userService.getUserByTgId(msg.getChatId());
                        if (user != null && user.getPersonage() != null) {
                            String characterType = user.getPersonage().getCharacterType();
                            String individualStat = statService.getIndividualStatForCharacter(characterType);
                            statChanges.put(individualStat, statService.generateRandomReward(15, 25));
                        }

                        // Применяем изменения статов
                        statService.applyStatChanges(msg.getChatId(), statChanges);

                        // Отправляем результат
                        SendMessage resultMessage = messageService.createInsertBeginningResultMessage(msg.getChatId(), statChanges);
                        bot.execute(resultMessage);

                        // 3. Еще через 3 секунды отправляем завершение раунда
                        schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                            try {
                                SendMessage endRoundMessage = messageService.endOfRoundOne(msg.getChatId());
                                bot.execute(endRoundMessage);
                                
                                // 4. Через 2 секунды отправляем сообщение от Итераториуса и викторину
                                schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                                    sendIteratoriusMessageAndQuiz(bot, msg.getChatId());
                                }, 2);
                                
                            } catch (TelegramApiException e) {
                                log.error("ArrayListStory: Ошибка отправки завершения раунда для chatId={}", msg.getChatId(), e);
                            }
                        }, 4);

                    } catch (TelegramApiException e) {
                        log.error("ArrayListStory: Ошибка отправки результата вставки для chatId={}", msg.getChatId(), e);
                    }
                }, 5);

            } catch (TelegramApiException e) {
                log.error("ArrayListStory: Ошибка отправки сообщения о вставке для chatId={}", msg.getChatId(), e);
            }
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
}
