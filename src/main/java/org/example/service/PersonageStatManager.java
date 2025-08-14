package org.example.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.repository.PersonageRepository;
import org.example.service.ArrayList.StatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * 🎯 ПЕРСОНАЖ СТАТ МЕНЕДЖЕР
 * 
 * Универсальный сервис для работы со статами персонажей.
 * Заменяет дублирующуюся логику во всех MessageService классах.
 * 
 * 🔥 ЧТО ДЕЛАЕТ:
 * - Валидирует пользователя/персонажа (одной строкой!)
 * - Генерирует рандомные награды  
 * - Обновляет статы в БД
 * - Возвращает данные для красивых сообщений
 * 
 * 💡 ИСПОЛЬЗОВАНИЕ:
 * ```java
 * // БЫЛО: 15 строк кода валидации + обновления
 * // СТАЛО: 1 строка!
 * StatUpdateResult result = statManager.updateRandomRewards(chatId, 30, 60, 200, 400);
 * return createMessage(chatId, result.getExpReward(), result.getCashReward());
 * ```
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PersonageStatManager {
    
    private final UserService userService;
    private final StatService statService;
    private final PersonageRepository personageRepository;

    /**
     * 🏆 РЕЗУЛЬТАТ ОБНОВЛЕНИЯ СТАТОВ
     * 
     * Содержит все данные для создания сообщений пользователю.
     */
    @Data
    @AllArgsConstructor
    public static class StatUpdateResult {
        private PersonageEntity personage; // Обновленный персонаж
        private int expReward;             // Сколько опыта получил
        private int cashReward;            // Сколько денег получил
        private boolean success;           // Успешно ли обновление
        private String errorMessage;       // Сообщение об ошибке (если есть)

        // Конструктор для успешного результата
        public StatUpdateResult(PersonageEntity personage, int expReward, int cashReward) {
            this(personage, expReward, cashReward, true, null);
        }

        // Конструктор для ошибки
        public static StatUpdateResult error(String errorMessage) {
            return new StatUpdateResult(null, 0, 0, false, errorMessage);
        }
    }

    /**
     * 🎲 ОБНОВЛЯЕТ СТАТЫ С РАНДОМНЫМИ НАГРАДАМИ
     * 
     * Универсальный метод для всех MessageService классов.
     * Валидирует → Генерирует → Обновляет → Сохраняет → Возвращает результат.
     * 
     * @param chatId - ID чата пользователя
     * @param expMin - минимальная награда опыта  
     * @param expMax - максимальная награда опыта
     * @param cashMin - минимальная награда денег
     * @param cashMax - максимальная награда денег
     * @return StatUpdateResult - результат обновления
     */
    @Transactional
    public StatUpdateResult updateRandomRewards(Long chatId, int expMin, int expMax, int cashMin, int cashMax) {
        log.debug("PersonageStatManager: Обновление статов для chatId={}", chatId);

        // 1. Валидация (одной строкой вместо 5!)
        PersonageEntity personage = validateAndGetPersonage(chatId);
        if (personage == null) {
            return StatUpdateResult.error("Персонаж не найден");
        }

        // 2. Генерация рандомных наград
        int expReward = statService.generateRandomReward(expMin, expMax);
        int cashReward = statService.generateRandomReward(cashMin, cashMax);

        // 3. Обновление статов с логированием ДО изменений
        log.debug("PersonageStatManager: Статы ДО изменений для {}: опыт={}, деньги={}", 
                personage.getName(), personage.getAchievementPoints(), personage.getCurrency());
        
        personage.setAchievementPoints(personage.getAchievementPoints() + expReward);
        personage.setCurrency(personage.getCurrency() + cashReward);

        // 4. Сохранение в БД
        try {
            personageRepository.save(personage);
            log.debug("PersonageStatManager: Статы успешно сохранены в БД для {}", personage.getName());
        } catch (Exception e) {
            log.error("PersonageStatManager: ❌ Ошибка сохранения в БД для {}: {}", personage.getName(), e.getMessage(), e);
            return StatUpdateResult.error("Ошибка сохранения в БД: " + e.getMessage());
        }

        // 5. Проверяем, что статы действительно обновились
        log.debug("PersonageStatManager: Статы ПОСЛЕ изменений для {}: опыт={}, деньги={}", 
                personage.getName(), personage.getAchievementPoints(), personage.getCurrency());

        log.info("PersonageStatManager: Обновлены статы для {}: +{} опыта, +{} денег", 
                personage.getName(), expReward, cashReward);

        return new StatUpdateResult(personage, expReward, cashReward);
    }

    /**
     * 🛡️ ВАЛИДАЦИЯ ПОЛЬЗОВАТЕЛЯ И ПЕРСОНАЖА
     * 
     * Заменяет 5 строк проверок одной строкой.
     * Возвращает персонажа или null если что-то не так.
     * 
     * @param chatId - ID чата
     * @return PersonageEntity или null если ошибка
     */
    @Transactional(readOnly = true)
    private PersonageEntity validateAndGetPersonage(Long chatId) {
        UserEntity user = userService.getUserByTgId(chatId);
        
        if (user == null) {
            log.warn("PersonageStatManager: Пользователь не найден для chatId={}", chatId);
            return null;
        }
        
        if (user.getPersonage() == null) {
            log.warn("PersonageStatManager: Персонаж не найден для пользователя chatId={}", chatId);
            return null;
        }
        
        return user.getPersonage();
    }

    /**
     * 🔧 СОЗДАЕТ СООБЩЕНИЕ ОБ ОШИБКЕ
     * 
     * Стандартное сообщение когда что-то пошло не так.
     * 
     * @param chatId - ID чата
     * @param errorMessage - текст ошибки
     * @return SendMessage с дружелюбной ошибкой
     */
    public SendMessage createErrorMessage(Long chatId, String errorMessage) {
        log.warn("PersonageStatManager: Создание сообщения об ошибке для chatId={}: {}", chatId, errorMessage);
        return new SendMessage(chatId.toString(), "🤖 " + errorMessage + ". Попробуйте /start для начала!");
    }

    /**
     * 🎯 БЫСТРОЕ ОБНОВЛЕНИЕ ДЛЯ СТАНДАРТНЫХ НАГРАД
     * 
     * Метод для частых случаев (30-60 опыта, 200-400 денег).
     * Ещё короче код!
     * 
     * @param chatId - ID чата
     * @return StatUpdateResult - результат
     */
    @Transactional
    public StatUpdateResult updateStandardRewards(Long chatId) {
        return updateRandomRewards(chatId, 30, 60, 200, 400);
    }
}