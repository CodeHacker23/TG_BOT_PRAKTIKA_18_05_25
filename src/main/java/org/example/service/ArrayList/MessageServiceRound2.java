package org.example.service.ArrayList;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bot.KeyboardService.KeyboardReam;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;
import org.example.service.PhotoService.PhotoReam;
import org.example.service.ArrayList.QuizService;
import org.example.service.PersonageStatManager;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * 🎮 СЕРВИС СООБЩЕНИЙ ДЛЯ РАУНДА 2 (РЕФАКТОРЕННЫЙ)
 * <p>
 * Этот класс теперь отвечает ТОЛЬКО за создание сообщений раунда 2.
 * Вся сложная логика планирования и сценариев вынесена в отдельные сервисы.
 * <p>
 * 🎯 ЦЕЛЬ: Максимальная простота и читаемость кода
 * <p>
 * 📝 ПРИНЦИПЫ:
 * - Один класс = одна ответственность
 * - Простые методы без сложной логики
 * - Использование специализированных сервисов
 * - Подробные комментарии
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageServiceRound2 {
    
    // Основные сервисы
    private final UserService userService;
    private final ArrayListTheoryService arrayListTheoryService;
    private final PersonageStatManager statManager;
    private final QuizService quizService;
    
    // Новые специализированные сервисы
    private final StatsDisplayService statsDisplayService;
    private final CasinoScenarioService casinoScenarioService;
    private final Round2SequenceService round2SequenceService;
    
    /**
     * Создает фото-сообщение с объявлением раунда 2.
     *
     * @param chatId — ID чата пользователя
     * @return SendPhoto — фото-сообщение о раунде 2 с подписью
     */
    public SendPhoto createRound2Message(Long chatId) {
        log.debug("MessageServiceRound2: Создание фото-сообщения о раунде 2 для chatId={}", chatId);

        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("MessageServiceRound2: Пользователь или персонаж не найден для раунда 2 для chatId={}", chatId);
            return PhotoReam.createRound2PhotoMessage(chatId, "Ошибка: персонаж не найден.");
        }

        PersonageEntity entity = user.getPersonage();
        String statsLine = statsDisplayService.buildStatsLine(entity);

        log.debug("MessageServiceRound2: Фото-сообщение о раунде 2 создано");
        return PhotoReam.createRound2PhotoMessage(chatId, statsLine);
    }

    /**
     * 💬 РЕПЛИКА ИТЕРАТОРИУСА ПЕРЕД РАУНДОМ 2
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

    /**
     * 📝 СООБЩЕНИЕ О АТМОСФЕРЕ РАУНДА 2
     */
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

    /**
     * 🎭 СООБЩЕНИЕ ОТ АРРЕЙНА В РАУНДЕ 2
     */
    public SendMessage messageArreyon2Rond(Long chatId) {
        log.debug("messageArreyon2Rond Отправка смс от Аррейна для chatId={}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);
        sendMessage.setText("""
                *Аррейн*
                 Ты реально ломаешь меня… Не думал, что кто-то пойдёт так далеко… \n
                 Надеюсь, у тебя есть план "Б" — и психолог. 
                """);
        return sendMessage;
    }

    /**
     * 🎯 ФИНАЛЬНОЕ СООБЩЕНИЕ ИТЕРАТОРИУСА С КНОПКАМИ
     */
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
                "☕ Кофе пауза — восстановить энергию(или нет)"
        );
        sendMessage.setReplyMarkup(KeyboardReam.BattlArreynRound2(chatId));
        return sendMessage;
    }

    /**
     * ☠️ СООБЩЕНИЕ НА КНОПКУ "ДОБИТЬ"
     */
    public SendMessage messageFinishOff(TelegramLongPollingBot bot, Long chatId) {
        log.info("MessageServiceRound2[messageFinishOff] ☠️ *Добить — повторить ад! запущен метод для chatId={}", chatId);
        
        // 🎯 КАСТОМНЫЕ ДИАПАЗОНЫ НАГРАД!
        PersonageStatManager.StatUpdateResult result = statManager.updateRandomRewards(
                chatId,
                80,   // expMin - минимальный опыт
                120,   // expMax - максимальный опыт
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
                        "\uD83D\uDD25_Нанесен урон Аррейну 30(-70)_\n" +
                        "💥Ты добиваешь Аррейона повторной вставкой в начало.\n" +
                        "Resize рушится. Структура разваливается на фрагменты.\n\n" +
                        "*Навык повышен:*\n" +
                        "  +" + result.getExpReward() + " ⭐️ к Очкам Достижения\n" +
                        "  +" + result.getCashReward() + " 💲 к Деньгам\n\n" +
                        "🐞 *Рандомный баг:* \"Ошибка залогирована… теперь её никто не найдёт, но все будут материться.\"\n"
        );
        message.setParseMode("Markdown");

        // 🎰 ЗАПУСКАЕМ СЦЕНАРИЙ КАЗИНО ЧЕРЕЗ CasinoScenarioService
        // Через 3 секунды после сообщения "Добить" запускаем казино
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                try {
                    log.info("MessageServiceRound2: 🎰 Запуск сценария казино после 'Добить' для chatId={}", chatId);
                    SendMessage casinoMessage = casinoScenarioService.createIteratoriusCasinoMessage(chatId);
                    casinoScenarioService.startCasinoScenario(bot, chatId, casinoMessage);
                } catch (Exception e) {
                    log.error("MessageServiceRound2: ❌ Ошибка запуска казино для chatId={}: {}", chatId, e.getMessage());
                }
            }
        }, 3000);

        return message;
    }

    /**
     * 🧠 СООБЩЕНИЕ ENSURE CAPACITY
     */
    public void messageEnsureCapacityRound2(TelegramLongPollingBot bot, Long chatId) {
        log.info("MessageServiceRound2[messageEnsureCapacityRound2] 🧠.ensureCapacity() запущен метод для chatId={}", chatId);

        try {
            // 1. СРАЗУ: Отправляем основное сообщение
            SendMessage mainMessage = new SendMessage(chatId.toString(),
                    "🧠.ensureCapacity() — взломать изнутри.\n" +
                            "🔥 _Нанесен урон Аррейну 30(-70)_ \n\n" +
                            "Ты вводишь команду, которой пользуются раз в пятилетку:\n" +
                            "```arrayList.ensureCapacity(50);```\n" +
                            "Аррейон корчится, его структура не выдерживает...\n\n" +
                            "🐞*Рандомный баг:* На проде эта фича сломалась бы в пятницу.\n"
            );
            mainMessage.setParseMode("Markdown");
            
            // 2. Создаем ответы персонажей
            SendMessage arreyonResponse = answerArrayenCapacityRound2(chatId);
            SendMessage iteratoriusResponse = answerIteratoriysCapacityRound2(bot, chatId);
            
            // 3. Запускаем цепочку через Round2SequenceService
            round2SequenceService.startEnsureCapacitySequence(bot, chatId, mainMessage, arreyonResponse, iteratoriusResponse);

        } catch (Exception e) {
            log.error("MessageServiceRound2: ❌ Критическая ошибка ensureCapacity для chatId={}: {}", chatId, e.getMessage(), e);
        }
    }

    /**
     * ☕ ЗАПУСКАЕТ КОФЕ-СЦЕНАРИЙ
     */
    public void messageCoffeRound2(TelegramLongPollingBot bot, Long chatId) {
        log.info("MessageServiceRound2[messageCoffeRound2] ☕ Запуск кофе-сценария для chatId={}", chatId);

        try {
            // 1. Отправляем фото с кодом и подписью
            SendPhoto coffeePhoto = PhotoReam.quizCoffeRound2(chatId);
            bot.execute(coffeePhoto);

            log.info("MessageServiceRound2: ☕ Фото с кодом отправлено для chatId={}", chatId);

            // 2. Через 3 секунды отправляем викторину
            // Используем простой планировщик для одного события
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    try {
                        SendPoll coffeeQuiz = quizService.createCoffeeQuiz(chatId);
                        bot.execute(coffeeQuiz);
                        log.info("MessageServiceRound2: ☕ Кофе-викторина отправлена для chatId={}", chatId);
                    } catch (TelegramApiException e) {
                        log.error("MessageServiceRound2: Ошибка отправки кофе-викторины для chatId={}: {}", chatId, e.getMessage());
                    }
                }
            }, 3000);

        } catch (TelegramApiException e) {
            log.error("MessageServiceRound2: Ошибка отправки фото для кофе-сценария chatId={}: {}", chatId, e.getMessage());
        }
    }

    /**
     * 🎭 ОТВЕТ АРРЕЙНА НА ENSURE CAPACITY
     */
    public SendMessage answerArrayenCapacityRound2(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Аррейн*\n" +
                "Не-е-ет! Только не резерв… Это мой ночной кошмар...");
        return sendMessage;
    }

    /**
     * 🎯 ОТВЕТ ИТЕРАТОРИУСА НА ENSURE CAPACITY
     */
    public SendMessage answerIteratoriysCapacityRound2(TelegramLongPollingBot bot, Long chatId) {
        log.info("MessageServiceRound2[answerIteratoriysCapacityRound2] 🎯 Создание ответа Итераториуса для chatId={}", chatId);

        PersonageStatManager.StatUpdateResult result = statManager.updateRandomRewards(
                chatId,
                80,   // expMin - минимальный опыт
                150,   // expMax - максимальный опыт
                300,  // cashMin - минимальные деньги
                450  // cashMax - максимальные деньги
        );

        // Если ошибка - возвращаем сообщение об ошибке
        if (!result.isSuccess()) {
            return statManager.createErrorMessage(chatId, result.getErrorMessage());
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус*\n" +
                "Ты сделал это... Малец, ты… ты не просто вызываешь методы — ты Понимаешь их.\n" +
                "А это... уже не шутки. Это... архитектура, мать её.\n\n" +
                "*Навык повышен:*\n" +
                "  +" + result.getExpReward() + " ⭐️ к Очкам Достижения\n" +
                "  +" + result.getCashReward() + " 💲 к Деньгам\n\n" +
                "🧠 *Мысль дня:* _Оптимизация — это когда код работает быстро, а не когда программист работает быстро._");

        log.info("MessageServiceRound2: ✅ Ответ Итераториуса создан для chatId={}, награды: +{} очков, +{} денег",
                chatId, result.getExpReward(), result.getCashReward());

        // 🎰 ЗАПУСКАЕМ СЦЕНАРИЙ КАЗИНО ЧЕРЕЗ CasinoScenarioService
        // Через 3 секунды после ответа Итераториуса запускаем казино
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                try {
                    log.info("MessageServiceRound2: 🎰 Запуск сценария казино после ответа Итераториуса для chatId={}", chatId);
                    SendMessage casinoMessage = casinoScenarioService.createIteratoriusCasinoMessage(chatId);
                    casinoScenarioService.startCasinoScenario(bot, chatId, casinoMessage);
                } catch (Exception e) {
                    log.error("MessageServiceRound2: ❌ Ошибка запуска казино для chatId={}: {}", chatId, e.getMessage());
                }
            }
        }, 3000);

        return sendMessage;
    }

    /**
     * 🚀 ЗАПУСК ПОЛНОЙ ЦЕПОЧКИ РАУНДА 2
     */
    public void startRound2Sequence(TelegramLongPollingBot bot, Long chatId) {
        log.info("MessageServiceRound2[startRound2Sequence] 🚀 Запуск цепочки раунда 2 для chatId={}", chatId);
        
        // Создаем все необходимые сообщения
        SendPhoto round2Photo = this.createRound2Message(chatId);
        SendMessage textMessage = this.sendMessageText2Rond(chatId);
        SendMessage arreyonMessage = this.messageArreyon2Rond(chatId);
        SendMessage iteratoriusMessage = this.messageIteratorius2Rond(chatId);
        
        // Делегируем планирование в Round2SequenceService
        round2SequenceService.startRound2Sequence(bot, chatId, round2Photo, textMessage, arreyonMessage, iteratoriusMessage);
    }

    /**
     * 🏆 СООБЩЕНИЕ О ПОБЕДЕ В РАУНДЕ 2
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
            statsLine = "\n" + statsDisplayService.buildStatsLine(user.getPersonage());
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
