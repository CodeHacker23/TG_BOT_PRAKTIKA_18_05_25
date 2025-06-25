package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserEntity getOrCreateUser(Long tgId, String username) {
        Optional<UserEntity> existingUser = userRepository.findByTgId(tgId);
        
        if (existingUser.isPresent()) {
            log.debug("Найден существующий пользователь: {}", tgId);
            return existingUser.get();
        }
        
        UserEntity newUser = new UserEntity();
        newUser.setTgId(tgId);
        newUser.setUsername(username != null ? username : "Unknown");
        newUser.setCurrentStage(UserEntity.UserStage.START);
        
        UserEntity savedUser = userRepository.save(newUser);
        log.info("Создан новый пользователь: {} с именем: {}", tgId, username);
        
        return savedUser;
    }

    public void updateUserStage(Long tgId, UserEntity.UserStage stage) {
        Optional<UserEntity> userOpt = userRepository.findByTgId(tgId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setCurrentStage(stage);
            userRepository.save(user);
            log.debug("Обновлен этап пользователя {}: {}", tgId, stage);
        } else {
            log.warn("Пользователь с tgId {} не найден для обновления этапа", tgId);
        }
    }

    public void updateQuizScore(Long tgId, Integer score) {
        Optional<UserEntity> userOpt = userRepository.findByTgId(tgId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            //user.setCurrentQuizScore(score);
            userRepository.save(user);
            log.debug("Обновлен счет викторины пользователя {}: {}", tgId, score);
        }
    }

    public void markTheoryCompleted(Long tgId) {
        Optional<UserEntity> userOpt = userRepository.findByTgId(tgId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            //user.setTheoryCompleted(true);
            userRepository.save(user);
            log.debug("Теория отмечена как завершенная для пользователя: {}", tgId);
        }
    }
} 