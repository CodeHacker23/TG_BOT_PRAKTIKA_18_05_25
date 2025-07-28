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
 * BattleActionService — сервис для обработки боевых действий в ArrayList.
 * <p>
 * Этот класс отвечает за:
 * - Обработку действия try-catch
 * - Обработку действия анализа
 * - Обработку действия вставки в начало
 * - Проверки возможности участия в бою
 * <p>
 * Зачем нужен:
 * - Вынес логику обработки действий из огромного ArrayListBattleService
 * - Централизованная обработка всех боевых действий
 * - Четкое разделение ответственности
 * <p>
 * Автор: Архитектор (который знает, что бой без логики — это просто баг)
 * <p>
 * Пример использования:
 * 
 * @Autowired private BattleActionService battleActionService;
 * 
 * battleActionService.processTryCatchAction(bot, chatId);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BattleActionService {

    private final UserService userService;
    private final StatService statService;
    private final MessageService messageService;

    /**
     * Обрабатывает действие "Блокировать (try-catch)" пользователя.
     * 
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
    public void processTryCatchAction(TelegramLongPollingBot bot, Long chatId) {
        log.info("BattleActionService: Обработка действия try-catch для chatId={}", chatId);

        try {
            // Отправляем сообщение о защите
            SendMessage defenseMessage = messageService.createTryCatchDefenseMessage(chatId);
            bot.execute(defenseMessage);
            log.debug("BattleActionService: Сообщение о защите отправлено");

            // Генерируем и применяем награды
            Map<String, Integer> rewards = statService.generateStandardRewards();
            statService.applyStatChanges(chatId, rewards);
            
            log.info("BattleActionService: Действие try-catch обработано для chatId={}", chatId);
            
        } catch (TelegramApiException e) {
            log.error("BattleActionService: Ошибка отправки сообщения о защите для chatId={}", chatId, e);
        }
    }

    /**
     * Обрабатывает действие "Анализировать" пользователя.
     * 
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
    public void processAnalysisAction(TelegramLongPollingBot bot, Long chatId) {
        log.info("BattleActionService: Обработка действия анализа для chatId={}", chatId);

        try {
            // Отправляем сообщение об анализе
            SendMessage analysisMessage = messageService.createAnalysisMessage(chatId);
            bot.execute(analysisMessage);
            log.debug("BattleActionService: Сообщение об анализе отправлено");

            // Генерируем и применяем награды
            Map<String, Integer> rewards = statService.generateStandardRewards();
            statService.applyStatChanges(chatId, rewards);
            
            log.info("BattleActionService: Действие анализа обработано для chatId={}", chatId);
            
        } catch (TelegramApiException e) {
            log.error("BattleActionService: Ошибка отправки сообщения об анализе для chatId={}", chatId, e);
        }
    }

    /**
     * Обрабатывает действие "Вставить в начало" пользователя.
     * 
     * @param bot — TelegramLongPollingBot для отправки сообщений
     * @param chatId — ID чата пользователя
     */
    public void processInsertBeginningAction(TelegramLongPollingBot bot, Long chatId) {
        log.info("BattleActionService: Обработка действия вставки в начало для chatId={}", chatId);

        try {
            // Отправляем сообщение о вставке
            SendMessage insertMessage = messageService.createInsertBeginningMessage(chatId);
            bot.execute(insertMessage);
            log.debug("BattleActionService: Сообщение о вставке отправлено");
            
            log.info("BattleActionService: Действие вставки в начало обработано для chatId={}", chatId);
            
        } catch (TelegramApiException e) {
            log.error("BattleActionService: Ошибка отправки сообщения о вставке для chatId={}", chatId, e);
        }
    }

    /**
     * Проверяет, может ли пользователь участвовать в бою.
     * 
     * @param chatId — ID чата пользователя
     * @return boolean — true если может участвовать
     */
    public boolean canParticipateInBattle(Long chatId) {
        log.debug("BattleActionService: Проверка возможности участия в бою для chatId={}", chatId);
        
        var user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("BattleActionService: Пользователь или персонаж не найден для chatId={}", chatId);
            return false;
        }
        
        // Проверяем энергию персонажа
        Integer energy = user.getPersonage().getEnergy();
        boolean canParticipate = energy != null && energy > 0;
        
        log.debug("BattleActionService: Результат проверки участия в бою для chatId={}: {}", chatId, canParticipate);
        return canParticipate;
    }

    /**
     * Получает статистику боя для пользователя.
     * 
     * @param chatId — ID чата пользователя
     * @return String — статистика боя
     */
    public String getBattleStats(Long chatId) {
        log.debug("BattleActionService: Получение статистики боя для chatId={}", chatId);
        
        var user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("BattleActionService: Пользователь или персонаж не найден для chatId={}", chatId);
            return "Ошибка: персонаж не найден";
        }

        var personage = user.getPersonage();
        
        StringBuilder stats = new StringBuilder();
        stats.append("Твои статы:\n");
        stats.append("|🏆Level: ").append(personage.getLevel()).append(" ");
        stats.append("|⚡️Энергия: ").append(personage.getEnergy()).append(" ");
        stats.append("|⭐️Очки достижения: ").append(personage.getAchievementPoints()).append(" ");
        stats.append("|💲Деньги: ").append(personage.getCurrency()).append(" ");
        stats.append("|💾 Точность кода: ").append(personage.getCodeAccuracy()).append(" ");
        stats.append("|⚙️ Оптимизация: ").append(personage.getOptimization()).append("\n");
        
        log.debug("BattleActionService: Статистика боя получена для chatId={}", chatId);
        return stats.toString();
    }
} 