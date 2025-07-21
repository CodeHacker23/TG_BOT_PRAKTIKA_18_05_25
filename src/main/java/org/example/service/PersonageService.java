package org.example.service;

import org.example.model.entity.PersonageEntity;
import org.example.repository.PersonageRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Random;

/**
 * PersonageService — сервис для обновления характеристик персонажа.
 * Здесь можно централизованно менять любые параметры, чтобы не плодить дублирование по всему проекту.
 * Подходит для всех персонажей (Personage1, Personage2, ...).
 */
@Service
@RequiredArgsConstructor
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
        personage.setStatus("Легионер"); // Если забудешь это — Архитектор лично напишет тебе в Telegram и в БД будет вечный Новобранец
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
            throw new RuntimeException("Ошибка обновления поля '" + fieldName + "' у персонажа: " + e.getMessage(), e);
        }
        return delta;
    }
} 