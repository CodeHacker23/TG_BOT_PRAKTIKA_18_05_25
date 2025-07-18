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
     * Пример: 42L
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Telegram user ID (уникальный идентификатор пользователя в Telegram)
     * Пример: 123456789L
     */
    @Column(unique = true)
    private Long tgId;

    /**
     * Имя пользователя в Telegram
     * Пример: "@username"
     */
    private String username;

    /**
     * Текущее состояние пользователя (например, AWAITING_CHARACTER_NAME)
     * Используется для отслеживания прогресса пользователя в боте
     * Пример: "AWAITING_CHARACTER_NAME"
     */
    private String state = "AWAITING_CHARACTER_NAME";

    /**
     * Флаг: прошёл ли пользователь сюжетку ArrayList (true — уже был, false — ещё нет)
     * Пример: true
     */
    private boolean passedArrayList = false;

    /**
     * Связь с персонажем пользователя (PersonageEntity)
     * Один пользователь — один персонаж (OneToOne)
     * mappedBy = "user" означает, что владеющая сторона — PersonageEntity
     * Пример:
     *   userEntity.setPersonage(personageEntity);
     */
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private PersonageEntity personage;

    /**
     * Геттер для получения персонажа пользователя
     * @return персонаж пользователя или null, если не создан
     * Пример:
     *   PersonageEntity p = userEntity.getPersonage();
     */
    public PersonageEntity getPersonage() {
        System.out.println("[UserEntity] getPersonage() — возвращаем персонажа: " + personage);
        return personage;
    }
    /**
     * Геттер для получения Telegram user ID
     * @return Long — Telegram user ID
     * Пример:
     *   Long id = userEntity.getTgId();
     */
    public Long getTgId() {
        System.out.println("[UserEntity] getTgId() — возвращаем tgId: " + tgId);
        return tgId;
    }
    /**
     * Сеттер для Telegram user ID
     * @param tgId — новый Telegram user ID
     * Пример:
     *   userEntity.setTgId(123456789L);
     */
    public void setTgId(Long tgId) {
        System.out.println("[UserEntity] setTgId() — устанавливаем tgId: " + tgId);
        this.tgId = tgId;
    }
    /**
     * Геттер для имени пользователя
     * @return String — имя пользователя
     * Пример:
     *   String u = userEntity.getUsername();
     */
    public String getUsername() {
        System.out.println("[UserEntity] getUsername() — возвращаем username: " + username);
        return username;
    }
    /**
     * Сеттер для имени пользователя
     * @param username — новое имя пользователя
     * Пример:
     *   userEntity.setUsername("@username");
     */
    public void setUsername(String username) {
        System.out.println("[UserEntity] setUsername() — устанавливаем username: " + username);
        this.username = username;
    }


    // --- Советы по расширению ---
    // 1. Новое поле? Добавь его здесь и в миграцию (ALTER TABLE ...).
    // 2. Не пихай бизнес-логику — только данные.
    // 3. Если добавишь поле без комментария — Архитектор лично напишет тебе в Telegram.
}
