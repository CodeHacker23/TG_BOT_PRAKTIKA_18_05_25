package org.example;

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
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Связь с пользователем (UserEntity)
     * Один пользователь — один персонаж (OneToOne)
     * Внешний ключ user_id в таблице personages
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    /**
     * Тип персонажа (например, Warrior, Mage и т.д.)
     */
    private String characterType;

    /**
     * Имя персонажа
     */
    private String name;

    /**
     * Уровень персонажа
     */
    private int level;

    /**
     * Энергия персонажа
     */
    private int energy;

    /**
     * Очки достижений (NOT NULL)
     * Обязательно указывать при создании персонажа!
     */
    private int achievementPoints;

    /**
     * Внутриигровая валюта
     */
    private double currency;

    /**
     * Статус персонажа (например, активен, в бою и т.д.)
     */
    private String status;

    /**
     * Устойчивость к дедлайнам (характеристика)
     */
    private Integer deadlineResistance;

    /**
     * Аналитика (характеристика)
     */
    private Integer analytics;

    /**
     * Telegram user ID (уникальный идентификатор пользователя в Telegram)
     */
    private Long tgId;

    /** Юмор (уникальное поле Personage2) */
    private Integer humor;
    /** Коммуникации (уникальное поле Personage2) */
    private Integer communication;
    /** Точность кода (уникальное поле Personage3) */
    private Integer codeAccuracy;
    /** Оптимизация (уникальное поле Personage3) */
    private Integer optimization;
    // ... добавь остальные уникальные поля для других персонажей
} 