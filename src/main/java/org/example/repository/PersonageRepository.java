package org.example.repository;

import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * PersonageRepository — Spring Data JPA репозиторий для работы с таблицей personages.
 * Здесь описываются методы для поиска, сохранения, удаления персонажей.
 *
 * Как расширять:
 *   - Новый способ поиска? Добавь метод findByXxx.
 *   - Не пихай бизнес-логику — только запросы к БД.
 *
 * Пример использования:
 *   Optional<PersonageEntity> opt = personageRepository.findByUser(userEntity);
 *
 * Юмор: если начнёшь писать SQL вручную — Иларион лично напишет тебе в Telegram.
 */
@Transactional
public interface PersonageRepository extends JpaRepository<PersonageEntity, Long> {
    /**
     * Найти персонажа по пользователю
     * @param user — сущность пользователя
     * @return Optional<PersonageEntity> — персонаж, если найден
     * Пример:
     *   Optional<PersonageEntity> opt = personageRepository.findByUser(userEntity);
     */
    Optional<PersonageEntity> findByUser(UserEntity user);
    // Если потребуется сложная логика — создай кастомный репозиторий и реализуй там с логами.
}