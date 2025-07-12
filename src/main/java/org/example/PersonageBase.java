package org.example;

import lombok.Data;
import java.util.List;
import java.util.Random;

/**
 * PersonageBase — абстрактный базовый класс для всех персонажей.
 * Здесь определяются общие поля и методы для всех героев (имя, уровень, энергия, очки достижений, деньги, статус).
 *
 * Почему нужен базовый класс? Потому что если начнёшь дублировать поля в каждом персонаже — твой код быстро превратится в ад.
 *
 * Пример расширения:
 *   - Хочешь добавить нового персонажа? Просто создай класс, унаследованный от PersonageBase, и добавь уникальные поля/методы.
 *   - Все общие методы (например, изменение энергии) — только здесь!
 *
 * Юмор: если скопируешь поля из PersonageBase в Personage4 — Архитектор лично напишет тебе в Telegram.
 */
@Data
public abstract class PersonageBase {
    /** Имя персонажа */
    protected String name;
    /** Уровень персонажа */
    protected int level;
    /** Энергия персонажа */
    protected int energy;
    /** Очки достижений */
    protected int achievementPoints;
    /** Деньги персонажа */
    protected double currency;
    /** Статус персонажа (например, Новобранец) */
    protected String status;

    /**
     * Изменить уровень персонажа
     * @param delta — на сколько изменить
     */
    public void changeLevel(int delta) {
        this.level += delta;
    }

    /**
     * Изменить энергию персонажа
     * @param delta — на сколько изменить
     */
    public void energy(int delta) {
        this.energy += delta;
    }

    /**
     * Изменить очки достижений
     * @param delta — на сколько изменить
     */
    public void changeAchievementPoints(int delta) {
        this.achievementPoints += delta;
    }

    /**
     * Изменить количество денег
     * @param delta — на сколько изменить
     */
    public void changeCurrency(int delta) {
        this.currency += delta;
    }

    /**
     * Установить имя персонажа
     * @param name — имя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получить случайного персонажа (используется при создании нового героя)
     * @return PersonageBase — случайный персонаж (Personage1, 2 или 3)
     *
     * Пример:
     *   PersonageBase p = PersonageBase.getRandomPersonage();
     */
    public static PersonageBase getRandomPersonage() {
        List<PersonageBase> personages = List.of(new Personage1(), new Personage2(), new Personage3());
        PersonageBase chosen = personages.get(new Random().nextInt(personages.size()));
        System.out.println("[PersonageBase] getRandomPersonage() — выбран персонаж: " + chosen.getClass().getSimpleName());
        return chosen;
    }

    // --- Советы по расширению ---
    // 1. Все новые персонажи должны наследоваться от PersonageBase.
    // 2. Общие поля и методы — только здесь, уникальные — в дочерних классах.
    // 3. Не копипасть! Если логика повторяется — выноси в базу.
    // 4. Если добавишь персонажа без комментария — Архитектор лично напишет тебе в Telegram.
}
