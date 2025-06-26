package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {// Тут мы уже рабоатем с нашим рипозиторием и вызываем его методы (у репозитория много разных методов, сохранения удаление и получение пользователей и т.д)

    private final UserRepository userRepository;

    public void saveUser (UserEntity user) { // вот это сохранить в бд
        userRepository.save(user);
    }

    public UserEntity getUserById(Long id) { //вот это получить из БД
        return userRepository.getReferenceById(id);
    }

    public void assignPersonageToUser(Long tgId, PersonageBase personage) {
        Optional<UserEntity> userOpt = userRepository.findByTgId(tgId);
        UserEntity user = userOpt.orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setTgId(tgId);
            return newUser;
        });
        user.setCharacterType(personage.getClass().getSimpleName());
        userRepository.save(user);
    }









}

