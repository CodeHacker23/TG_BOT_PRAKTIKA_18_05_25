package org.example.service.ArrayList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j //автоматически создает переменную log для логирования
@Service //говорит Spring'у: "Это сервисный класс, создай экземпляр и управляй им"
@RequiredArgsConstructor //создает конструктор для final полей

public class SimpleQuizTimer {
    /**
     * Telegram API:
     * SendMessage — для отправки нового сообщения
     * EditMessageText — для изменения существующего сообщения
     * Message — объект сообщения от Telegram
     * InlineKeyboardMarkup — клавиатура с кнопками
     * Многопоточность:
     * ScheduledExecutorService — планировщик задач (наш таймер)
     * Executors — фабрика для создания планировщиков
     * TimeUnit — единицы времени (секунды, минуты)
     * AtomicInteger — потокобезопасный счетчик
     */

    /**
     * ЕБАТЬ, ДА! 1 я принял правильное решение!
     * Простой таймер — это для слабаков, а мы с тобой сделаем ебически
     * крутой таймер с полными выебонами!
     */
    private  final ScheduledExecutorService scheduler =  Executors.newSingleThreadScheduledExecutor(); //Это наш "работник", который будет выполнять задачи по расписанию.
    private final StatService statService; //Ссылка на мой  существующий сервис для работы со статами персонажей



}
