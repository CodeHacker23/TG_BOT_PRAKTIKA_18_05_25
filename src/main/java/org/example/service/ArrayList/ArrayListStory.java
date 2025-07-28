package org.example.service.ArrayList;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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
 * - Делегирование обработки в специализированные сервисы
 * - Управление жизненным циклом изучения ArrayList
 * <p>
 * Связи с другими классами:
 * - Использует ArrayListTheoryService для работы с теорией и контентом
 * - Использует ArrayListBattleService для боевых действий
 * - Использует ArrayListSchedulerService для планирования событий
 * - Интегрируется с MessageHandlerService для получения команд
 * <p>
 * Принцип работы:
 * 1. Получает команду от MessageHandlerService
 * 2. Проверяет, может ли обработать команду
 * 3. Делегирует обработку в соответствующий сервис
 * 4. Логирует все действия для отладки
 * <p>
 * Автор: Архитектор (который знает, что маршрутизация — это искусство)
 * <p>
 * Пример использования:
 *
 * @Autowired private ArrayListStory arrayListStory;
 * <p>
 * if (arrayListStory.canHandle(text)) {
 * arrayListStory.handle(bot, message);
 * }
 */
@RequiredArgsConstructor
@Service
public class ArrayListStory {
    private static final Logger log = LoggerFactory.getLogger(ArrayListStory.class);

    // Специализированные сервисы для разных аспектов ArrayList
    private final ArrayListTheoryService theoryService;
    private final ArrayListBattleService battleService;
    private final ArrayListSchedulerService schedulerService;

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
            battleService.processTryCatchAction(bot, msg.getChatId());
        });

        // Команда для боевого действия Анализировать
        commandsMap.put("\uD83D\uDD0D Уклониться \n и \nпроанализировать", (bot, msg) -> {
            log.info("ArrayListStory: Обработка команды '🔍 Уклониться и проанализировать' для chatId={}", msg.getChatId());
            battleService.processAnalysisAction(bot, msg.getChatId());
            schedulerService.answerIteratoriys(bot, msg.getChatId());
        });

        commandsMap.put("\uD83D\uDEA8 Отразить \n" +
                "вставкой \n" +
                " в начало",( bot, msg)->{
            log.info("ArrayListStory: Обработка команды 'Отразить' для chatId={}", msg.getChatId());
            
            try {
                // Отправляем первое сообщение
                SendMessage firstMessage = battleService.InsertBeginning(msg.getChatId());
                bot.execute(firstMessage);
                log.info("ArrayListStory: Первое сообщение отправлено для chatId={}", msg.getChatId());
                
                // Через 3 секунды отправляем сообщение от Итераториуса с изменениями статов
                schedulerService.sendInsertBeginningResult(bot, msg.getChatId());
            } catch (TelegramApiException e) {
                log.error("ArrayListStory: Ошибка отправки первого сообщения для chatId={}", msg.getChatId(), e);
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
     * Запускает полную сюжетную линию ArrayList.
     *
     * Этот метод запускает последовательность событий:
     * 1. Отправка теории с автоудалением через 20 секунд
     * 2. Предупреждение о противнике
     * 3. Фото противника с представлением
     * 4. Начало боевой последовательности
     *
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
//    public void startArrayListStory(TelegramLongPollingBot bot, Long chatId) {
//        log.info("ArrayListStory: Запуск сюжетной линии ArrayList для chatId={}", chatId);
//        schedulerService.sendTheoryWithAutoDelete(bot, chatId);
//    }

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
