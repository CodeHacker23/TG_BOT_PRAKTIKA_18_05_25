package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * UserRepository — Spring Data JPA репозиторий для работы с таблицей users.
 * Здесь описываются методы для поиска, сохранения, удаления пользователей.
 *
 * Как расширять:
 *   - Новый способ поиска? Добавь метод с правильным именованием (findByXxx).
 *   - Не пихай бизнес-логику — только запросы к БД.
 *
 * Пример использования:
 *   Optional<UserEntity> userOpt = userRepository.findByTgId(123456789L);
 *   UserEntity user = userRepository.findByUsername("vasya");
 *
 * Юмор: если начнёшь писать SQL вручную в репозитории — Архитектор лично напишет тебе в Telegram.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * Найти пользователя по Telegram ID
     * @param tgId — Telegram user ID
     * @return Optional<UserEntity> — пользователь, если найден
     * Пример:
     *   Optional<UserEntity> userOpt = userRepository.findByTgId(123456789L);
     */
    Optional<UserEntity> findByTgId(Long tgId);

    /**
     * Найти пользователя по username
     * @param username — имя пользователя
     * @return UserEntity — пользователь, если найден
     * Пример:
     *   UserEntity user = userRepository.findByUsername("vasya");
     */
    UserEntity findByUsername(String username);

    // --- Советы по расширению ---
    // 1. Новый способ поиска? Добавь метод findByXxx.
    // 2. Не пихай бизнес-логику — только запросы к БД.
    // 3. Если добавишь метод без комментария — Архитектор лично напишет тебе в Telegram.
    // Если потребуется сложная логика — создай кастомный репозиторий и реализуй там с логами.
}
