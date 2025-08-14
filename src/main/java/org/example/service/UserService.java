package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.UserEntity;
import org.example.repository.UserRepository;
import org.example.model.personage.PersonageBase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * UserService — твой личный менеджер пользователей.
 * Здесь происходит вся грязная работа с UserRepository: сохранение, поиск, назначение персонажа.
 *
 * Почему нельзя писать это прямо в контроллере/боте? Потому что иначе твой проект быстро превратится в SpaghettiService.
 *
 * Пример использования:
 *   userService.saveUser(user);
 *   UserEntity user = userService.getUserByTgId(tgId);
 *   userService.assignPersonageToUser(tgId, personage);
 *
 * Если забудешь добавить комментарий — Иларион добавит тебе багов.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    // Тут мы уже работаем с нашим репозиторием и вызываем его методы (у репозитория много разных методов, сохранения удаление и получение пользователей и т.д)
    private final UserRepository userRepository;

    /**
     * Сохраняет пользователя в базе данных
     * @param user — сущность пользователя
     *
     * Пример:
     *   userService.saveUser(user);
     */
    public void saveUser (UserEntity user) {
        System.out.println("[UserService] saveUser() — сохраняем пользователя: tgId=" + user.getTgId());
        userRepository.save(user);
    }

    /**
     * Получить пользователя по ID (из БД)
     *
     * @param id — внутренний ID пользователя
     * @return UserEntity или null, если не найден
     * <p>
     * Пример:
     * UserEntity user = userService.getUserById(42L);
     */
    public UserEntity getUserById(Long id) {
        System.out.println("[UserService] getUserById() — ищем пользователя по id=" + id);
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Получить пользователя по Telegram ID
     * @param tgId — Telegram user ID
     * @return UserEntity или null, если не найден
     *
     * Пример:
     *   UserEntity user = userService.getUserByTgId(123456789L);
     */
    public UserEntity getUserByTgId(Long tgId) {
        System.out.println("[UserService] getUserByTgId() — ищем пользователя по tgId=" + tgId);
        return userRepository.findByTgId(tgId).orElse(null);
    }

    /**
     * Назначить персонажа пользователю по tgId
     * @param tgId — Telegram user ID
     * @param personage — персонаж (PersonageBase)
     *
     * Пример:
     *   userService.assignPersonageToUser(123456789L, new Personage1());
     *
     * Если пользователя нет — создаём нового (да, это магия)
     */
    public void assignPersonageToUser(Long tgId, PersonageBase personage) {
        System.out.println("[UserService] assignPersonageToUser() — назначаем персонажа '" + personage.getClass().getSimpleName() + "' пользователю tgId=" + tgId);
        Optional<UserEntity> userOpt = userRepository.findByTgId(tgId);
        UserEntity user = userOpt.orElseGet(() -> {
            System.out.println("[UserService] assignPersonageToUser() — пользователь не найден, создаём нового!");
            UserEntity newUser = new UserEntity();
            newUser.setTgId(tgId);
            return newUser;
        });
        user.getPersonage().setCharacterType(personage.getClass().getSimpleName());
        userRepository.save(user);
    }

    /**
     * Повысить базовые статы персонажа пользователя по Telegram ID
     */
    public void levelUpUserPersonage(Long tgId) {
        UserEntity user = getUserByTgId(tgId);
        if (user == null || user.getPersonage() == null) return;
        org.example.model.entity.PersonageEntity entity = user.getPersonage();
        org.example.model.personage.PersonageBase base = entity.toPersonageBase();
        if (base == null) return;
        base.levelUp();
        entity.updateFromBase(base);
        saveUser(user);
    }

    // Если добавишь новый метод без комментария — Иларион лично напишет тебе в Telegram.
}

