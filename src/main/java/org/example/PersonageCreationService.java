package org.example;

import org.springframework.stereotype.Service;

@Service
public class PersonageCreationService {
    private final UserService userService;

    public PersonageCreationService(UserService userService) {
        this.userService = userService;
    }

    public boolean hasCharacter(UserEntity user) {
        return user != null
            && user.getCharacterType() != null && !user.getCharacterType().isEmpty()
            && user.getCharacterName() != null && !user.getCharacterName().isEmpty();
    }

    /**
     * Перевести пользователя в режим создания персонажа, если персонажа нет. Возвращает true, если можно создавать, false если уже есть.
     */
    public boolean startCharacterCreation(UserEntity user) {
        if (hasCharacter(user)) {
            user.setState(null);
            userService.saveUser(user);
            return false;
        }
        user.setState("AWAITING_CHARACTER_NAME");
        userService.saveUser(user);
        return true;
    }

    /**
     * Обработка запроса на создание персонажа по userId. Возвращает пару (user, можно ли создавать)
     */
    public CharacterCreationResult handleCreatePersonageRequest(Long tgId) {
        UserEntity user = userService.getUserByTgId(tgId);
        if (user == null) {
            user = new UserEntity();
            user.setTgId(tgId);
        }
        boolean canCreate = startCharacterCreation(user);
        return new CharacterCreationResult(user, canCreate);
    }

    public static class CharacterCreationResult {
        public final UserEntity user;
        public final boolean canCreate;
        public CharacterCreationResult(UserEntity user, boolean canCreate) {
            this.user = user;
            this.canCreate = canCreate;
        }
    }
} 