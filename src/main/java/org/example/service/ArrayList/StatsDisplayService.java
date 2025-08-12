package org.example.service.ArrayList;

import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.PersonageEntity;
import org.springframework.stereotype.Service;

/**
 * StatsDisplayService — универсальный сервис для отображения статистики персонажей.
 * 
 * Этот класс решает проблему дублирования метода buildStatsLine в разных сервисах.
 * Теперь все классы могут использовать один сервис для отображения статов.
 * 
 * Связи с другими классами:
 * - Используется в ArrayListTheoryService для отображения статов
 * - Используется в MessageServiceRound2 для отображения статов
 * - Может использоваться в любом сервисе, которому нужно показать статистику
 * 
 * Автор: Иларион (который ненавидит дублирование кода)
 */
@Slf4j
@Service
public class StatsDisplayService {
    
    /**
     * Формирует строку со статистикой персонажа для отображения.
     * 
     * Этот метод создает читаемую строку с характеристиками персонажа,
     * включая уровень, энергию, очки достижения, деньги и специальные навыки
     * в зависимости от типа персонажа.
     * 
     * @param entity — сущность персонажа
     * @return String — отформатированная строка со статистикой
     * 
     * Пример использования:
     *   String stats = statsDisplayService.buildStatsLine(personageEntity);
     *   message.setText("Твои статы:\n" + stats);
     */
    public String buildStatsLine(PersonageEntity entity) {
        log.debug("StatsDisplayService: Формирование строки статов для персонажа типа: {}", entity.getCharacterType());
        
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
        
        log.debug("StatsDisplayService: Строка статов сформирована: {}", statsLine);
        return statsLine;
    }
    
    /**
     * Хелпер для безопасного вывода Integer значений (null -> 0).
     * 
     * Этот метод предотвращает NullPointerException при работе с Integer полями,
     * которые могут быть null в базе данных.
     * 
     * @param value — Integer значение, которое может быть null
     * @return int — безопасное значение (0 если null, иначе исходное значение)
     * 
     * Пример использования:
     *   int safeValue = statsDisplayService.safe(entity.getAnalytics());
     */
    public int safe(Integer value) {
        return value != null ? value : 0;
    }
}
