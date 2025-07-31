package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.PhotoService.PhotoReam;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ArrayListSchedulerService — универсальный сервис для планирования задач в Telegram боте.
 * <p>
 * Этот класс отвечает за:
 * - Автоматическое удаление сообщений через заданное время
 * - Последовательное выполнение событий с задержками
 * - Создание сложных сценариев с множественными событиями
 * - Управление временными интервалами в игровом процессе
 * - Планирование отправки фото и сообщений
 * <p>
 * Связи с другими классами:
 * - Используется в ArrayListStory для планирования событий
 * - Работает с ArrayListTheoryService для получения контента
 * - Интегрируется с PhotoReam для отправки изображений
 * - Использует MessageService для создания сообщений
 * - Использует StatService для работы со статами
 * <p>
 * Принцип работы:
 * 1. Пользователь запрашивает теорию
 * 2. Система отправляет теорию и планирует её удаление через 20 секунд
 * 3. После удаления запускается последовательность событий
 * 4. Каждое событие планируется с определенной задержкой
 * <p>
 * Универсальные методы для разработки:
 * - scheduleEvent() — планирование одного события
 * - scheduleMessage() — планирование отправки сообщения
 * - scheduleMessageDeletion() — планирование удаления сообщения
 * - createEventSequence() — создание сложных последовательностей
 * <p>
 * Автор: Архитектор (который знает, что время — это не просто переменная)
 * <p>
 * Пример использования:
 *
 * @Autowired private ArrayListSchedulerService schedulerService;
 * <p>
 * // Простое планирование
 * schedulerService.scheduleEvent(bot, chatId, () -> sendMessage(bot, chatId), 5);
 * <p>
 * // Сложная последовательность
 * ScheduledEvent[] sequence = {
 *     new ScheduledEvent(() -> sendWarning(bot, chatId), 0),
 *     new ScheduledEvent(() -> sendEnemyPhoto(bot, chatId), 3),
 *     new ScheduledEvent(() -> sendAttack(bot, chatId), 6)
 * };
 * schedulerService.createEventSequence(bot, chatId, sequence);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArrayListSchedulerService {

    private final UserService userService;
    private final ArrayListTheoryService theoryService;
    private final StatService statService;
    private final MessageServiceRound1 messageService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Отправляет теорию с автоматическим удалением через 20 секунд.
     * <p>
     * Этот метод реализует игровую механику "свиток самоуничтожится":
     * 1. Отправляет теорию по ArrayList
     * 2. Планирует удаление сообщения через 20 секунд
     * 3. После удаления запускает последовательность событий
     * 4. Отправляет предупреждение о противнике
     * 5. Планирует отправку фото противника
     * 6. Запускает боевую последовательность
     * <p>
     * Временная последовательность:
     * - 0 сек: отправка теории
     * - 20 сек: удаление теории + предупреждение о противнике
     * - 25 сек: отправка фото противника + представление
     * - 29 сек: начало боевой последовательности
     *
     * @param bot    — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     *               <p>
     *               Пример использования:
     *               schedulerService.sendTheoryWithAutoDelete(bot, chatId);
     */
    public void sendTheoryWithAutoDelete(TelegramLongPollingBot bot, Long chatId) {
        log.info("ArrayListSchedulerService: Отправка теории с автоудалением для chatId={}", chatId);

        try {
            // Получаем теорию из теории сервиса
            String theory = theoryService.formatArrayListInfo();
            SendMessage sendMessage = new SendMessage(chatId.toString(), theory);
            sendMessage.setParseMode("Markdown");

            // Отправляем теорию и получаем ID сообщения
            Message sentMsg = bot.execute(sendMessage);
            Integer messageId = sentMsg.getMessageId();

            log.info("ArrayListSchedulerService: Теория отправлена, messageId={}", messageId);

            // Планируем удаление через 20 секунд и запуск последовательности событий
            scheduler.schedule(() -> {
                try {
                    log.info("ArrayListSchedulerService: Удаление теории, messageId={}", messageId);

                    // Удаляем сообщение с теорией
                    DeleteMessage deleteMessage = new DeleteMessage();
                    deleteMessage.setChatId(chatId.toString());
                    deleteMessage.setMessageId(messageId);
                    bot.execute(deleteMessage);

                    log.info("ArrayListSchedulerService: Теория удалена, запуск последовательности событий");

                    // Запускаем последовательность событий после удаления
                    startEventSequence(bot, chatId);

                } catch (Exception e) {
                    log.error("ArrayListSchedulerService: Ошибка при удалении теории или запуске событий для chatId={}", chatId, e);
                }
            }, 20, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("ArrayListSchedulerService: Ошибка при отправке теории для chatId={}", chatId, e);
        }
    }

    /**
     * Запускает последовательность событий после удаления теории.
     * <p>
     * Эта последовательность создает драматический эффект:
     * 1. Предупреждение о приближении противника
     * 2. Отправка фото противника с представлением
     * 3. Начало боевой последовательности
     *
     * @param bot    — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
    private void startEventSequence(TelegramLongPollingBot bot, Long chatId) {
        log.debug("ArrayListSchedulerService: Запуск последовательности событий для chatId={}", chatId);

        try {
            // 1. Отправляем предупреждение о противнике
            SendMessage warningMessage = theoryService.createIteratoriusWarningMessage(chatId);
            bot.execute(warningMessage);
            log.debug("ArrayListSchedulerService: Предупреждение о противнике отправлено");

            // 2. Планируем отправку фото противника через 5 секунд
            scheduler.schedule(() -> {
                try {
                    log.info("ArrayListSchedulerService: Отправка фото противника для chatId={}", chatId);

                    // Отправляем фото противника
                    bot.execute(PhotoReam.photoArray(chatId));

                    // Отправляем представление противника
                    SendMessage introMessage = theoryService.createArrayenIntroMessage(chatId);
                    bot.execute(introMessage);

                    log.debug("ArrayListSchedulerService: Фото и представление противника отправлены");

                    // 3. Планируем начало боевой последовательности через 6 секунд
                    scheduler.schedule(() -> {
                        try {
                            log.info("ArrayListSchedulerService: Начало боевой последовательности для chatId={}", chatId);

                            // Отправляем информацию о раунде 1
                            SendMessage roundMessage = theoryService.createRound1Message(chatId);
                            bot.execute(roundMessage);
                            scheduler.schedule(() -> {
                                // Отправляем атаку противника
                                SendMessage attackMessage = messageService.createAttackMessage(chatId);
                                try {
                                    bot.execute(attackMessage);
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                            }, 4, TimeUnit.SECONDS);

                            log.debug("ArrayListSchedulerService: Боевая последовательность запущена");

                        } catch (TelegramApiException e) {
                            log.error("ArrayListSchedulerService: Ошибка в боевой последовательности для chatId={}", chatId, e);
                        }
                    }, 5, TimeUnit.SECONDS);

                } catch (TelegramApiException e) {
                    log.error("ArrayListSchedulerService: Ошибка отправки фото противника для chatId={}", chatId, e);
                }
            }, 6, TimeUnit.SECONDS);

        } catch (TelegramApiException e) {
            log.error("ArrayListSchedulerService: Ошибка отправки предупреждения для chatId={}", chatId, e);
        }
    }



    /**
     * Планирует выполнение события через заданное количество секунд.
     * <p>
     * Этот метод предоставляет универсальный способ планирования
     * любых действий с задержкой.
     *
     * @param bot          — TelegramLongPollingBot для отправки сообщений
     * @param chatId       — ID чата пользователя
     * @param event        — событие для выполнения
     * @param delaySeconds — задержка в секундах
     *                     <p>
     *                     Пример использования:
     *                     schedulerService.scheduleEvent(bot, chatId, () -> {
     *                     SendMessage msg = new SendMessage(chatId, "Привет!");
     *                     bot.execute(msg);
     *                     }, 10);
     */
    public void scheduleEvent(TelegramLongPollingBot bot, Long chatId, Runnable event, int delaySeconds) {
        log.debug("ArrayListSchedulerService: Планирование события через {} секунд для chatId={}", delaySeconds, chatId);

        scheduler.schedule(() -> {
            try {
                event.run();
                log.debug("ArrayListSchedulerService: Событие выполнено для chatId={}", chatId);
            } catch (Exception e) {
                log.error("ArrayListSchedulerService: Ошибка выполнения события для chatId={}", chatId, e);
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * Планирует отправку сообщения через заданное количество секунд.
     * <p>
     * Удобный метод для планирования отправки сообщений с задержкой.
     *
     * @param bot          — TelegramLongPollingBot для отправки сообщений
     * @param message      — сообщение для отправки
     * @param delaySeconds — задержка в секундах
     */
    public void scheduleMessage(TelegramLongPollingBot bot, SendMessage message, int delaySeconds) {
        log.debug("ArrayListSchedulerService: Планирование отправки сообщения через {} секунд", delaySeconds);

        scheduler.schedule(() -> {
            try {
                bot.execute(message);
                log.debug("ArrayListSchedulerService: Запланированное сообщение отправлено");
            } catch (TelegramApiException e) {
                log.error("ArrayListSchedulerService: Ошибка отправки запланированного сообщения", e);
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }


    /**
     * Планирует удаление сообщения через заданное количество секунд.
     * <p>
     * Этот метод полезен для:
     * - Автоматического удаления временных сообщений
     * - Создания эффекта "самоуничтожающихся" сообщений
     * - Очистки чата от устаревшей информации
     * <p>
     * Пример использования:
     * 
     * // Отправляем сообщение и сразу планируем его удаление
     * SendMessage tempMessage = new SendMessage(chatId, "Временное сообщение");
     * Message sentMsg = bot.execute(tempMessage);
     * schedulerService.scheduleMessageDeletion(bot, chatId, sentMsg.getMessageId(), 10);
     * 
     * // Через 10 секунд сообщение автоматически удалится
     *
     * @param bot          — TelegramLongPollingBot для удаления сообщений
     * @param chatId       — ID чата
     * @param messageId    — ID сообщения для удаления
     * @param delaySeconds — задержка в секундах
     */
    public void scheduleMessageDeletion(TelegramLongPollingBot bot, Long chatId, Integer messageId, int delaySeconds) {
        log.debug("ArrayListSchedulerService: Планирование удаления сообщения {} через {} секунд", messageId, delaySeconds);

        scheduler.schedule(() -> {
            try {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId.toString());
                deleteMessage.setMessageId(messageId);
                bot.execute(deleteMessage);
                log.debug("ArrayListSchedulerService: Сообщение {} удалено", messageId);
            } catch (TelegramApiException e) {
                log.error("ArrayListSchedulerService: Ошибка удаления сообщения {}", messageId, e);
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * Создает сложную последовательность событий с заданными интервалами.
     * <p>
     * Этот метод позволяет создавать сложные сценарии с множественными событиями.
     * Каждое событие выполняется через определенный интервал от начала последовательности.
     * <p>
     * Пример использования:
     * 
     * // Создаем последовательность событий для боевой сцены
     * ScheduledEvent[] battleSequence = {
     *     new ScheduledEvent(() -> sendWarning(bot, chatId), 0),      // Сразу
     *     new ScheduledEvent(() -> sendEnemyPhoto(bot, chatId), 3),    // Через 3 сек
     *     new ScheduledEvent(() -> sendAttack(bot, chatId), 6),        // Через 6 сек
     *     new ScheduledEvent(() -> sendResult(bot, chatId), 9)         // Через 9 сек
     * };
     * 
     * schedulerService.createEventSequence(bot, chatId, battleSequence);
     * 
     * // Все события выполнятся автоматически с нужными интервалами
     *
     * @param bot    — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     * @param events — массив событий с задержками
     */
    public void createEventSequence(TelegramLongPollingBot bot, Long chatId, ScheduledEvent... events) {
        log.debug("ArrayListSchedulerService: Создание последовательности из {} событий для chatId={}", events.length, chatId);

        for (ScheduledEvent event : events) {
            scheduler.schedule(() -> {
                try {
                    event.execute(bot, chatId);
                    log.debug("ArrayListSchedulerService: Событие выполнено в последовательности");
                } catch (Exception e) {
                    log.error("ArrayListSchedulerService: Ошибка выполнения события в последовательности", e);
                }
            }, event.getDelaySeconds(), TimeUnit.SECONDS);
        }
    }

    /**
     * Останавливает планировщик и освобождает ресурсы.
     * <p>
     * Этот метод должен вызываться при завершении работы приложения
     * для корректного освобождения ресурсов планировщика.
     * <p>
     * Пример использования:
     * 
     * // В @PreDestroy методе или при завершении приложения
     * @PreDestroy
     * public void cleanup() {
     *     schedulerService.shutdown();
     * }
     * 
     * // Или при получении сигнала завершения
     * Runtime.getRuntime().addShutdownHook(new Thread(() -> {
     *     schedulerService.shutdown();
     * }));
     */
    public void shutdown() {
        log.info("ArrayListSchedulerService: Остановка планировщика");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Внутренний класс для представления запланированного события.
     * <p>
     * Этот класс инкапсулирует событие и его задержку для использования
     * в методах создания последовательностей событий.
     * <p>
     * Пример создания события:
     * 
     * ScheduledEvent event = new ScheduledEvent(() -> {
     *     try {
     *         SendMessage message = new SendMessage(chatId, "Привет!");
     *         bot.execute(message);
     *     } catch (TelegramApiException e) {
     *         log.error("Ошибка отправки", e);
     *     }
     * }, 5); // Выполнится через 5 секунд
     * 
     * // Использование в последовательности:
     * ScheduledEvent[] events = {
     *     new ScheduledEvent(() -> sendMessage1(bot, chatId), 0),
     *     new ScheduledEvent(() -> sendMessage2(bot, chatId), 3),
     *     new ScheduledEvent(() -> sendMessage3(bot, chatId), 6)
     * };
     * 
     * schedulerService.createEventSequence(bot, chatId, events);
     */
    public static class ScheduledEvent {
        private final Runnable event;
        private final int delaySeconds;

        /**
         * Создает новое запланированное событие.
         * 
         * @param event — лямбда-функция для выполнения
         * @param delaySeconds — задержка в секундах от начала последовательности
         */
        public ScheduledEvent(Runnable event, int delaySeconds) {
            this.event = event;
            this.delaySeconds = delaySeconds;
        }

        /**
         * Выполняет событие.
         * 
         * @param bot — TelegramLongPollingBot для отправки сообщений
         * @param chatId — ID чата пользователя
         */
        public void execute(TelegramLongPollingBot bot, Long chatId) {
            event.run();
        }

        /**
         * Получает задержку события в секундах.
         * 
         * @return int — задержка в секундах
         */
        public int getDelaySeconds() {
            return delaySeconds;
        }
    }

} 