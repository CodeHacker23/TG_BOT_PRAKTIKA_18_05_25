package org.example.model.entity;

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
     * Связь с персонажем пользователя (PersonageEntity)
     * Один пользователь — один персонаж (OneToOne)
     */
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private PersonageEntity personage;

    /**
     * Текущий сюжет, который проходит пользователь
     */
    private String currentStory;

    /**
     * Имя пользователя в Telegram
     */
    private String username;

    // Остальные поля (state, passedArrayList и т.д.)
    private String state = "AWAITING_CHARACTER_NAME";
    private boolean passedArrayList = false;

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

    @Override
    public String toString() {
        return "UserEntity{" +
            "id=" + id +
            ", tgId=" + tgId +
            ", username='" + username + '\'' +
            // НЕ добавляй personage!
            '}';
    }

    public String getCurrentStory() {
        return currentStory;
    }
    public void setCurrentStory(String currentStory) {
        this.currentStory = currentStory;
    }


    // --- Советы по расширению ---
    // 1. Новое поле? Добавь его здесь и в миграцию (ALTER TABLE ...).
    // 2. Не пихай бизнес-логику — только данные.
    // 3. Если добавишь поле без комментария — Архитектор лично напишет тебе в Telegram.
}
