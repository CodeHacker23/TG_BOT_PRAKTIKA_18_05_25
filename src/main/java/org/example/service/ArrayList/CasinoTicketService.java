package org.example.service.ArrayList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CasinoTicketService {
    private final UserService userService;           // Для получения юзера из БД по Telegram ID
    private final StatService statService;          // Для изменения статов персонажа (деньги, опыт)
    private final EpicQuizTimerService epicQuizTimerService;


    public SendMessage handleTicketSelection(TelegramLongPollingBot bot, Long chatId, String buttonText) {
        switch (buttonText) {
            case "1⃣" -> {
                return ticket1(chatId);
            }
            case "2⃣" -> {
                return ticket2(chatId);
            }
            case "3⃣" -> {
                return ticket3(chatId);
            }
            case "4⃣" -> {
                return ticket4(chatId);
            }
            case "5⃣" -> {
                return ticket5(chatId);
            }
            case "6⃣" -> {
                return ticket6(chatId);
            }
            case "7⃣" -> {
                return ticket7(chatId);
            }
            case "8⃣" -> {
                return ticket8(chatId);
            }
            case "9⃣" -> {
                return ticket9(bot, chatId);
            }
            case "🔟" -> {
                return ticket10(bot, chatId);
            }
            default -> {
                return new SendMessage(chatId.toString(), "❌ Неизвестный билет!");
            }
        }
    }


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
        
        return message;
    }

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
        
        return message;
    }

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
        
        return message;
    }

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
        
        return message;
    }


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
        
        return message;
    }

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
        
        return message;
    }

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
        
        return message;
    }

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
        
        return message;
    }

    /**
     * 🎲 БИЛЕТ 9: ЭПИЧЕСКАЯ ВИКТОРИНА С ТАЙМЕРОМ
     *
     * ЭТАПЫ БИЛЕТА 9:
     * 1. Показываем информационное сообщение о том что это особый билет
     * 2. Пауза 6 секунд чтобы пользователь прочитал
     * 3. Запускаем эпическую викторину с таймером в отдельном потоке
     *
     * @param bot - объект бота для запуска викторины
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

        log.info("CasinoTicketService: ✅ Информационное сообщение отправлено, викторина запустится через 11 секунд для chatId={}", chatId);
        return infoMessage;
    }

    /**
     * 🕐 ЗАПУСКАЕТ ВИКТОРИНУ С ЗАДЕРЖКОЙ 11 СЕКУНД
     *
     * ЗАЧЕМ ЭТОТ МЕТОД НУЖЕН:
     * - Пользователь нажимает кнопку 9⃣ и СРАЗУ получает информационное сообщение 
     * - Параллельно запускается этот метод в отдельном потоке
     * - Он ждет 11 секунд чтобы пользователь успел прочитать информацию
     * - Только потом запускает саму викторину с таймером
     *
     * ПОЧЕМУ ОТДЕЛЬНЫЙ ПОТОК:
     * - Если бы мы делали Thread.sleep() в основном потоке, бот "завис" бы на 11 секунд
     * - Пользователь не смог бы нажимать другие кнопки, бот не отвечал бы на команды
     * - Отдельный поток позволяет боту работать нормально, пока идет ожидание
     *
     * @param bot - объект бота (нужен для запуска викторины через 11 сек)
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
     *
     * ЭТАПЫ БИЛЕТА 10:
     * 1. Показываем информационное сообщение о том что это ЭКСПЕРТНАЯ викторина
     * 2. Пауза 11 секунд чтобы пользователь прочитал и приготовился  
     * 3. Запускаем СЛОЖНУЮ викторину с таймером в отдельном потоке
     *
     * ОСОБЕННОСТИ ЭКСПЕРТНОЙ ВИКТОРИНЫ:
     * - Вопрос гораздо сложнее чем в билете 9
     * - Увеличенные награды (но пользователь не знает сколько именно)
     * - Увеличенные штрафы (интрига сохраняется)
     * - Требует глубокого понимания ArrayList
     *
     * @param bot - объект бота для запуска викторины через 11 сек
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

        log.info("CasinoTicketService: ✅ Экспертная викторина сообщение отправлено, викторина запустится через 11 секунд для chatId={}", chatId);
        return infoMessage;
    }

    /**
     * 🏆 ЗАПУСКАЕТ ЭКСПЕРТНУЮ ВИКТОРИНУ С ЗАДЕРЖКОЙ 11 СЕКУНД
     *
     * ЗАЧЕМ ОТДЕЛЬНЫЙ МЕТОД ДЛЯ ЭКСПЕРТНОЙ ВИКТОРИНЫ:
     * - Экспертная викторина имеет другой вопрос (сложнее)
     * - Другие награды/штрафы (больше, но пользователь не знает сколько)
     * - Может иметь другое время (пока 30 сек, но можно изменить)
     * - Логика такая же как в ticket9, но данные другие
     *
     * @param bot - объект бота (нужен для запуска викторины через 11 сек)
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

}
