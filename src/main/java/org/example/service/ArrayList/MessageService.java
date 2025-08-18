package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bot.KeyboardService.KeyboardReam;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;
import org.example.service.PhotoService.PhotoReam;
import org.example.bot.KeyboardService.KeyboardReam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Map;

/**
 * MessageService — универсальный сервис для создания всех типов сообщений в ArrayList.
 * 
 * 🎯 ЧТО ДЕЛАЕТ ЭТОТ КЛАСС:
 * - Создает боевые сообщения (try-catch, анализ, вставка)
 * - Создает сообщения с результатами и наградами
 * - Создает сообщения для раунда 2
 * - Создает сообщения с клавиатурами
 * 
 * 🧠 КАК РАБОТАЕТ ПОД КАПОТОМ:
 * 1. Получает запрос на создание сообщения
 * 2. Создает объект SendMessage с нужным текстом и форматированием
 * 3. Возвращает готовое сообщение для отправки
 * 
 * 🔗 СВЯЗИ С ДРУГИМИ КЛАССАМИ:
 * - UserService — для получения данных пользователей
 * - StatService — для работы со статами персонажей
 * - StatsDisplayService — для отображения статистики
 * - PhotoReam — для создания фото-сообщений
 * - KeyboardReam — для создания клавиатур
 * 
 * 💀 АВТОР: Иларион (который знает, что сообщения должны быть читаемыми, а не как шифровка от ФСБ)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final UserService userService;
    private final StatService statService;
    private final StatsDisplayService statsDisplayService;

    // ===== БОЕВЫЕ СООБЩЕНИЯ (РАУНД 1) =====

    /**
     * Создает сообщение о попытке защиты try-catch.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, как пользователь пытался защититься
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, показывает код try-catch и результат
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, что его защита не сработала (как обычно в жизни)
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о защите try-catch
     */
    public SendMessage createTryCatchDefenseMessage(Long chatId) {
        log.debug("MessageService: Создание сообщения о защите try-catch для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🛡 **БЛОКИРОВАТЬ (TRY-CATCH)**\n" +
                "*Нанесен урон Aррейну 100(-50)*\n\n" +
                "Ты быстро строишь защитную стену:\n\n" +
                "```java\n" +
                "try {\n" +
                "    list.add(0, \"💀BUG\");\n" +
                "    // Пытаемся защититься от атаки\n" +
                "} catch (IndexOutOfBoundsException e) {\n" +
                "    System.out.println(\"Поймал баг!\");\n" +
                "}\n" +
                "```\n\n" +
                "_Но Аррейн не из тех, кто уважает чужие перехваты!_\n\n" +
                "💥 **Аррейн пробивает твою защиту:**\n" +
                "```\n" +
                "Exception in thread \"main\":\n" +
                "ArrayIndexOutOfBoundsException: Index 0 out of bounds\n" +
                "```\n\n" +
                "🐞 **ПОЛУЧЕН БАГ** - _Ошибка ушла в отпуск, но обещала вернуться к дедлайну._");

        log.debug("MessageService: Сообщение о защите try-catch создано");
        return sendMessage;
    }

    /**
     * Создает сообщение о попытке анализа и уклона.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, как пользователь анализировал и уклонялся
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, показывает код анализа
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, что анализ — это хорошо, но не всегда помогает
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение об анализе и уклонении
     */
    public SendMessage createAnalysisAndDodgeMessage(Long chatId) {
        log.debug("MessageService: Создание сообщения об анализе и уклонении для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🔍 **УКЛОНИТЬСЯ И ПРОАНАЛИЗИРОВАТЬ**\n" +
                "*Нанесен урон Aррейну 80(-20)*\n\n" +
                "Ты ловко уклоняешься и анализируешь ситуацию:\n\n" +
                "```java\n" +
                "// Анализируем размер списка перед добавлением\n" +
                "if (list.size() > 0) {\n" +
                "    list.add(0, \"💀BUG\");\n" +
                "    System.out.println(\"Анализ показал, что можно добавить!\");\n" +
                "} else {\n" +
                "    System.out.println(\"Список пуст, добавляем в конец\");\n" +
                "    list.add(\"💀BUG\");\n" +
                "}\n" +
                "```\n\n" +
                "_Твой анализ помог избежать ошибки!_\n\n" +
                "✅ **УСПЕШНЫЙ АНАЛИЗ** - _Иногда думать полезнее, чем просто кодить._");

        log.debug("MessageService: Сообщение об анализе и уклонении создано");
        return sendMessage;
    }

    /**
     * Создает сообщение о попытке вставки в начало списка.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, как пользователь пытался вставить элемент в начало
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, показывает код вставки
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, что вставка в начало — это рискованно, но может сработать
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о вставке в начало
     */
    public SendMessage createInsertBeginningMessage(Long chatId) {
        log.debug("MessageService: Создание сообщения о вставке в начало для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("📜 **ПОЛУЧИТЬ БОЕВОЙ СВИТОК**\n" +
                "*Нанесен урон Aррейну 120(+20)*\n\n" +
                "Ты используешь боевой свиток — вставку в начало:\n\n" +
                "```java\n" +
                "// Сначала увеличиваем емкость, потом вставляем\n" +
                "list.ensureCapacity(list.size() + 1);\n" +
                "list.add(0, \"💀BUG\");\n" +
                "System.out.println(\"Вставка в начало успешна!\");\n" +
                "```\n\n" +
                "_Твой боевой свиток сработал на ура!_\n\n" +
                "⚔️ **УСПЕШНАЯ ВСТАВКА** - _Иногда нужно рискнуть, чтобы победить._");

        log.debug("MessageService: Сообщение о вставке в начало создано");
        return sendMessage;
    }

    // ===== СООБЩЕНИЯ С РЕЗУЛЬТАТАМИ =====

    /**
     * Создает сообщение с результатом боя и наградами.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, что получил пользователь за бой
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, показывает награды
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, что он получил за свои старания
     * 
     * @param chatId — ID чата пользователя
     * @param experience — полученный опыт
     * @param money — полученные деньги
     * @return SendMessage — сообщение с результатами боя
     */
    public SendMessage createBattleResultMessage(Long chatId, int experience, int money) {
        log.debug("MessageService: Создание сообщения с результатами боя для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🎉 **БОЙ ЗАВЕРШЕН!**\n\n" +
                "Ты успешно сражался с Aррейном и получил:\n\n" +
                "⭐️ **Опыт:** +" + experience + "\n" +
                "💰 **Деньги:** +" + money + "\n\n" +
                "_Продолжай в том же духе, и ты станешь настоящим мастером ArrayList!_");

        log.debug("MessageService: Сообщение с результатами боя создано");
        return sendMessage;
    }

    /**
     * Создает сообщение с результатом вставки в начало и изменениями статов.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, как изменились статы персонажа
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, показывает изменения статов
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, как изменились его характеристики
     * 
     * @param chatId — ID чата пользователя
     * @param statChanges — карта изменений статов
     * @return SendMessage — сообщение с изменениями статов
     */
    public SendMessage createInsertBeginningResultMessage(Long chatId, Map<String, Integer> statChanges) {
        log.debug("MessageService: Создание сообщения с изменениями статов для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🎯 **РЕЗУЛЬТАТ ВСТАВКИ В НАЧАЛО**\n\n" +
                "Твоя вставка в начало ArrayList изменила статы персонажа:\n\n" +
                statChanges.entrySet().stream()
                    .map(entry -> {
                        String statName = getStatDisplayName(entry.getKey());
                        int value = entry.getValue();
                        String emoji = value > 0 ? "📈" : "📉";
                        return emoji + " **" + statName + ":** " + (value > 0 ? "+" : "") + value;
                    })
                    .reduce("", (a, b) -> a + b + "\n") +
                "\n_Твои навыки растут с каждым боем!_");

        log.debug("MessageService: Сообщение с изменениями статов создано");
        return sendMessage;
    }

    /**
     * Создает сообщение о завершении раунда.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, что раунд завершен
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, что раунд закончился
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о завершении раунда
     */
    public SendMessage createRoundCompleteMessage(Long chatId) {
        log.debug("MessageService: Создание сообщения о завершении раунда для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🏁 **РАУНД ЗАВЕРШЕН!**\n\n" +
                "Ты успешно завершил раунд изучения ArrayList!\n\n" +
                "_Готов к следующему вызову? Итераториус ждет тебя!_");

        log.debug("MessageService: Сообщение о завершении раунда создано");
        return sendMessage;
    }

    // ===== СООБЩЕНИЯ ДЛЯ РАУНДА 2 =====

    /**
     * Создает фото-сообщение с объявлением раунда 2.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает фото с подписью о раунде 2 и статистикой персонажа
     * 🧠 КАК РАБОТАЕТ: Получает данные пользователя, строит строку статов, создает фото-сообщение
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь увидел свои текущие характеристики перед раундом 2
     * 
     * @param chatId — ID чата пользователя
     * @return SendPhoto — фото-сообщение о раунде 2
     */
    public SendPhoto createRound2Message(Long chatId) {
        log.debug("MessageService: Создание фото-сообщения о раунде 2 для chatId={}", chatId);

        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("MessageService: Пользователь или персонаж не найден для раунда 2 для chatId={}", chatId);
            return PhotoReam.createRound2PhotoMessage(chatId, "Ошибка: персонаж не найден.");
        }

        PersonageEntity entity = user.getPersonage();
        
        // Логируем детали пользователя и персонажа для отладки
        log.debug("MessageService: Пользователь найден: tgId={}, персонаж: type={}, name={}", 
                user.getTgId(), entity.getCharacterType(), entity.getName());
        
        String statsLine = statsDisplayService.buildStatsLine(entity);
        log.debug("MessageService: Строка статов получена: {}", statsLine);

        log.debug("MessageService: Фото-сообщение о раунде 2 создано");
        return PhotoReam.createRound2PhotoMessage(chatId, statsLine);
    }

    /**
     * Создает вступительную реплику Итераториуса для раунда 2.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение, которое готовит пользователя к раунду 2
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, использует многострочный текст
     * 💀 ЗАЧЕМ НУЖНО: Чтобы создать атмосферу и подготовить пользователя к серьезному испытанию
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — вступительная реплика Итераториуса
     */
    public SendMessage createIteratoriusRound2Intro(Long chatId) {
        log.debug("MessageService: Создание вступительной реплики Итераториуса для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);

        sendMessage.setText("""
                *Итераториус:*
                 _"Неплохо справился с первым испытанием! Но это была лишь разминка..."_
                
                 _"Раунд 2 покажет, действительно ли ты понял принципы работы ArrayList._
                 _Противники стали хитрее, а ставки — выше."_
                
                 _"Помни: знание структуры данных — твое главное оружие!"_
                
                 ⚔️ **Приготовься к настоящему испытанию!**
                """);

        return sendMessage;
    }

    // ===== МЕТОДЫ ДЛЯ РАУНДА 2 (КОМАНДЫ) =====

    /**
     * Создает сообщение для команды "Добить".
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, что пользователь добивает противника
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, показывает результат добивания
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, что его добивание сработало
     * 
     * @param bot — Telegram бот
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о добивании
     */
    public SendMessage messageFinishOff(TelegramLongPollingBot bot, Long chatId) {
        log.info("MessageService: ☠️ Добить — повторить ад! запущен метод для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("☠️ **ДОБИТЬ — ПОВТОРИТЬ АД!**\n\n" +
                "Ты безжалостно добиваешь Aррейна:\n\n" +
                "```java\n" +
                "// Добиваем противника до конца\n" +
                "while (list.size() > 0) {\n" +
                "    list.remove(0); // Убираем элементы по одному\n" +
                "    System.out.println(\"Элемент удален!\");\n" +
                "}\n" +
                "```");

        return sendMessage;
    }

    /**
     * Создает сообщение для команды ".ensureCapacity()".
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, как пользователь использует ensureCapacity
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, показывает код использования
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, как работает ensureCapacity
     * 
     * @param bot — Telegram бот
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение об ensureCapacity
     */
    public SendMessage messageEnsureCapacityRound2(TelegramLongPollingBot bot, Long chatId) {
        log.info("MessageService: 🧠 .ensureCapacity() запущен метод для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🧠.ensureCapacity() — взломать изнутри.\n" +
                "🔥 _Нанесен урон Аррейну 30(-70)_ \n\n" +
                "Ты вводишь команду, которой пользуются раз в пятилетку:\n" +
                "```arrayList.ensureCapacity(50);```\n" +
                "Аррейон корчится, его структура не выдерживает...\n\n" +
                "🐞*Рандомный баг:* На проде эта фича сломалась бы в пятницу.");

        return sendMessage;
    }

    /**
     * Создает ответ Аррейна на ensureCapacity.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает ответ противника на использование ensureCapacity
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown
     * 💀 ЗАЧЕМ НУЖНО: Чтобы противник показал свою реакцию
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — ответ Аррейна
     */
    public SendMessage createArreyonEnsureCapacityResponse(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Аррейн*\n" +
                "Не-е-ет! Только не резерв… Это мой ночной кошмар...");
        return sendMessage;
    }

    /**
     * Создает ответ Итераториуса на ensureCapacity.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает ответ наставника с наградами
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, показывает награды
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь получил награды за понимание
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — ответ Итераториуса
     */
    public SendMessage createIteratoriusEnsureCapacityResponse(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус*\n" +
                "Ты сделал это... Малец, ты… ты не просто вызываешь методы — ты Понимаешь их.\n" +
                "А это... уже не шутки. Это... архитектура, мать её.\n\n" +
                "*Навык повышен:*\n" +
                "  +80 ⭐️ к Очкам Достижения\n" +
                "  +300 💲 к Деньгам\n\n" +
                "🧠 *Мысль дня:* _Оптимизация — это когда код работает быстро, а не когда программист работает быстро._");
        return sendMessage;
    }

    /**
     * Создает сообщение для команды "Кофе пауза".
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о кофе-паузе
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, что началась кофе-пауза
     * 
     * @param bot — Telegram бот
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о кофе-паузе
     */
    public SendMessage messageCoffeRound2(TelegramLongPollingBot bot, Long chatId) {
        log.info("MessageService: ☕ Запуск кофе-сценария для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("☕ Кофе пауза — восстановить энергию(или нет)");

        return sendMessage;
    }

    /**
     * Создает сообщение с результатом try-catch действия.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, что получил пользователь за try-catch
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, показывает награды
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, что он получил за свою защиту
     * 
     * @param chatId — ID чата пользователя
     * @param expReward — полученный опыт
     * @param cashReward — полученные деньги
     * @return SendMessage — сообщение с результатами try-catch
     */
    public SendMessage createTryCatchResultMessage(Long chatId, int expReward, int cashReward) {
        log.debug("MessageService: Создание сообщения с результатом try-catch для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🛡 **РЕЗУЛЬТАТ TRY-CATCH**\n\n" +
                "Твоя защита try-catch принесла награды:\n\n" +
                "⭐️ **Опыт:** +" + expReward + "\n" +
                "💰 **Деньги:** +" + cashReward + "\n\n" +
                "_Иногда лучше защищаться, чем атаковать!_");

        return sendMessage;
    }

    /**
     * Создает сообщение с результатом анализа.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, что получил пользователь за анализ
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown, показывает награды
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, что он получил за свой анализ
     * 
     * @param chatId — ID чата пользователя
     * @param expReward — полученный опыт
     * @param cashReward — полученные деньги
     * @return SendMessage — сообщение с результатами анализа
     */
    public SendMessage createAnalysisResultMessage(Long chatId, int expReward, int cashReward) {
        log.debug("MessageService: Создание сообщения с результатом анализа для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🔍 **РЕЗУЛЬТАТ АНАЛИЗА**\n\n" +
                "Твой анализ принес награды:\n\n" +
                "⭐️ **Опыт:** +" + expReward + "\n" +
                "💰 **Деньги:** +" + cashReward + "\n\n" +
                "_Думать — это полезно и прибыльно!_");

        return sendMessage;
    }

    /**
     * Создает сообщение о завершении раунда 1.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Создает сообщение о том, что раунд 1 завершен
     * 🧠 КАК РАБОТАЕТ: Форматирует текст с Markdown
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понял, что раунд 1 закончился
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о завершении раунда 1
     */
    public SendMessage endOfRoundOne(Long chatId) {
        log.debug("MessageService: Создание сообщения о завершении раунда 1 для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🏁 **РАУНД 1 ЗАВЕРШЕН!**\n\n" +
                "Ты успешно завершил первый раунд изучения ArrayList!\n\n" +
                "_Готов к следующему вызову? Итераториус ждет тебя!_");

        return sendMessage;
    }

    /**
     * Создает сообщение с атакой.
     *
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение с атакой
     */
    public SendMessage createAttackMessage(Long chatId) {
        log.debug("MessageService: Создание сообщения с атакой для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("💥 _Аррейн бросает в тебя виртуальный элемент с индексом 0!_\n\n" +
                "```java\n" +
                "ArrayList<String> list = new ArrayList<>();\n" +
                "list.add(\"Bug\");  // <- ВОТ ЭТО ЛЕТИТ В ТЕБЯ!\n" +
                "list.add(\"Error\");\n" +
                "list.add(\"Exception\");\n\n" +
                "```" +
                "🧠 *Итераториус*_(шепчет_): Аррейн пытается добавить баг в начало списка!\n" +
                "У тебя есть доля секунды. Реагируй!");
        
        // Добавляем клавиатуру с кнопками боевых действий
        sendMessage.setReplyMarkup(KeyboardReam.BattlArreyn(chatId));

        log.debug("MessageService: Сообщение с атакой создано");
        return sendMessage;
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    /**
     * Преобразует техническое название стата в читаемое название.
     * 
     * 🎯 ЧТО ДЕЛАЕТ: Переводит технические названия статов в понятные пользователю
     * 🧠 КАК РАБОТАЕТ: Использует switch для сопоставления названий
     * 💀 ЗАЧЕМ НУЖНО: Чтобы пользователь понимал, какие именно характеристики изменились
     * 
     * @param statKey — техническое название стата
     * @return String — читаемое название стата
     */
    private String getStatDisplayName(String statKey) {
        return switch (statKey) {
            case "achievement_points" -> "Очки Достижения";
            case "currency" -> "Деньги";
            case "experience" -> "Опыт";
            case "health" -> "Здоровье";
            case "strength" -> "Сила";
            case "intelligence" -> "Интеллект";
            case "agility" -> "Ловкость";
            default -> statKey; // Если не знаем, возвращаем как есть
        };
    }
}
