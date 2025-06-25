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
public class TheoryStageHandler extends StageHandler {

    @Autowired
    public TheoryStageHandler(MessageService messageService,
                              PhotoService photoService,
                              TheoryService theoryService,
                              UserService userService) {
        super(messageService, photoService, theoryService, userService);
    }

    @Override
    public void handle(Long chatId, UserEntity user) {
        log.info("Обработка этапа теории для пользователя: {}", chatId);
        
        // Отправляем фото теории
        photoService.sendTheoryPhoto(chatId);
        
        // Отправляем теорию
        String theory = theoryService.getArrayListTheory();
        messageService.sendMessage(chatId, theory);
        
        // Обновляем этап пользователя
        updateUserStage(chatId, UserEntity.UserStage.PHOTO);
        
        log.debug("Этап теории завершен для пользователя: {}", chatId);
    }
} 