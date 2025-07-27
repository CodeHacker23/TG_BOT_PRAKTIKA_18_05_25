package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bot.KeyboardService.KeyboardReam;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ArrayListBattleService — сервис для управления боевой системой в изучении ArrayList.
 * <p>
 * Этот класс отвечает за:
 * - Обработку боевых действий пользователя
 * - Начисление опыта и валюты за бои
 * - Создание атакующих сообщений
 * - Логику боевых раундов
 * <p>
 * Связи с другими классами:
 * - Используется в ArrayListStory для обработки боевых команд
 * - Работает с UserService для сохранения прогресса пользователя
 * - Использует ArrayListTheoryService для создания сообщений
 * - Интегрируется с ArrayListSchedulerService для отложенных действий
 * <p>
 * Принцип работы:
 * 1. Пользователь выбирает боевое действие (например, try-catch)
 * 2. Система обрабатывает действие и начисляет награды
 * 3. Отправляется результат боя с информацией о полученных наградах
 * 4. Прогресс сохраняется в базе данных
 * <p>
 * Автор: Архитектор (который знает, что бой без логики — это просто баг)
 * <p>
 * Пример использования:
 *
 * @Autowired private ArrayListBattleService battleService;
 * <p>
 * battleService.processTryCatchAction(bot, chatId);
 * SendMessage attack = battleService.createAttackMessage(chatId);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArrayListBattleService {

    private final UserService userService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Обрабатывает действие "Блокировать (try-catch)" пользователя.
     * <p>
     * Этот метод реализует боевую логику для защиты от ошибок ArrayList:
     * - Отправляет сообщение о попытке защиты
     * - Начисляет случайное количество опыта и валюты
     * - Сохраняет прогресс в базе данных
     * - Отправляет результат через 2 секунды
     * <p>
     * Логика боя:
     * 1. Пользователь пытается защититься try-catch
     * 2. Система показывает, что защита не сработала
     * 3. Начисляются награды за попытку
     * 4. Отправляется результат с полученными наградами
     *
     * @param bot    — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     *               <p>
     *               Пример использования:
     *               battleService.processTryCatchAction(bot, chatId);
     */
    public void processTryCatchAction(TelegramLongPollingBot bot, Long chatId) {
        log.info("ArrayListBattleService: Обработка действия try-catch для chatId={} - ВЫЗВАН ИЗ: {}", chatId, 
                Thread.currentThread().getStackTrace()[2].getMethodName());

        // Создаем сообщение о попытке защиты
        SendMessage defenseMessage = createTryCatchDefenseMessage(chatId);
        try {
            // Отправляем сообщение о защите
            bot.execute(defenseMessage);
            log.debug("ArrayListBattleService: Сообщение о защите отправлено");

            // Обрабатываем награды и сохраняем прогресс
            processBattleRewards(chatId, bot);

        } catch (TelegramApiException e) {
            log.error("ArrayListBattleService: Ошибка отправки сообщения о защите для chatId={}", chatId, e);
        }
    }


    /**
     * Создает сообщение о попытке защиты try-catch.
     * <p>
     * Это сообщение объясняет, что try-catch не всегда помогает
     * при работе с ArrayList, особенно при ArrayIndexOutOfBoundsException.
     *
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о попытке защиты
     */
    private SendMessage createTryCatchDefenseMessage(Long chatId) {
        log.debug("ArrayListBattleService: Создание сообщения о защите try-catch для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("Нанесен урон Айрену 100(-50)\n\n" +
                "_Ты строишь стену из_\n" +
                "```try { ... } catch (...) { ... } ```\n" +
                "_но Аррейн не из тех, кто уважает чужие перехваты._\n\n" +
                "Аррейон выносит с ноги твою защиту, как баги выносят прод после пятничного рефактора.\n\n" +
                "*Ошибка:* ```ArrayIndexOutOfBoundsException``` прорывает блок, словно нож сквозь масло.\n\n" +
                "*Получен БАГ* - Ошибка ушла в отпуск, но обещала вернуться к дедлайну.");
        log.debug("ArrayListBattleService: Сообщение о защите создано");
        return sendMessage;
    }

    /**
     * Обрабатывает награды за боевое действие и сохраняет прогресс.
     * <p>
     * Этот метод:
     * - Получает данные пользователя и персонажа
     * - Генерирует случайные награды (опыт и валюта)
     * - Сохраняет изменения в базе данных
     * - Планирует отправку результата через 2 секунды
     *
     * @param chatId — ID чата пользователя
     * @param bot    — TelegramLongPollingBot для отправки результата
     */
    private void processBattleRewards(Long chatId, TelegramLongPollingBot bot) {
        log.debug("ArrayListBattleService: Обработка наград для chatId={}", chatId);

        // Получаем пользователя и персонажа
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("ArrayListBattleService: Пользователь или персонаж не найден для chatId={}", chatId);
            return;
        }

        PersonageEntity entity = user.getPersonage();

        // Сохраняем старые значения для отображения
        int oldPoints = entity.getAchievementPoints();
        double oldMoney = entity.getCurrency();

        // Генерируем случайные награды
        int expReward = generateRandomReward(38, 55);
        int cashReward = generateRandomReward(200, 350);

        // Начисляем награды
        entity.setAchievementPoints(oldPoints + expReward);
        entity.setCurrency(oldMoney + cashReward);

        // Сохраняем изменения в БД
        userService.saveUser(user);

        log.info("ArrayListBattleService: Награды начислены - опыт: +{}, валюта: +{} для chatId={}",
                expReward, cashReward, chatId);

        // Планируем отправку результата через 2 секунды
        scheduler.schedule(() -> {
            try {
                SendMessage resultMessage = createBattleResultMessage(chatId, expReward, cashReward);
                bot.execute(resultMessage);
                log.debug("ArrayListBattleService: Результат боя отправлен для chatId={}", chatId);
            } catch (TelegramApiException e) {
                log.error("ArrayListBattleService: Ошибка отправки результата боя для chatId={}", chatId, e);
            }
        }, 4, TimeUnit.SECONDS);
    }

    /**
     * Создает сообщение с результатом боевого действия.
     * <p>
     * Это сообщение показывает пользователю, какие награды он получил
     * за попытку защиты, и дает совет на будущее.
     *
     * @param chatId     — ID чата пользователя
     * @param expReward  — полученный опыт
     * @param cashReward — полученная валюта
     * @return SendMessage — сообщение с результатом боя
     */
    private SendMessage createBattleResultMessage(Long chatId, int expReward, int cashReward) {
        log.info("ArrayListBattleService: Создание результата боя (try-catch) для chatId={}, опыт: {}, валюта: {}",
                chatId, expReward, cashReward);

        SendMessage resultMsg = new SendMessage();
        resultMsg.setChatId(chatId);
        resultMsg.setParseMode("Markdown");
        resultMsg.setText("*Итераториус*:\n\n" +
                "Молодец, конечно… Только try-catch не вечен.\n" +
                "Не всё в жизни ловится на костыли.\n" +
                " _Следующий ход — только защита или анализ. Атаковать нельзя._\n" +
                "*Навык повышен:*\n" +
                "  +" + expReward + " ⭐️ к Очкам Достижения\n" +
                "  +" + cashReward + " \uD83D\uDCB2 к Деньгам.");

        log.info("ArrayListBattleService: Результат боя (try-catch) создан");
        return resultMsg;
    }

    /**
     * Создает атакующее сообщение от Аррейна.
     * <p>
     * Это сообщение представляет атаку противника и предоставляет
     * пользователю кнопки для выбора ответного действия.
     *
     * @param chatId — ID чата пользователя
     * @return SendMessage — атакующее сообщение с кнопками
     * <p>
     * Пример использования:
     * SendMessage attack = battleService.createAttackMessage(chatId);
     * bot.execute(attack);
     */
    public SendMessage createAttackMessage(Long chatId) {
        log.debug("ArrayListBattleService: Создание атакующего сообщения для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("\uD83D\uDCA5 _Аррейн бросает в тебя виртуальный элемент с индексом 0!_\n\n" +
                "\uD83E\uDDE0 *Итераториус* (шепчет):\n" +
                "У тебя есть доля секунды. Реагируй!\n\n ");
        sendMessage.setReplyMarkup(KeyboardReam.BattlArreyn(chatId));

        log.debug("ArrayListBattleService: Атакующее сообщение создано");
        return sendMessage;
    }

    /**
     * Генерирует случайную награду в заданном диапазоне.
     * <p>
     * Этот метод используется для создания случайных наград
     * в боевой системе, чтобы сделать игру более интересной.
     *
     * @param min — минимальное значение награды
     * @param max — максимальное значение награды
     * @return int — случайная награда в диапазоне [min, max]
     * <p>
     * Пример использования:
     * int reward = battleService.generateRandomReward(10, 50);
     */
    /**
     * Генерирует случайную награду в заданном диапазоне.
     * 
     * @param min — минимальное значение награды
     * @param max — максимальное значение награды
     * @return int — случайная награда в диапазоне [min, max]
     */
    public int generateRandomReward(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    /**
     * Обрабатывает другие боевые действия (заглушка для будущего расширения).
     * <p>
     * Этот метод предназначен для обработки других типов боевых действий,
     * которые могут быть добавлены в будущем.
     *
     * @param action — тип боевого действия
     * @param bot    — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
    public void processOtherBattleAction(String action, TelegramLongPollingBot bot, Long chatId) {
        log.info("ArrayListBattleService: Обработка боевого действия '{}' для chatId={}", action, chatId);

        // TODO: Реализовать обработку других боевых действий
        log.warn("ArrayListBattleService: Боевое действие '{}' не реализовано", action);

        try {
            SendMessage notImplementedMessage = new SendMessage();
            notImplementedMessage.setChatId(chatId);
            notImplementedMessage.setText("🚧 Это боевое действие пока в разработке!");
            bot.execute(notImplementedMessage);
        } catch (TelegramApiException e) {
            log.error("ArrayListBattleService: Ошибка отправки сообщения о нереализованном действии", e);
        }
    }

    /**
     * Проверяет, может ли пользователь участвовать в бою.
     * <p>
     * Этот метод проверяет наличие персонажа у пользователя
     * и его готовность к боевым действиям.
     *
     * @param chatId — ID чата пользователя
     * @return boolean — true если пользователь может участвовать в бою
     */
    public boolean canParticipateInBattle(Long chatId) {
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("ArrayListBattleService: Пользователь не может участвовать в бою - нет персонажа для chatId={}", chatId);
            return false;
        }

        PersonageEntity entity = user.getPersonage();
        if (entity.getEnergy() <= 0) {
            log.warn("ArrayListBattleService: Пользователь не может участвовать в бою - нет энергии для chatId={}", chatId);
            return false;
        }

        return true;
    }

    /**
     * Получает информацию о боевых характеристиках персонажа.
     * <p>
     * Этот метод возвращает строку с боевыми характеристиками персонажа,
     * которые важны для боевой системы.
     *
     * @param chatId — ID чата пользователя
     * @return String — строка с боевыми характеристиками или null если персонаж не найден
     */
    public String getBattleStats(Long chatId) {
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            return null;
        }

        PersonageEntity entity = user.getPersonage();
        return String.format("⚡️Энергия: %d | ⭐️Очки: %d | 💲Деньги: %.0f",
                entity.getEnergy(),
                entity.getAchievementPoints(),
                entity.getCurrency());

    }


    public void processAnalysisAction(TelegramLongPollingBot bot, Long chatId) {
        log.info("ArrayListBattleService: Обработка действия Analysis для chatId={} - ВЫЗВАН ИЗ: {}", chatId, 
                Thread.currentThread().getStackTrace()[2].getMethodName());

        // Создаем сообщение о попытке анализа.
        SendMessage analysisMessage = sendMessageAnalysis(chatId);
        try {
            // Отправляем сообщение о анализе
            bot.execute(analysisMessage);
            log.debug("ArrayListBattleService: Сообщение о анализе отправлено");

            // НЕ вызываем processBattleRewards - награды будут обработаны в ArrayListSchedulerService

        } catch (TelegramApiException e) {
            log.error("ArrayListBattleService: Ошибка отправки сообщения о анализе для chatId={}", chatId, e);
        }
    }

    /**
     * Создает сообщение о попытке анализа ArrayList.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о попытке анализа
     */
    private SendMessage sendMessageAnalysis(Long chatId) {
        log.debug("ArrayListBattleService: Создание сообщения о анализе для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("📘 Ты вспоминаешь строки древнего манускрипта JavaDocs…\n\n" +
                "Ты сканируешь память — мозг работает на пределе.\n" +
                "*ArrayList — это просто массив.*\n" +
                "*Вставка в начало? Сдвиг, тормоза, страдания. Ты этого хочешь?!*");
        
        log.debug("ArrayListBattleService: Сообщение о анализе создано");
        return sendMessage;
    }

    /**
     * Обрабатывает награды за действие анализа.
     * 
     * @param chatId — ID чата пользователя
     * @param expReward — награда за опыт
     * @param cashReward — награда за деньги
     */
    public void processAnalysisRewards(Long chatId, int expReward, int cashReward) {
        log.debug("ArrayListBattleService: Обработка наград за анализ для chatId={}, опыт: {}, валюта: {}", 
                chatId, expReward, cashReward);

        // Получаем пользователя и персонажа
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("ArrayListBattleService: Пользователь или персонаж не найден для chatId={}", chatId);
            return;
        }

        PersonageEntity entity = user.getPersonage();

        // Начисляем награды
        entity.setAchievementPoints(entity.getAchievementPoints() + expReward);
        entity.setCurrency(entity.getCurrency() + cashReward);

        // Сохраняем изменения в БД
        userService.saveUser(user);

        log.info("ArrayListBattleService: Награды за анализ начислены - опыт: +{}, валюта: +{} для chatId={}",
                expReward, cashReward, chatId);
    }

    /**
     * Создает сообщение с результатом анализа от Итераториуса.
     * 
     * @param chatId — ID чата пользователя
     * @param expReward — награда за опыт
     * @param cashReward — награда за деньги
     * @return SendMessage — сообщение с результатом анализа
     */
    public SendMessage BattleResultIteratorius(Long chatId, int expReward, int cashReward) {
        log.info("ArrayListBattleService: Создание результата АНАЛИЗА для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус:*\n\n" +
                "Вот это подход! Учиться через боль — зато запомнишь на всю жизнь.\n" +
                "Только не забывай, что в пятницу прод лучше не трогать.\n\n" +
                "Видно, что ты читал JavaDoc, а не только переписывал код с StackOverflow.\n\n" +
                "*Навык повышен:*\n" +
                "  +" + expReward + " ⭐️ к Очкам Достижения\n" +
                "  +" + cashReward + " 💲 к Деньгам.");
        
        log.info("ArrayListBattleService: Результат АНАЛИЗА создан");
        return sendMessage;
    }



}