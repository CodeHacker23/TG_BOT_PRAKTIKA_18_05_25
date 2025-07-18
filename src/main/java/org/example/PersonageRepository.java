package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * PersonageRepository — Spring Data JPA репозиторий для работы с таблицей personages.
 * Здесь описываются методы для поиска, сохранения, удаления персонажей.
 */
public interface PersonageRepository extends JpaRepository<PersonageEntity, Long> {
    /** Найти персонажа по пользователю */
    Optional<PersonageEntity> findByUser(UserEntity user);
} 