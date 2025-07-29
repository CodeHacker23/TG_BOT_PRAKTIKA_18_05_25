package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

/**
 * ArrayListQuizHandler — обработчик ответов на викторины по ArrayList.
 * <p>
 * Этот класс отвечает за:
 * - Проверку правильности ответов на викторины
 * - Начисление/снятие очков за ответы
 * - Отправку персонализированных сообщений о результатах
 * <p>
 * Связи с другими классами:
 * - Использует StatService для изменения статов персонажей
 * - Использует MessageService для отправки сообщений
 * - Использует ArrayListQuiz для получения информации о викторинах
 * <p>
 * Автор: Архитектор (который знает, что викторины — это не просто вопросы)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArrayListQuizHandler {

    private final StatService statService;
    private final MessageService messageService;
    private final ArrayListQuiz arrayListQuiz;

    /**
     * Обрабатывает ответ пользователя на викторину по ArrayList.
     * 
     * @param pollAnswer — ответ пользователя на викторину
     * @param bot — TelegramLongPollingBot для отправки сообщений
     */
    public void handleArrayListQuizAnswer(PollAnswer pollAnswer, TelegramLongPollingBot bot) {
        Long chatId = pollAnswer.getUser().getId();
        int selectedOption = pollAnswer.getOptionIds().get(0);
        int correctOption = 2; // add() - правильный ответ

        log.info("ArrayListQuizHandler: Обработка ответа на викторину для chatId={}, выбранный вариант: {}, правильный: {}", 
                chatId, selectedOption, correctOption);

        if (selectedOption == correctOption) {
            // Правильно! +50 очков достижения
            Map<String, Integer> rewards = Map.of("achievement_points", 50);
            statService.applyStatChanges(chatId, rewards);
            
            // Отправляем сообщение о правильном ответе
            SendMessage correctMessage = createCorrectAnswerMessage(chatId);
            try {
                bot.execute(correctMessage);
                log.info("ArrayListQuizHandler: Правильный ответ! +50 очков достижения для chatId={}", chatId);
            } catch (TelegramApiException e) {
                log.error("ArrayListQuizHandler: Ошибка отправки сообщения о правильном ответе для chatId={}", chatId, e);
            }
        } else {
            // Неправильно! -20 очков достижения
            Map<String, Integer> penalties = Map.of("achievement_points", -20);
            statService.applyStatChanges(chatId, penalties);
            
            // Отправляем сообщение о неправильном ответе
            SendMessage wrongMessage = createWrongAnswerMessage(chatId);
            try {
                bot.execute(wrongMessage);
                log.info("ArrayListQuizHandler: Неправильный ответ! -20 очков достижения для chatId={}", chatId);
            } catch (TelegramApiException e) {
                log.error("ArrayListQuizHandler: Ошибка отправки сообщения о неправильном ответе для chatId={}", chatId, e);
            }
        }
    }

    /**
     * Создает сообщение о правильном ответе.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о правильном ответе
     */
    private SendMessage createCorrectAnswerMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус:*\n\n" +
                "✅ Отлично! Ты знаешь основы ArrayList!\n" +
                "*add()* — правильный метод для добавления элементов.\n\n" +
                "*Награда:* +50 ⭐️ к Очкам Достижения\n\n" +
                "_Продолжай в том же духе, но не забывай про другие методы!_");
        
        return sendMessage;
    }

    /**
     * Создает сообщение о неправильном ответе.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о неправильном ответе
     */
    private SendMessage createWrongAnswerMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус:*\n\n" +
                "❌ Блять, как же ты не знаешь *add()*?!\n" +
                "Это же самый базовый метод ArrayList!\n\n" +
                "*Штраф:* -20 ⭐️ к Очкам Достижения\n\n" +
                "_Иди почитай документацию, а потом возвращайся!_");
        
        return sendMessage;
    }
}


