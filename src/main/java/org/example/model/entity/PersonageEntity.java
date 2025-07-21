package org.example.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * PersonageEntity — отдельная сущность для хранения параметров персонажа пользователя.
 * Все игровые параметры, уникальные поля и статы теперь лежат здесь, а не в UserEntity.
 * Один пользователь — один персонаж (OneToOne).
 *
 * Как расширять:
 *   - Добавь новое поле — не забудь про миграцию в БД!
 *   - Для новых персонажей просто добавляй уникальные поля.
 *
 * Юмор: если начнёшь хранить всё в одной таблице — Архитектор лично напишет тебе в Telegram.
 */
@Entity
@Data
@Table(name = "personages")
public class PersonageEntity {
    /**
     * Внутренний ID персонажа (PRIMARY KEY)
     * Пример: 42L
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Связь с пользователем (UserEntity)
     * Один пользователь — один персонаж (OneToOne)
     * Внешний ключ user_id в таблице personages
     * Пример:
     *   personageEntity.setUser(userEntity);
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;
    /**
     * Telegram user ID (уникальный идентификатор пользователя в Telegram)
     * Пример: 123456789L
     */
    private Long tgId;
    /**
     * Тип персонажа (например, Warrior, Mage и т.д.)
     * Пример: "Mage"
     */
    private String characterType;

    /**
     * Имя персонажа
     * Пример: "Вася"
     */
    private String name;

    /**
     * Уровень персонажа
     * Пример: 5
     */
    private int level;

    /**
     * Энергия персонажа
     * Пример: 10
     */
    private int energy;

    /**
     * Очки достижений (NOT NULL)
     * Обязательно указывать при создании персонажа!
     * Пример: 100
     */
    private int achievementPoints;

    /**
     * Внутриигровая валюта
     * Пример: 42.0
     */
    private double currency;

    /**
     * Статус персонажа (например, активен, в бою и т.д.)
     * Пример: "В бою"
     */
    private String status;

    /**
     * Устойчивость к дедлайнам (характеристика Personage1)
     * Пример: 90
     */
    private Integer deadlineResistance;

    /**
     * Аналитика (характеристика Personage1)
     * Пример: 110
     */
    private Integer analytics;



    /** Юмор (уникальное поле Personage2) */
    private Integer humor;
    /** Коммуникации (уникальное поле Personage2) */
    private Integer communication;
    /** Точность кода (уникальное поле Personage3) */
    private Integer codeAccuracy;
    /** Оптимизация (уникальное поле Personage3) */
    private Integer optimization;
    // ... добавь остальные уникальные поля для других персонажей

    // --- Кастомные геттеры/сеттеры с логами для примера ---
    /**
     * Получить имя персонажа
     * @return String — имя персонажа
     * Пример:
     *   String n = personageEntity.getName();
     */
    public String getName() {
        System.out.println("[PersonageEntity] getName() — возвращаем имя: " + name);
        return name;
    }
    /**
     * Установить имя персонажа
     * @param name — новое имя
     * Пример:
     *   personageEntity.setName("Вася");
     */
    public void setName(String name) {
        System.out.println("[PersonageEntity] setName() — устанавливаем имя: " + name);
        this.name = name;
    }
    /**
     * Получить уровень персонажа
     * @return int — уровень
     * Пример:
     *   int lvl = personageEntity.getLevel();
     */
    public int getLevel() {
        System.out.println("[PersonageEntity] getLevel() — возвращаем уровень: " + level);
        return level;
    }
    /**
     * Установить уровень персонажа
     * @param level — новый уровень
     * Пример:
     *   personageEntity.setLevel(5);
     */
    public void setLevel(int level) {
        System.out.println("[PersonageEntity] setLevel() — устанавливаем уровень: " + level);
        this.level = level;
    }
    // ... по аналогии можно добавить для других ключевых полей ...
} 