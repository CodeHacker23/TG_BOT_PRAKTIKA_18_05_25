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
import java.util.Map;
import java.util.HashMap;

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
        sendMessage.setText("*Нанесен урон Аррейну 100(-50)*\n\n" +
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
     * Обработка индивидуальных парметоров
     * @param chatId
     * @param bot
     */
    public void processBattleRewardsSpecial(Long chatId, TelegramLongPollingBot bot){
        log.warn("ArrayListBattleService: [processBattleRewardsSpecial] Пользователь или персонаж не найден для chatId={}", chatId);

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
     * Обрабатывает кастомные изменения статов персонажа.
     * 
     * Этот метод применяет изменения к различным характеристикам персонажа:
     * - Может уменьшать деньги (отрицательные значения)
     * - Может увеличивать другие статы (положительные значения)
     * - Сохраняет все изменения в базе данных
     * 
     * @param chatId — ID чата пользователя
     * @param statChanges — Map с изменениями статов
     * 
     * Пример использования:
     * Map<String, Integer> changes = generateCustomStatChanges();
     * processCustomRewards(chatId, changes);
     */
    public void processCustomRewards(Long chatId, Map<String, Integer> statChanges) {
        log.debug("ArrayListBattleService: Обработка кастомных изменений статов для chatId={}, изменения: {}", 
                chatId, statChanges);

        // Получаем пользователя и персонажа
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("ArrayListBattleService: Пользователь или персонаж не найден для chatId={}", chatId);
            return;
        }

        PersonageEntity entity = user.getPersonage();

        // Применяем изменения к статам
        for (Map.Entry<String, Integer> entry : statChanges.entrySet()) {
            String statName = entry.getKey();
            Integer change = entry.getValue();

            switch (statName) {
                case "money":
                    Double currentCurrency = entity.getCurrency();
                    entity.setCurrency((currentCurrency != null ? currentCurrency : 0.0) + change);
                    log.debug("ArrayListBattleService: Изменены деньги на {} для chatId={}", change, chatId);
                    break;
                case "analytics":
                    Integer currentAnalytics = entity.getAnalytics();
                    entity.setAnalytics((currentAnalytics != null ? currentAnalytics : 0) + change);
                    log.debug("ArrayListBattleService: Изменена аналитика на {} для chatId={}", change, chatId);
                    break;
                case "optimization":
                    Integer currentOptimization = entity.getOptimization();
                    entity.setOptimization((currentOptimization != null ? currentOptimization : 0) + change);
                    log.debug("ArrayListBattleService: Изменена оптимизация на {} для chatId={}", change, chatId);
                    break;
                case "code_accuracy":
                    Integer currentCodeAccuracy = entity.getCodeAccuracy();
                    entity.setCodeAccuracy((currentCodeAccuracy != null ? currentCodeAccuracy : 0) + change);
                    log.debug("ArrayListBattleService: Изменена точность кода на {} для chatId={}", change, chatId);
                    break;
                case "communication":
                    Integer currentCommunication = entity.getCommunication();
                    entity.setCommunication((currentCommunication != null ? currentCommunication : 0) + change);
                    log.debug("ArrayListBattleService: Изменена коммуникация на {} для chatId={}", change, chatId);
                    break;
                case "humor":
                    Integer currentHumor = entity.getHumor();
                    entity.setHumor((currentHumor != null ? currentHumor : 0) + change);
                    log.debug("ArrayListBattleService: Изменен юмор на {} для chatId={}", change, chatId);
                    break;
                case "achievement_points":
                    Integer currentAchievementPoints = entity.getAchievementPoints();
                    entity.setAchievementPoints((currentAchievementPoints != null ? currentAchievementPoints : 0) + change);
                    log.debug("ArrayListBattleService: Изменены очки достижения на {} для chatId={}", change, chatId);
                    break;
                default:
                    log.warn("ArrayListBattleService: Неизвестный стат '{}' для chatId={}", statName, chatId);
            }
        }

        // Сохраняем изменения в БД
        userService.saveUser(user);

        log.info("ArrayListBattleService: Кастомные изменения статов применены для chatId={}", chatId);
    }

    /**
     * Генерирует изменения статов для персонажа.
     * 
     * Этот метод создает Map с изменениями различных характеристик персонажа:
     * - Деньги могут уменьшаться (отрицательные значения)
     * - Другие статы увеличиваются (положительные значения)
     * - Каждый вызов генерирует случайные значения в заданных диапазонах
     * 
     * @return Map<String, Integer> — карта изменений статов
     * 
     * Пример возвращаемого значения:
     * {
     *   "money": -95,        // Уменьшаем деньги на 95
     *   "analytics": +18,    // Увеличиваем аналитику на 18
     *   "optimization": +12, // Увеличиваем оптимизацию на 12
     *   "code_accuracy": +8  // Увеличиваем точность кода на 8
     * }
     */
    public Map<String, Integer> generateCustomStatChanges() {
        Map<String, Integer> changes = new HashMap<>();
        
        // Уменьшаем деньги (отрицательное значение)
        changes.put("money", -generateRandomReward(80, 120));
        
        // Увеличиваем различные статы (положительные значения)
        changes.put("analytics", generateRandomReward(15, 25));
        changes.put("optimization", generateRandomReward(10, 20));
        changes.put("code_accuracy", generateRandomReward(8, 15));
        changes.put("communication", generateRandomReward(5, 12));
        changes.put("humor", generateRandomReward(3, 8));
        
        log.debug("ArrayListBattleService: Сгенерированы изменения статов: {}", changes);
        return changes;
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






    /**
     * Отправялем ответ на кнопку вставить в начало
     * @param chatId
     * @return
     */
    public SendMessage InsertBeginning(Long chatId ){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*\uD83D\uDEA8 Вставить в начало — ва-банк!*\n" +
                "\uD83D\uDD25 Аррейн получает урон 100(-50) — начинается частичный resize()\n" +
                "\n" +
                "Ты швыряешь элемент в начало списка — как камень в стеклянную крышу офиса.\n" +
                "\n" +
                "*АРРЕЙОН (звереет):*\n" +
                "«Ты что, совсем страх потерял?! Я ТАК не работаю! Сейчас будет больно — тебе, мне и твоему менеджеру.»\n" +
                "\n" +
                "_Массив трещит, но урон  наносишь ты_");
        return sendMessage;
    }

        /**
     * Создает сообщение с результатом "Вставить в начало" от Итераториуса.
     * 
     * Этот метод применяет изменения статов:
     * - Уменьшает деньги на фиксированную сумму (150-200)
     * - Увеличивает очки достижения
     * - Увеличивает один индивидуальный параметр в зависимости от типа персонажа
     * - Сохраняет изменения в базе данных
     *
     * @param chatId — ID чата пользователя
     * @param expReward — награда за опыт (не используется, оставлен для совместимости)
     * @param cashReward — награда за деньги (не используется, оставлен для совместимости)
     * @return SendMessage — сообщение с результатом изменений
     */
    public SendMessage BattleResultInsertBeginning(Long chatId, int expReward, int cashReward){
        log.info("ArrayListBattleService: [BattleResultInsertBeginning] создание изменений статов для chatId={}", chatId);
        
        try {
            // Получаем пользователя и персонажа
            UserEntity user = userService.getUserByTgId(chatId);
            if (user == null || user.getPersonage() == null) {
                log.warn("ArrayListBattleService: Пользователь или персонаж не найден для chatId={}", chatId);
                return new SendMessage(chatId.toString(), "Ошибка: персонаж не найден");
            }

            PersonageEntity entity = user.getPersonage();
            log.info("ArrayListBattleService: Персонаж найден: {}", entity.getCharacterType());
            
            // Создаем изменения статов
            Map<String, Integer> statChanges = new HashMap<>();
            
            // Уменьшаем деньги на фиксированную сумму (150-200)
            int moneyChange = -generateRandomReward(150, 200);
            statChanges.put("money", moneyChange);
            log.info("ArrayListBattleService: Сгенерировано изменение денег: {}", moneyChange);
            
            // Увеличиваем очки достижения
            int achievementChange = generateRandomReward(30, 50);
            statChanges.put("achievement_points", achievementChange);
            log.info("ArrayListBattleService: Сгенерировано изменение очков достижения: {}", achievementChange);
            
            // Увеличиваем один индивидуальный параметр в зависимости от типа персонажа
            String characterType = entity.getCharacterType();
            String individualStat = getIndividualStatForCharacter(characterType);
            int individualChange = generateRandomReward(15, 25);
            statChanges.put(individualStat, individualChange);
            log.info("ArrayListBattleService: Сгенерировано изменение индивидуального стата {}: {}", individualStat, individualChange);
            
            // Применяем изменения к персонажу
            processCustomRewards(chatId, statChanges);
            log.info("ArrayListBattleService: Изменения применены к персонажу");
            
            // Создаем сообщение с результатом
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setParseMode("Markdown");
            
            // Определяем эмодзи и название в зависимости от типа персонажа
            String individualStatInfo = "";
            switch (individualStat) {
                case "analytics":
                    individualStatInfo = "📊 к Аналитике";
                    break;
                case "optimization":
                    individualStatInfo = "⚙️ к Оптимизации";
                    break;
                case "code_accuracy":
                    individualStatInfo = "🎯 к Точности кода";
                    break;
                case "communication":
                    individualStatInfo = "💬 к Коммуникации";
                    break;
                default:
                    individualStatInfo = "📊 к Аналитике";
            }
            
            sendMessage.setText("*Итераториус:*\n\n" +
                    "Смело, но немного безрассудно. Главное, чтобы прод это не увидел.\n" +
                    "Иногда даже идиотизм — это стратегия.\n\n" +
                    "*Навык повышен:*\n" +
                    " +" + achievementChange + " ⭐️ к Очкам Достижения\n" +
                    " -" + Math.abs(moneyChange) + " 💲 к Деньгам (за психотерапевта позже)\n" +
                    " +" + individualChange + " " + individualStatInfo);
            
            log.info("ArrayListBattleService: [BattleResultInsertBeginning] сообщение создано для chatId={}, тип персонажа: {}, индивидуальный стат: {}", 
                    chatId, characterType, individualStat);
            return sendMessage;
            
        } catch (Exception e) {
            log.error("ArrayListBattleService: Ошибка в BattleResultInsertBeginning для chatId={}", chatId, e);
            return new SendMessage(chatId.toString(), "Ошибка: не удалось создать сообщение");
        }
    }

    /**
     * Получает индивидуальный стат для конкретного типа персонажа.
     * 
     * @param characterType — тип персонажа
     * @return String — название индивидуального стата
     */
    private String getIndividualStatForCharacter(String characterType) {
        switch (characterType) {
            case "Personage1":
                return "analytics"; // Аналитика для Personage1
            case "Personage2":
                return "communication"; // Коммуникация для Personage2
            case "Personage3":
                return "code_accuracy"; // Точность кода для Personage3
            default:
                log.warn("ArrayListBattleService: Неизвестный тип персонажа: {}", characterType);
                return "analytics"; // По умолчанию аналитика
        }
    }
}