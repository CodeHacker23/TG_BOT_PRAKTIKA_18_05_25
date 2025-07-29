package org.example.service.ArrayList;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ArrayListQuiz — сервис для создания викторин по ArrayList.
 * <p>
 * Этот класс отвечает за:
 * - Создание викторин по ArrayList
 * - Хранение правильных ответов
 * - Форматирование вопросов и вариантов ответов
 * <p>
 * Связи с другими классами:
 * - Используется в ArrayListQuizHandler для проверки ответов
 * - Используется в ArrayListStory для отправки викторин
 * <p>
 * Автор: Архитектор (который знает, что викторины должны быть интересными)
 */
@Slf4j
@Service
public class ArrayListQuiz {
    
    private final Map<Long, Integer> correctAnswers = new ConcurrentHashMap<>();

    /**
     * Создает викторину по ArrayList.
     * 
     * @param chatId — ID чата Telegram
     * @return SendPoll — объект викторины
     *
     * Пример:
     *   SendPoll poll = arrayListQuiz.getArrayListQuiz(chatId);
     *   bot.execute(poll);
     */
    public SendPoll getArrayListQuiz(Long chatId) {
        log.info("ArrayListQuiz: Создание викторины по ArrayList для chatId={}", chatId);
        
        SendPoll poll = new SendPoll();
        poll.setIsAnonymous(false); // НЕАНОНИМНАЯ викторина для отслеживания ответов
        int correctOption = 2; // add() - правильный ответ
        
        poll.setChatId(chatId);
        poll.setQuestion("Какой метод добавляет элемент в ArrayList?");
        poll.setOptions(Arrays.asList("abb()", "insert()", "add()", "push()"));
        poll.setCorrectOptionId(correctOption);
        poll.setType("quiz");
        poll.setExplanation("БЛЯТЬ");
        
        // Сохраняем правильный ответ для проверки
        correctAnswers.put(chatId, correctOption);
        
        log.info("ArrayListQuiz: Викторина создана для chatId={}, правильный ответ: {}", chatId, correctOption);
        return poll;
    }

    /**
     * Получает правильный ответ для викторины.
     * 
     * @param chatId — ID чата
     * @return Integer — номер правильного варианта ответа
     */
    public Integer getCorrectAnswer(Long chatId) {
        return correctAnswers.get(chatId);
    }

    /**
     * Удаляет правильный ответ после проверки.
     * 
     * @param chatId — ID чата
     */
    public void removeCorrectAnswer(Long chatId) {
        correctAnswers.remove(chatId);
        log.debug("ArrayListQuiz: Удален правильный ответ для chatId={}", chatId);
    }
}
