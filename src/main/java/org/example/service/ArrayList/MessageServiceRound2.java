package org.example.service.ArrayList;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bot.KeyboardService.KeyboardReam;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;
import org.example.service.PersonageStatManager;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.example.service.PhotoService.PhotoReam;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 🎮 СЕРВИС СООБЩЕНИЙ ДЛЯ РАУНДА 2
 * <p>
 * Этот класс отвечает за создание всех сообщений раунда 2:
 * - Начало раунда 2 (перенесено из ArrayListTheoryService)
 * - Реплики персонажей в раунде 2
 * - Вывод статов персонажа
 * - Боевые сообщения раунда 2
 * <p>
 * 🎯 ЦЕЛЬ: Централизовать все сообщения раунда 2 в одном месте
 * для упрощения разработки и поддержки.
 * <p>
 * 📝 ПРИНЦИПЫ:
 * - Простота и читаемость кода
 * - Переиспользование логики (buildStatsLine)
 * - Подробные комментарии
 * - Один класс = один раунд
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageServiceRound2 {
    private final UserService userService;
    private final ArrayListTheoryService arrayListTheoryService;
    private final PersonageStatManager statManager;
    private final QuizService quizService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    /**
     * Создает фото-сообщение с объявлением раунда 2.
     *
     * @param chatId — ID чата пользователя
     * @return SendPhoto — фото-сообщение о раунде 2 с подписью
     */
    public SendPhoto createRound2Message(Long chatId) {
        log.debug("MessageServiceRound2: Создание фото-сообщения о раунде 2 для chatId={}", chatId);

        UserEntity user = userService.getUserByTgId(chatId);

        // Получаем пользователя и персонажа проверяем что он не null
        if (user == null || user.getPersonage() == null) {
            log.warn("MessageServiceRound2: Пользователь или персонаж не найден для раунда 2 для chatId={}", chatId);
            // В случае ошибки возвращаем фото без статов
            return PhotoReam.createRound2PhotoMessage(chatId, "Ошибка: персонаж не найден.");
        }

        //получаем нашего персонажа из БД
        PersonageEntity entity = user.getPersonage();

        // Формируем строку статов
        String statsLine = buildStatsLine(entity);

        log.debug("MessageServiceRound2: Фото-сообщение о раунде 2 создано");
        return PhotoReam.createRound2PhotoMessage(chatId, statsLine);
    }

    /**
     * 💬 РЕПЛИКА ИТЕРАТОРИУСА ПЕРЕД РАУНДОМ 2
     * <p>
     * Дополнительное сообщение с репликой от Итераториуса о сложности раунда 2.
     */
    public SendMessage createIteratoriusRound2Intro(Long chatId) {
        log.debug("MessageServiceRound2: Создание вступительной реплики Итераториуса для chatId={}", chatId);

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


    public SendMessage sendMessageText2Rond(Long chatId) {
        log.debug("sendMessageText2Rond: Создание смс для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);

        sendMessage.setText("""
                Атмосфера сгустилась. Аррейн дрожит, индексы плавают, память утекает как отпускные в июле.
                🔥 Его тело дергается — начинается беспорядочный .resize()
                """);

        return sendMessage;
    }


    public SendMessage messageArreyon2Rond(Long chatId) {
        log.debug("messageArreyon2Rond Отправка смс от Аррейна для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);
        sendMessage.setText("""
                *Аррейн*
                 Ты реально ломаешь меня… Не думал, что кто-то пойдёт так далеко… \n
                 Надеюсь, у тебя есть план “Б” — и психолог. 
                """);
        return sendMessage;
    }

    public SendMessage messageIteratorius2Rond(Long chatId) {
        log.debug("messageIteratorius2Rond: Создание смс для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);
        sendMessage.setText("*Итераториус* \n" +
                "Он готов рухнуть! Ударь, пока GC не пришёл и не начал собирать остатки твоей мотивации!\n\n" +
                "\uD83C\uDFAE Твои действия?\n" +
                "\n" +
                "☠\uFE0F Добить — повторить ад!\n" +
                "\n" +
                "\uD83E\uDDE0 .ensureCapacity() — взломать изнутри\n" +
                "\n" +
                "\uD83D\uDCE6 Кофе пауза — восстановить энергию(или нет)"
        );
        sendMessage.setReplyMarkup(KeyboardReam.BattlArreynRound2(chatId));
        return sendMessage;
    }

    public SendMessage messageFinishOff(Long chatId) {
        log.info("MessageServiceRound2[messageFinishOff] ☠️ *Добить — повторить ад! запущен метод для chatId={}", chatId);
        // 🎯 КАСТОМНЫЕ ДИАПАЗОНЫ НАГРАД!
        PersonageStatManager.StatUpdateResult result = statManager.updateRandomRewards(
                chatId,
                40,   // expMin - минимальный опыт
                80,   // expMax - максимальный опыт
                300,  // cashMin - минимальные деньги
                450  // cashMax - максимальные деньги
        );
        // Если ошибка - возвращаем сообщение об ошибке
        if (!result.isSuccess()) {
            return statManager.createErrorMessage(chatId, result.getErrorMessage());
        }

        // Создаем сообщение с фактическими наградами
        SendMessage message = new SendMessage(chatId.toString(),
                "☠️ *Добить — повторить ад!*\n\n" +
                        "💥 Ты добиваешь Аррейона повторной вставкой в начало.\n" +
                        "Resize рушится. Структура разваливается на фрагменты.\n\n" +
                        "*Навык повышен:*\n" +
                        "  +" + result.getExpReward() + " ⭐️ к Очкам Достижения\n" +
                        "  +" + result.getCashReward() + " 💲 к Деньгам\n\n" +
                        "🐞 *Рандомный баг:* \"Ошибка залогирована… теперь её никто не найдёт, но все будут материться.\"\n"
        );
        message.setParseMode("Markdown");
        return message;
    }

    public SendMessage messageEnsureCapacityRound2(Long chatId) {
        log.info("MessageServiceRound2[messageEnsureCapacityRound2] \uD83E\uDDE0.ensureCapacity() запущен метод для chatId={}", chatId);
        PersonageStatManager.StatUpdateResult result = statManager.updateRandomRewards(
                chatId,
                40,   // expMin - минимальный опыт
                80,   // expMax - максимальный опыт
                300,  // cashMin - минимальные деньги
                450  // cashMax - максимальные деньги
        );
        // Если ошибка - возвращаем сообщение об ошибке
        if (!result.isSuccess()) {
            return statManager.createErrorMessage(chatId, result.getErrorMessage());
        }
        // Создаем сообщение с фактическими наградами
        SendMessage message = new SendMessage(chatId.toString(),
                "\uD83E\uDDE0.ensureCapacity() — взломать изнутри\n" +
                        "Ты вводишь команду, которой пользуются раз в пятилетку:\n" +
                        "\n" +
                        "```arrayList.ensureCapacity(50);```\n" +
                        "\n" +
                        "Аррейон корчится, его структура не выдерживает... \n" +
                        "*Навык повышен:*\n" +
                        "  +" + result.getExpReward() + " ⭐️ к Очкам Достижения\n" +
                        "  +" + result.getCashReward() + " 💲 к Деньгам\n\n" +
                        "🐞*Рандомный баг:* На проде эта фича сломалась бы в пятницу.\n"
        );
        message.setParseMode("Markdown");
        return message;
    }

    /**
     * ☕ ЗАПУСКАЕТ КОФЕ-СЦЕНАРИЙ: ФОТО → ВИКТОРИНА → ОТВЕТ ИТЕРАТОРИУСА
     * 
     * СЦЕНАРИЙ:
     * 1. Отправляет фото с кодом и подписью (PhotoReam.quizCoffeRound2)
     * 2. Через 3 сек отправляет викторину с 4 вариантами ответов
     * 3. После ответа пользователя — Итераториус отвечает вашими сообщениями
     * 4. Автоматически начисляет/снимает очки и сохраняет в БД
     * 
     * @param bot — Telegram бот для отправки
     * @param chatId — ID чата пользователя
     */
    public  void messageCoffeRound2(TelegramLongPollingBot bot, Long chatId) {
        log.info("MessageServiceRound2[messageCoffeRound2] ☕ Запуск кофе-сценария для chatId={}", chatId);
        
        try {
            // 1. Отправляем ваше готовое фото с кодом и подписью
            SendPhoto coffeePhoto = PhotoReam.quizCoffeRound2(chatId);
            bot.execute(coffeePhoto);
            
            log.info("MessageServiceRound2: ☕ Фото с кодом отправлено для chatId={}", chatId);
            
            // 2. Через 3 секунды отправляем викторину (пользователь успеет рассмотреть код)
            scheduler.schedule(() -> {
                try {
                    SendPoll coffeeQuiz = quizService.createCoffeeQuiz(chatId);
                    bot.execute(coffeeQuiz);
                    
                    log.info("MessageServiceRound2: ☕ Кофе-викторина отправлена для chatId={}", chatId);
                    
                } catch (TelegramApiException e) {
                    log.error("MessageServiceRound2: Ошибка отправки кофе-викторины для chatId={}: {}", chatId, e.getMessage());
                }
            }, 3, TimeUnit.SECONDS);
            
        } catch (TelegramApiException e) {
            log.error("MessageServiceRound2: Ошибка отправки фото для кофе-сценария chatId={}: {}", chatId, e.getMessage());
        }
    }

    /**
     * 🚀 ЗАПУСК ПОЛНОЙ ЦЕПОЧКИ РАУНДА 2 (НАДЕЖНАЯ ВЕРСИЯ)
     * 
     * ПОСЛЕДОВАТЕЛЬНОСТЬ:
     * 1. Фото с объявлением раунда 2 и статами (сразу)
     * 2. Через 5 сек → sendMessageText2Rond
     * 3. Через 8 сек → messageArreyon2Rond  
     * 4. Через 12 сек → messageIteratorius2Rond
     * 
     * Используется собственный scheduler для надежности.
     * 
     * @param bot — Telegram бот для отправки
     * @param chatId — ID чата пользователя
     */
    public void startRound2Sequence(TelegramLongPollingBot bot, Long chatId) {
        log.info("MessageServiceRound2[startRound2Sequence] 🚀 Запуск цепочки раунда 2 для chatId={}", chatId);
        
        try {
            // 1. СРАЗУ: Отправляем фото с объявлением раунда 2
            SendPhoto round2Photo = this.createRound2Message(chatId);
            bot.execute(round2Photo);
            log.info("MessageServiceRound2: ✅ Фото раунда 2 отправлено для chatId={}", chatId);
            
            // 2. ЧЕРЕЗ 5 СЕК: sendMessageText2Rond
            scheduler.schedule(() -> {
                try {
                    SendMessage textMessage = this.sendMessageText2Rond(chatId);
                    bot.execute(textMessage);
                    log.info("MessageServiceRound2: ✅ sendMessageText2Rond отправлено для chatId={}", chatId);
                } catch (Exception e) {
                    log.error("MessageServiceRound2: ❌ Ошибка sendMessageText2Rond для chatId={}: {}", chatId, e.getMessage(), e);
                }
            }, 5, TimeUnit.SECONDS);
            
            // 3. ЧЕРЕЗ 8 СЕК: messageArreyon2Rond
            scheduler.schedule(() -> {
                try {
                    SendMessage arreyonMessage = this.messageArreyon2Rond(chatId);
                    bot.execute(arreyonMessage);
                    log.info("MessageServiceRound2: ✅ messageArreyon2Rond отправлено для chatId={}", chatId);
                } catch (Exception e) {
                    log.error("MessageServiceRound2: ❌ Ошибка messageArreyon2Rond для chatId={}: {}", chatId, e.getMessage(), e);
                }
            }, 8, TimeUnit.SECONDS);
            
            // 4. ЧЕРЕЗ 12 СЕК: messageIteratorius2Rond
            scheduler.schedule(() -> {
                try {
                    SendMessage iteratoriusMessage = this.messageIteratorius2Rond(chatId);
                    bot.execute(iteratoriusMessage);
                    log.info("MessageServiceRound2: ✅ messageIteratorius2Rond отправлено для chatId={}", chatId);
                } catch (Exception e) {
                    log.error("MessageServiceRound2: ❌ Ошибка messageIteratorius2Rond для chatId={}: {}", chatId, e.getMessage(), e);
                }
            }, 12, TimeUnit.SECONDS);
            
            log.info("MessageServiceRound2: 🎯 Все задачи раунда 2 запланированы для chatId={}", chatId);
            
        } catch (TelegramApiException e) {
            log.error("MessageServiceRound2: ❌ Критическая ошибка запуска раунда 2 для chatId={}: {}", chatId, e.getMessage(), e);
        }
    }
























    /**
     * 📊 ФОРМИРОВАНИЕ СТРОКИ СТАТИСТИКИ (перенесено из ArrayListTheoryService)
     * <p>
     * Создает читаемую строку с характеристиками персонажа.
     * Метод перенесен без изменений для сохранения совместимости.
     *
     * @param entity — сущность персонажа
     * @return String — отформатированная строка со статистикой
     */
    private String buildStatsLine(PersonageEntity entity) {
        log.debug("MessageServiceRound2: Формирование строки статов для персонажа типа: {}", entity.getCharacterType());

        String type = entity.getCharacterType();
        String money = String.valueOf(entity.getCurrency());
        if (money.endsWith(".0")) money = money.substring(0, money.length() - 2); // убираем .0 если не нужно

        String statsLine;
        switch (type) {
            case "Personage1":
                statsLine = "|🏆Level: " + entity.getLevel() +
                        " |⚡️Энергия: " + entity.getEnergy() +
                        " |⭐️Очки достижения: " + entity.getAchievementPoints() +
                        " |💲Деньги: " + money +
                        " |📊 Аналитика: " + (entity.getAnalytics() != null ? entity.getAnalytics() : 0) +
                        " |🛡 Сопротивление дедлайну: " + (entity.getDeadlineResistance() != null ? entity.getDeadlineResistance() : 0);
                break;
            case "Personage2":
                statsLine = "|🏆Level: " + entity.getLevel() +
                        " |⚡️Энергия: " + entity.getEnergy() +
                        " |⭐️Очки достижения: " + entity.getAchievementPoints() +
                        " |💲Деньги: " + money +
                        " |😁 Юмор: " + (entity.getHumor() != null ? entity.getHumor() : 0) +
                        " |💬 Навыки коммуникации: " + (entity.getCommunication() != null ? entity.getCommunication() : 0);
                break;
            case "Personage3":
                statsLine = "|🏆Level: " + entity.getLevel() +
                        " |⚡️Энергия: " + entity.getEnergy() +
                        " |⭐️Очки достижения: " + entity.getAchievementPoints() +
                        " |💲Деньги: " + money +
                        " |💾 Точность кода: " + (entity.getCodeAccuracy() != null ? entity.getCodeAccuracy() : 0) +
                        " |⚙️ Оптимизация: " + (entity.getOptimization() != null ? entity.getOptimization() : 0);
                break;
            default:
                statsLine = "|⚡️Энергия: " + entity.getEnergy();
                break;
        }

        log.debug("MessageServiceRound2: Строка статов сформирована: {}", statsLine);
        return statsLine;
    }

    private int safe(Integer value) {
        return value != null ? value : 0;
    }


    /**
     * 🏆 СООБЩЕНИЕ О ПОБЕДЕ В РАУНДЕ 2
     * <p>
     * Поздравление игрока с прохождением раунда 2.
     */
    public SendMessage createRound2VictoryMessage(Long chatId) {
        log.debug("MessageServiceRound2: Создание сообщения о победе в раунде 2 для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);

        // Получаем обновленные статы для показа прогресса
        UserEntity user = userService.getUserByTgId(chatId);
        String statsLine = "";
        if (user != null && user.getPersonage() != null) {
            statsLine = "\n" + buildStatsLine(user.getPersonage());
        }

        sendMessage.setText("""
                🎊 **РАУНД 2 ПРОЙДЕН!** 🎊
                
                💬 **Итераториус (впечатлен):**
                _"Превосходно! Ты не просто выжил — ты доминировал!"_
                
                _"Твое понимание ArrayList стало глубже. Теперь ты видишь не только методы, но и их внутреннюю работу."_
                
                _"Аррейн 2.0 был серьезным противником, но ты справился!"_
                %s
                
                🎯 **Готов к еще большим вызовам?**
                """.formatted(statsLine));

        return sendMessage;
    }

}
