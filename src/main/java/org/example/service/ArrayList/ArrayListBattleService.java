package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

/**
 * ArrayListBattleService — упрощенный сервис для боевой системы ArrayList.
 * <p>
 * Этот класс теперь является фасадом для других сервисов:
 * - BattleActionService — обработка боевых действий
 * - StatService — работа со статами
 * - MessageService — создание сообщений
 * <p>
 * Зачем упростили:
 * - Было 697 строк говнокода, стало ~100 строк чистого кода
 * - Разделили ответственность между специализированными сервисами
 * - Код стал читаемым и поддерживаемым
 * <p>
 * Автор: Архитектор (который знает, что рефакторинг — это как пластическая хирургия для кода)
 * <p>
 * Пример использования:
 * 
 * @Autowired private ArrayListBattleService battleService;
 * 
 * battleService.processTryCatchAction(bot, chatId);
 * SendMessage result = battleService.createBattleResult(chatId, expReward, cashReward);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArrayListBattleService {

    private final UserService userService;
    private final BattleActionService battleActionService;
    private final StatService statService;
    private final MessageService messageService;

    // ========== БОЕВЫЕ ДЕЙСТВИЯ ==========

    /**
     * Обрабатывает действие "Блокировать (try-catch)".
     * 
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
    public void processTryCatchAction(TelegramLongPollingBot bot, Long chatId) {
        log.info("ArrayListBattleService: Делегируем обработку try-catch для chatId={}", chatId);
        battleActionService.processTryCatchAction(bot, chatId);
    }

    /**
     * Обрабатывает действие "Анализировать".
     * 
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
    public void processAnalysisAction(TelegramLongPollingBot bot, Long chatId) {
        log.info("ArrayListBattleService: Делегируем обработку анализа для chatId={}", chatId);
        battleActionService.processAnalysisAction(bot, chatId);
    }

    /**
     * Обрабатывает действие "Вставить в начало".
     * 
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
    public void processInsertBeginningAction(TelegramLongPollingBot bot, Long chatId) {
        log.info("ArrayListBattleService: Делегируем обработку вставки в начало для chatId={}", chatId);
        battleActionService.processInsertBeginningAction(bot, chatId);
    }

    // ========== СОЗДАНИЕ СООБЩЕНИЙ ==========

    /**
     * Создает сообщение о попытке защиты try-catch.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о защите
     */
    public SendMessage createTryCatchDefenseMessage(Long chatId) {
        log.debug("ArrayListBattleService: Делегируем создание сообщения о защите для chatId={}", chatId);
        return messageService.createTryCatchDefenseMessage(chatId);
    }

    /**
     * Создает сообщение с результатом боя.
     * 
     * @param chatId — ID чата пользователя
     * @param expReward — награда за опыт
     * @param cashReward — награда за деньги
     * @return SendMessage — сообщение с результатом
     */
    public SendMessage createBattleResultMessage(Long chatId, int expReward, int cashReward) {
        log.debug("ArrayListBattleService: Делегируем создание результата боя для chatId={}", chatId);
        return messageService.createBattleResultMessage(chatId, expReward, cashReward);
    }

    /**
     * Создает сообщение о попытке анализа.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение об анализе
     */
    public SendMessage createAnalysisMessage(Long chatId) {
        log.debug("ArrayListBattleService: Делегируем создание сообщения об анализе для chatId={}", chatId);
        return messageService.createAnalysisMessage(chatId);
    }

    /**
     * Создает сообщение с результатом анализа.
     * 
     * @param chatId — ID чата пользователя
     * @param expReward — награда за опыт
     * @param cashReward — награда за деньги
     * @return SendMessage — сообщение с результатом анализа
     */
    public SendMessage createAnalysisResultMessage(Long chatId, int expReward, int cashReward) {
        log.debug("ArrayListBattleService: Делегируем создание результата анализа для chatId={}", chatId);
        return messageService.createAnalysisResultMessage(chatId, expReward, cashReward);
    }

    /**
     * Создает сообщение о попытке вставки в начало.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о вставке
     */
    public SendMessage createInsertBeginningMessage(Long chatId) {
        log.debug("ArrayListBattleService: Делегируем создание сообщения о вставке для chatId={}", chatId);
        return messageService.createInsertBeginningMessage(chatId);
    }

    /**
     * Создает сообщение с результатом вставки в начало.
     * 
     * @param chatId — ID чата пользователя
     * @param statChanges — изменения статов
     * @return SendMessage — сообщение с результатом
     */
    public SendMessage createInsertBeginningResultMessage(Long chatId, Map<String, Integer> statChanges) {
        log.debug("ArrayListBattleService: Делегируем создание результата вставки для chatId={}", chatId);
        return messageService.createInsertBeginningResultMessage(chatId, statChanges);
    }

    /**
     * Создает сообщение с атакой.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение с атакой
     */
    public SendMessage createAttackMessage(Long chatId) {
        log.debug("ArrayListBattleService: Делегируем создание сообщения с атакой для chatId={}", chatId);
        return messageService.createAttackMessage(chatId);
    }

    // ========== РАБОТА СО СТАТАМИ ==========

    /**
     * Генерирует случайное число в заданном диапазоне.
     * 
     * @param min — минимальное значение
     * @param max — максимальное значение
     * @return int — случайное число
     */
    public int generateRandomReward(int min, int max) {
        return statService.generateRandomReward(min, max);
    }

    /**
     * Генерирует стандартные награды для боевых действий.
     * 
     * @return Map<String, Integer> — карта наград
     */
    public Map<String, Integer> generateStandardRewards() {
        return statService.generateStandardRewards();
    }

    /**
     * Генерирует кастомные изменения статов.
     * 
     * @return Map<String, Integer> — карта изменений
     */
    public Map<String, Integer> generateCustomStatChanges() {
        return statService.generateCustomStatChanges();
    }

    /**
     * Применяет изменения статов к персонажу.
     * 
     * @param chatId — ID чата пользователя
     * @param statChanges — карта изменений статов
     */
    public void applyStatChanges(Long chatId, Map<String, Integer> statChanges) {
        statService.applyStatChanges(chatId, statChanges);
    }

    // ========== УТИЛИТЫ ==========

    /**
     * Проверяет, может ли пользователь участвовать в бою.
     * 
     * @param chatId — ID чата пользователя
     * @return boolean — true если может участвовать
     */
    public boolean canParticipateInBattle(Long chatId) {
        return battleActionService.canParticipateInBattle(chatId);
    }

    /**
     * Получает статистику боя для пользователя.
     * 
     * @param chatId — ID чата пользователя
     * @return String — статистика боя
     */
    public String getBattleStats(Long chatId) {
        return battleActionService.getBattleStats(chatId);
    }

    // ========== КОМПАТИБИЛЬНОСТЬ ==========

    /**
     * @deprecated Используйте processTryCatchAction вместо этого метода
     */
    @Deprecated
    public void processTryCatchAction(TelegramLongPollingBot bot, Long chatId, boolean deprecated) {
        log.warn("ArrayListBattleService: Используется устаревший метод processTryCatchAction");
        processTryCatchAction(bot, chatId);
    }

    /**
     * @deprecated Используйте createBattleResultMessage вместо этого метода
     */
    @Deprecated
    public SendMessage BattleResultIteratorius(Long chatId, int expReward, int cashReward) {
        log.warn("ArrayListBattleService: Используется устаревший метод BattleResultIteratorius");
        return createAnalysisResultMessage(chatId, expReward, cashReward);
    }

    /**
     * @deprecated Используйте createInsertBeginningMessage вместо этого метода
     */
    @Deprecated
    public SendMessage InsertBeginning(Long chatId) {
        log.warn("ArrayListBattleService: Используется устаревший метод InsertBeginning");
        return createInsertBeginningMessage(chatId);
    }

    /**
     * @deprecated Используйте createInsertBeginningResultMessage вместо этого метода
     */
    @Deprecated
    public SendMessage BattleResultInsertBeginning(Long chatId, int expReward, int cashReward) {
        log.warn("ArrayListBattleService: Используется устаревший метод BattleResultInsertBeginning");
        
        // Генерируем кастомные изменения статов
        Map<String, Integer> statChanges = statService.generateCustomStatChanges();
        
        // Добавляем индивидуальный стат для персонажа
        var user = userService.getUserByTgId(chatId);
        if (user != null && user.getPersonage() != null) {
            String characterType = user.getPersonage().getCharacterType();
            String individualStat = statService.getIndividualStatForCharacter(characterType);
            statChanges.put(individualStat, statService.generateRandomReward(15, 25));
        }
        
        // Применяем изменения
        statService.applyStatChanges(chatId, statChanges);
        
        return createInsertBeginningResultMessage(chatId, statChanges);
    }
}