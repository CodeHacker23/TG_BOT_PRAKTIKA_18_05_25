package org.example;

import org.springframework.stereotype.Service;

/**
 * PersonageCreationService — фабрика по созданию персонажей и надзиратель за их уникальностью.
 * Здесь решается, может ли пользователь создать персонажа, и если да — переводим его в нужное состояние.
 *
 * Почему нельзя делать это прямо в боте? Потому что иначе твой код будет как RPG без сюжета: много действий, а смысла ноль.
 *
 * Пример использования:
 *   boolean canCreate = personageCreationService.startCharacterCreation(user);
 *   CharacterCreationResult result = personageCreationService.handleCreatePersonageRequest(tgId);
 *
 * Если забудешь добавить комментарий — Архитектор создаст тебе персонажа с именем "Баг".
 */
@Service
public class PersonageCreationService {
    private final UserService userService;

    public PersonageCreationService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Проверка, есть ли у пользователя персонаж
     * @param user — сущность пользователя
     * @return true, если персонаж уже есть
     *
     * Пример:
     *   if (personageCreationService.hasCharacter(user)) { ... }
     */
    public boolean hasCharacter(UserEntity user) {
        boolean result = user != null
                && user.getPersonage() != null && user.getPersonage().getCharacterType() != null && !user.getPersonage().getCharacterType().isEmpty()
                && user.getPersonage() != null && user.getPersonage().getName() != null && !user.getPersonage().getName().isEmpty();
        System.out.println("[PersonageCreationService] hasCharacter() — userId=" + (user != null ? user.getTgId() : null) + ", result=" + result);
        return result;
    }

    /**
     * Перевести пользователя в режим создания персонажа, если персонажа нет. Возвращает true, если можно создавать, false если уже есть.
     * @param user — сущность пользователя
     * @return true, если можно создавать персонажа
     *
     * Пример:
     *   boolean canCreate = personageCreationService.startCharacterCreation(user);
     */
    public boolean startCharacterCreation(UserEntity user) {
        if (hasCharacter(user)) {
            user.setState(null);
            userService.saveUser(user);
            System.out.println("[PersonageCreationService] startCharacterCreation() — персонаж уже есть, создание запрещено.");
            return false;
        }
        user.setState("AWAITING_CHARACTER_NAME");
        userService.saveUser(user);
        System.out.println("[PersonageCreationService] startCharacterCreation() — переводим пользователя в режим ожидания имени персонажа.");
        return true;
    }

    /**
     * Обработка запроса на создание персонажа по userId. Возвращает пару (user, можно ли создавать)
     * @param tgId — Telegram user ID
     * @return CharacterCreationResult (user, можно ли создавать)
     *
     * Пример:
     *   CharacterCreationResult result = personageCreationService.handleCreatePersonageRequest(123456789L);
     */
    public CharacterCreationResult handleCreatePersonageRequest(Long tgId) {
        UserEntity user = userService.getUserByTgId(tgId);
        if (user == null) {
            System.out.println("[PersonageCreationService] handleCreatePersonageRequest() — пользователь не найден, создаём нового!");
            user = new UserEntity();
            user.setTgId(tgId);
        }
        boolean canCreate = startCharacterCreation(user);
        System.out.println("[PersonageCreationService] handleCreatePersonageRequest() — userId=" + tgId + ", canCreate=" + canCreate);
        return new CharacterCreationResult(user, canCreate);
    }

    /**
     * Результат попытки создания персонажа: содержит пользователя и флаг, можно ли создавать
     */
    public static class CharacterCreationResult {
        public final UserEntity user;
        public final boolean canCreate;

        public CharacterCreationResult(UserEntity user, boolean canCreate) {
            this.user = user;
            this.canCreate = canCreate;
        }
    }

    // Если добавишь новый метод без комментария — Архитектор лично напишет тебе в Telegram.
} 