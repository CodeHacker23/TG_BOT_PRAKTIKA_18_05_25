package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}

