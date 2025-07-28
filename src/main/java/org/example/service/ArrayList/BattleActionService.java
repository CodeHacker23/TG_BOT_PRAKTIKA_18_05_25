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