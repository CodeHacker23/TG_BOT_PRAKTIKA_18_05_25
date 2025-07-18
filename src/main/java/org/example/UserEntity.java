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
    /**
     * Внутренний ID пользователя (PRIMARY KEY)
     * Используется для связи с персонажем (PersonageEntity)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Telegram user ID (уникальный идентификатор пользователя в Telegram)
     */
    @Column(unique = true)
    private Long tgId;

    /**
     * Имя пользователя в Telegram
     */
    private String username;

    /**
     * Текущее состояние пользователя (например, AWAITING_CHARACTER_NAME)
     * Используется для отслеживания прогресса пользователя в боте
     */
    private String state = "AWAITING_CHARACTER_NAME";

    /**
     * Флаг: прошёл ли пользователь сюжетку ArrayList (true — уже был, false — ещё нет)
     */
    private boolean passedArrayList = false;

    /**
     * Связь с персонажем пользователя (PersonageEntity)
     * Один пользователь — один персонаж (OneToOne)
     * mappedBy = "user" означает, что владеющая сторона — PersonageEntity
     */
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private PersonageEntity personage;

    /**
     * Геттер для получения персонажа пользователя
     * @return персонаж пользователя или null, если не создан
     */
    public PersonageEntity getPersonage() {
        return personage;
    }


    // --- Советы по расширению ---
    // 1. Новое поле? Добавь его здесь и в миграцию (ALTER TABLE ...).
    // 2. Не пихай бизнес-логику — только данные.
    // 3. Если добавишь поле без комментария — Архитектор лично напишет тебе в Telegram.
}
