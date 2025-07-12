package org.example;

import jakarta.persistence.*;
import lombok.Data;

/**
 * UserEntity — сущность пользователя для хранения в базе данных.
 * Здесь описываются все поля, которые будут храниться в таблице users.
 *
 * Как расширять:
 *   - Новое поле? Добавь его здесь, не забудь про миграцию в БД!
 *   - Не пихай бизнес-логику в сущность — только данные.
 *
 * Пример использования:
 *   UserEntity user = new UserEntity();
 *   user.setTgId(123456789L);
 *   user.setCharacterType("Personage1");
 *
 * Юмор: если добавишь бизнес-логику в UserEntity — Архитектор лично напишет тебе в Telegram.
 */
@Entity
@Data
@Table(name = "users")
public class UserEntity {
    /** Внутренний ID пользователя (PRIMARY KEY) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Telegram user ID (уникальный) */
    @Column(unique = true)
    private Long tgId;

    /** Имя пользователя в Telegram */
    private String username;

    /** Тип выбранного персонажа (Personage1, Personage2, ...) */
    private String characterType;

    /** Имя персонажа, заданное пользователем */
    private String characterName;

    /** Текущее состояние пользователя (например, AWAITING_CHARACTER_NAME) */
    private String state = "AWAITING_CHARACTER_NAME";

    /** Энергия пользователя (например, на сутки) */
    private int energy;

    // --- Советы по расширению ---
    // 1. Новое поле? Добавь его здесь и в миграцию (ALTER TABLE ...).
    // 2. Не пихай бизнес-логику — только данные.
    // 3. Если добавишь поле без комментария — Архитектор лично напишет тебе в Telegram.
}
