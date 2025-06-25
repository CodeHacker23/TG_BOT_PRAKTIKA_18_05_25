package org.example.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.service.MessageService;
import org.example.service.PhotoService;
import org.example.service.TheoryService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PhotoStageHandler extends StageHandler {

    @Autowired
    public PhotoStageHandler(MessageService messageService,
                             PhotoService photoService,
                             TheoryService theoryService,
                             UserService userService) {
        super(messageService, photoService, theoryService, userService);
    }

    @Override
    public void handle(Long chatId, UserEntity user) {
        log.info("Обработка этапа фото для пользователя: {}", chatId);
        
        // Отправляем мемное фото
        photoService.sendMemePhoto(chatId);
        
        // Отправляем сообщение
        messageService.sendMessage(chatId, "Вот тебе мем! Теперь ты готов к тесту?");
        
        // Обновляем этап пользователя
        updateUserStage(chatId, UserEntity.UserStage.QUIZ);
        
        log.debug("Этап фото завершен для пользователя: {}", chatId);
    }
} 