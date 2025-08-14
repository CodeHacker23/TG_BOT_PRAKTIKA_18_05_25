package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;

/**
 * StatService — универсальный сервис для работы со статами персонажей в Telegram боте.
 * <p>
 * Этот класс отвечает за:
 * - Генерацию случайных наград в заданных диапазонах
 * - Применение изменений к статам персонажа с null-безопасностью
 * - Сохранение прогресса в базе данных через UserService
 * - Определение индивидуальных статов для разных типов персонажей
 * - Генерацию кастомных изменений (деньги уменьшаются, другие статы растут)
 * - Получение информации для отображения статов
 * <p>
 * Связи с другими классами:
 * - Использует UserService для получения и сохранения данных пользователей
 * - Используется в ArrayListStory для применения наград
 * - Используется в MessageService для отображения изменений статов
 * - Работает с PersonageEntity для изменения статов персонажей
 * <p>
 * Поддерживаемые статы:
 * - achievement_points — очки достижения
 * - currency — деньги персонажа
 * - analytics — аналитика (для Personage1)
 * - communication — коммуникация (для Personage2)
 * - code_accuracy — точность кода (для Personage3)
 * - optimization — оптимизация
 * - humor — юмор
 * <p>
 * Безопасность:
 * - Проверяет null значения перед арифметическими операциями
 * - Использует значения по умолчанию (0) для null полей
 * - Логирует все изменения для отладки
 * - Обрабатывает ошибки при отсутствии пользователя/персонажа
 * <p>
 * Автор: Иларион (который знает, что статы без логики — это просто цифры)
 * <p>
 * Пример использования:
 * 
 * @Autowired private StatService statService;
 * 
 * // Генерация стандартных наград
 * Map<String, Integer> rewards = statService.generateStandardRewards();
 * statService.applyStatChanges(chatId, rewards);
 * 
 * // Генерация кастомных изменений
 * Map<String, Integer> changes = statService.generateCustomStatChanges();
 * statService.applyStatChanges(chatId, changes);
 * 
 * // Получение индивидуального стата для персонажа
 * String individualStat = statService.getIndividualStatForCharacter("Personage1");
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StatService {

    private final UserService userService;

    /**
     * Генерирует случайное число в заданном диапазоне.
     * 
     * @param min — минимальное значение
     * @param max — максимальное значение
     * @return int — случайное число
     */
    public int generateRandomReward(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    /**
     * Генерирует стандартные награды для боевых действий.
     * 
     * @return Map<String, Integer> — карта наград
     */
    public Map<String, Integer> generateStandardRewards() {
        Map<String, Integer> rewards = new HashMap<>();
        rewards.put("achievement_points", generateRandomReward(30, 60));
        rewards.put("currency", generateRandomReward(200, 400));
        return rewards;
    }

    /**
     * Генерирует кастомные изменения статов (деньги уменьшаются, другие статы растут).
     * 
     * @return Map<String, Integer> — карта изменений
     */
    public Map<String, Integer> generateCustomStatChanges() {
        Map<String, Integer> changes = new HashMap<>();
        
        // Уменьшаем деньги (отрицательное значение)
        changes.put("money", -generateRandomReward(150, 200));
        
        // Увеличиваем очки достижения
        changes.put("achievement_points", generateRandomReward(30, 50));
        
        log.debug("StatService: Сгенерированы кастомные изменения статов: {}", changes);
        return changes;
    }

    /**
     * Применяет изменения статов к персонажу и сохраняет в БД.
     * 
     * @param chatId — ID чата пользователя
     * @param statChanges — карта изменений статов
     */
    public void applyStatChanges(Long chatId, Map<String, Integer> statChanges) {
        log.debug("StatService: Применение изменений статов для chatId={}, изменения: {}", 
                chatId, statChanges);

        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("StatService: Пользователь или персонаж не найден для chatId={}", chatId);
            return;
        }

        PersonageEntity entity = user.getPersonage();

        // Применяем изменения к статам
        for (Map.Entry<String, Integer> entry : statChanges.entrySet()) {
            String statName = entry.getKey();
            Integer change = entry.getValue();

            switch (statName) {
                case "currency":
                    Double currentCurrency = entity.getCurrency();
                    entity.setCurrency((currentCurrency != null ? currentCurrency : 0.0) + change);
                    log.debug("StatService: Изменены деньги на {} для chatId={}", change, chatId);
                    break;
                case "analytics":
                    Integer currentAnalytics = entity.getAnalytics();
                    entity.setAnalytics((currentAnalytics != null ? currentAnalytics : 0) + change);
                    log.debug("StatService: Изменена аналитика на {} для chatId={}", change, chatId);
                    break;
                case "optimization":
                    Integer currentOptimization = entity.getOptimization();
                    entity.setOptimization((currentOptimization != null ? currentOptimization : 0) + change);
                    log.debug("StatService: Изменена оптимизация на {} для chatId={}", change, chatId);
                    break;
                case "code_accuracy":
                    Integer currentCodeAccuracy = entity.getCodeAccuracy();
                    entity.setCodeAccuracy((currentCodeAccuracy != null ? currentCodeAccuracy : 0) + change);
                    log.debug("StatService: Изменена точность кода на {} для chatId={}", change, chatId);
                    break;
                case "communication":
                    Integer currentCommunication = entity.getCommunication();
                    entity.setCommunication((currentCommunication != null ? currentCommunication : 0) + change);
                    log.debug("StatService: Изменена коммуникация на {} для chatId={}", change, chatId);
                    break;
                case "humor":
                    Integer currentHumor = entity.getHumor();
                    entity.setHumor((currentHumor != null ? currentHumor : 0) + change);
                    log.debug("StatService: Изменен юмор на {} для chatId={}", change, chatId);
                    break;
                case "achievement_points":
                    Integer currentAchievementPoints = entity.getAchievementPoints();
                    entity.setAchievementPoints((currentAchievementPoints != null ? currentAchievementPoints : 0) + change);
                    log.debug("StatService: Изменены очки достижения на {} для chatId={}", change, chatId);
                    break;
                default:
                    log.warn("StatService: Неизвестный стат '{}' для chatId={}", statName, chatId);
            }
        }

        // Сохраняем изменения в БД
        userService.saveUser(user);
        log.info("StatService: Изменения статов применены для chatId={}", chatId);
    }

    /**
     * Получает индивидуальный стат для конкретного типа персонажа.
     * 
     * @param characterType — тип персонажа
     * @return String — название индивидуального стата
     */
    public String getIndividualStatForCharacter(String characterType) {
        switch (characterType) {
            case "Personage1":
                return "analytics"; // Аналитика для Personage1
            case "Personage2":
                return "communication"; // Коммуникация для Personage2
            case "Personage3":
                return "code_accuracy"; // Точность кода для Personage3
            default:
                log.warn("StatService: Неизвестный тип персонажа: {}", characterType);
                return "analytics"; // По умолчанию аналитика
        }
    }

    /**
     * Получает эмодзи и название для отображения стата.
     * 
     * @param statName — название стата
     * @return String — эмодзи + название для отображения
     */
    public String getStatDisplayInfo(String statName) {
        switch (statName) {
            case "analytics":
                return "📊 к Аналитике";
            case "optimization":
                return "⚙️ к Оптимизации";
            case "code_accuracy":
                return "🎯 к Точности кода";
            case "communication":
                return "💬 к Коммуникации";
            case "humor":
                return "😄 к Юмору";
            default:
                return "📊 к Аналитике";
        }
    }
} 