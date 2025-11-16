package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.PersonageEntity;
import org.example.repository.PersonageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Random;

/**
 * PersonageService — сервис для обновления характеристик персонажа.
 * Здесь можно централизованно менять любые параметры, чтобы не плодить дублирование по всему проекту.
 * Подходит для всех персонажей (Personage1, Personage2, ...).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PersonageService {
    private final PersonageRepository personageRepository;
    private final Random random = new Random();

    /**
     * Универсальный метод для обновления характеристик персонажа.
     * Можно передавать любые дельты (на сколько увеличить/уменьшить),
     * а для аналитики — если randomAnalytics=true, то начисление будет случайным в заданном диапазоне.
     *
     * @param personage персонаж, которого обновляем
     * @param levelDelta на сколько увеличить уровень
     * @param achievementDelta на сколько увеличить очки достижений
     * @param currencyDelta на сколько увеличить деньги
     * @param analyticsDelta если randomAnalytics=true — это min, иначе просто дельта
     * @param analyticsMax если randomAnalytics=true — это max, иначе игнорируется
     * @param randomAnalytics true — начислять аналитику рандомно
     */


    /**
     * === ИНСТРУКЦИЯ: Как рандомно обновлять характеристики для каждого типа персонажа ===
     *
     * 1. Для каждого типа персонажа (Personage1, Personage2, Personage3) есть свой уникальный параметр:
     *    - Personage1: analytics (аналитика)
     *    - Personage2: communication (коммуникации)
     *    - Personage3: optimization (оптимизация)
     *
     * 2. Чтобы обновить нужный параметр рандомно, используй метод:
     *    int delta = personageService.updateStatWithRandomDelta(personage, "имя_поля", min, max);
     *    // Пример для Personage2:
     *    int communicationDelta = personageService.updateStatWithRandomDelta(personage, "communication", 23, 39);
     *
     * 3. После этого communicationDelta будет содержать фактическую дельту, которую можно показать пользователю.
     *
     * 4. Для Personage1 и Personage3 аналогично:
     *    int analyticsDelta = personageService.updateStatWithRandomDelta(personage, "analytics", 23, 39);
     *    int optimizationDelta = personageService.updateStatWithRandomDelta(personage, "optimization", 23, 39);
     *
     * 5. Не забудь: если будешь обновлять не то поле — Иларион лично напишет тебе в Telegram!
     *
     * === Пример в обработчике команды ===
     *
     * if ("Personage2".equals(type)) {
     *     int communicationDelta = personageService.updateStatWithRandomDelta(personage, "communication", 23, 39);
     *     // ... отправь фото с communicationDelta
     * }
     *
     * === Чёрный юмор ===
     * - Если ты обновишь аналитику у Personage3 — баги будут смеяться над тобой в логах.
     * - Если забудешь логирование — NullPointerException найдёт тебя даже в отпуске.
     */
    public void updateStats(PersonageEntity personage,
                            int levelDelta,
                            int achievementDelta,
                            double currencyDelta,
                            int analyticsDelta,
                            int analyticsMax,
                            boolean randomAnalytics) {
        personage.setLevel(personage.getLevel() + levelDelta);
        personage.setAchievementPoints(personage.getAchievementPoints() + achievementDelta);
        personage.setCurrency(personage.getCurrency() + currencyDelta);
        if (randomAnalytics) {
            int analytics = personage.getAnalytics() != null ? personage.getAnalytics() : 0;
            int randomValue = analyticsDelta + random.nextInt(analyticsMax - analyticsDelta + 1);
            personage.setAnalytics(analytics + randomValue);
        } else {
            int analytics = personage.getAnalytics() != null ? personage.getAnalytics() : 0;
            personage.setAnalytics(analytics + analyticsDelta);
        }
        // --- Теперь ты не просто джун, а ЛЕГИОНЕР, мать его! ---
        personage.setStatus("Легионер"); // Если забудешь это — Иларион лично напишет тебе в Telegram и в БД будет вечный Новобранец
        personageRepository.save(personage);
    }

    /**
     * Универсальный метод для обновления любого int-поля персонажа с рандомной дельтой по имени поля.
     * @param personage персонаж
     * @param fieldName имя поля (например, "analytics", "optimization")
     * @param min минимальное значение дельты
     * @param max максимальное значение дельты
     * @return фактическая дельта, которая была прибавлена
     */
    public int updateStatWithRandomDelta(PersonageEntity personage, String fieldName, int min, int max) {
        int oldValue = 0;
        int newValue = 0;
        int delta = min + random.nextInt(max - min + 1);
        try {
            java.lang.reflect.Field field = PersonageEntity.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(personage);
            oldValue = value != null ? (int) value : 0;
            newValue = oldValue + delta;
            field.set(personage, newValue);
            personageRepository.save(personage);
        } catch (Exception e) {
            log.error("PersonageService: КРИТИЧЕСКАЯ ошибка обновления поля '{}' у персонажа (ID={}): {}", 
                     fieldName, personage.getId(), e.getMessage(), e);
            throw new RuntimeException("Ошибка обновления поля '" + fieldName + "' у персонажа: " + e.getMessage(), e);
        }
        return delta;
    }
} 