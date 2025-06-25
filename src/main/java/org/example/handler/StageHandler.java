package org.example.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.service.MessageService;
import org.example.service.PhotoService;
import org.example.service.TheoryService;
import org.example.service.UserService;

@Slf4j
@RequiredArgsConstructor
public abstract class StageHandler {
    protected final MessageService messageService;
    protected final PhotoService photoService;
    protected final TheoryService theoryService;
    protected final UserService userService;

    public abstract void handle(Long chatId, UserEntity user);

    protected void sendMessage(Long chatId, String text) {
        messageService.sendMessage(chatId, text);
    }

    protected void sendPhoto(Long chatId, String photoUrl) {
        photoService.sendMemePhoto(chatId);
    }

    protected void updateUserStage(Long chatId, UserEntity.UserStage stage) {
        userService.updateUserStage(chatId, stage);
    }
} 