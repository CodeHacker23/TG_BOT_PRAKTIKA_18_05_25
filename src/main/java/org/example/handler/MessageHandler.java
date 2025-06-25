package org.example.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.keyboard.KeyboardService;
import org.example.service.MessageService;
import org.example.service.PhotoService;
import org.example.service.QuizService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandler {
    private final MessageService messageService;
    private final PhotoService photoService;
    private final QuizService quizService;
    private final KeyboardService keyboardService;
    private final UserService userService;
    private final TheoryStageHandler theoryStageHandler;
    private final PhotoStageHandler чистую сборку (clean и install;

    public void handleMessage(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();
        
        log.info("Получено сообщение от пользователя {}: {}", chatId, text);
        
        UserEntity user = userService.getOrCreateUser(chatId, message.getFrom().getUserName());

        switch (text) {
            case "/start" -> handleStartCommand(chatId, user);
            case "🚀 Начать с теории" -> theoryStageHandler.handle(chatId, user);
            case "💡 Пройти тест на знания" -> handleQuizCommand(chatId, user);
            case "🎮 Испытать квест" -> handleQuestCommand(chatId, user);
            case "Да" -> handleYesAnswer(chatId, user);
            case "Нет" -> handleNoAnswer(chatId, user);
            case "Я изучаю пайтон" -> handlePythonAnswer(chatId, user);
            default -> handleUnknownCommand(chatId, text);
        }
    }

    private void handleStartCommand(Long chatId, UserEntity user) {
        log.info("Обработка команды /start для пользователя: {}", chatId);
        
        photoService.sendStartPhoto(chatId);
        
        String welcomeText = "Ты попал в мир Java-коллекций — Увлекательного приключения!\n" +
                "Где ты узнаешь, как работают ArrayList, LinkedList, и др структуры данных. \n" +
                "В этом мире тебе предстоит сражаться за уровни, решать задачи, собирать знания, искать пасхалки и получать боевые награды! 🏆 \n" +
                "\n🔹 Начни с теории — если ты новичок! \n" +
                "🔹 Пройти тест — если чувствуешь себя уверенно.\n" +
                "🔹 Испытать квест — если любишь головоломки и приключения.\n" +
                "Готов начать? Тогда выбери свой первый путь воин!";
        
        messageService.sendMessageWithKeyboard(chatId, welcomeText, keyboardService.createStartKeyboard());
        
        userService.updateUserStage(chatId, UserEntity.UserStage.START);
    }

    private void handleQuizCommand(Long chatId, UserEntity user) {
        log.info("Обработка команды викторины для пользователя: {}", chatId);
        quizService.sendAdvancedQuiz(chatId);
        userService.updateUserStage(chatId, UserEntity.UserStage.QUIZ);
    }

    private void handleQuestCommand(Long chatId, UserEntity user) {
        log.info("Обработка команды квеста для пользователя: {}", chatId);
        messageService.sendMessage(chatId, "Квест пока в разработке! 🚧");
    }

    private void handleYesAnswer(Long chatId, UserEntity user) {
        log.info("Обработка ответа 'Да' для пользователя: {}", chatId);
        theoryStageHandler.handle(chatId, user);
    }

    private void handleNoAnswer(Long chatId, UserEntity user) {
        log.info("Обработка ответа 'Нет' для пользователя: {}", chatId);
        quizService.sendArrayListQuiz(chatId);
        messageService.sendMessageWithKeyboard(chatId, "Хотите прочитать теорию о ArrayList?", 
                keyboardService.createYesNoPythonKeyboard());
    }

    private void handlePythonAnswer(Long chatId, UserEntity user) {
        log.info("Обработка ответа 'Я изучаю пайтон' для пользователя: {}", chatId);
        photoService.sendPythonPhoto(chatId);
        messageService.sendMessage(chatId, "Согласен?!");
    }

    private void handleUnknownCommand(Long chatId, String text) {
        log.warn("Неизвестная команда от пользователя {}: {}", chatId, text);
        messageService.sendMessage(chatId, "Неизвестная команда. Используйте /start для начала.");
    }
} 