package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Утилитный класс для отправки дружелюбных сообщений об ошибках пользователям.
 * 
 * Основные принципы:
 * - Техническую информацию логируем, а не отправляем пользователю
 * - Пользователю показываем понятные и дружелюбные сообщения
 * - Предлагаем конкретные действия для решения проблемы
 */
@Slf4j
public class ErrorMessageUtil {

    // Список дружелюбных сообщений об ошибках с эмодзи
    private static final List<String> FRIENDLY_ERROR_MESSAGES = Arrays.asList(
            "🤖 Что-то пошло не так! Попробуйте ещё раз через пару секунд.",
            "⚡ Небольшая техническая неполадка. Повторите попытку!",
            "🔧 Произошла временная ошибка. Попробуйте команду /start",
            "🚀 Сейчас что-то не работает, но мы уже это исправляем! Попробуйте позже.",
            "💫 Упс! Попробуйте ещё раз или начните сначала с /start"
    );

    private static final Random random = new Random();

    /**
     * Отправляет дружелюбное сообщение об ошибке пользователю и логирует техническую информацию.
     * 
     * @param bot — Telegram бот для отправки сообщения
     * @param chatId — ID чата пользователя  
     * @param userId — ID пользователя (для логирования)
     * @param technicalError — техническое описание ошибки (только для логов)
     * @param context — контекст где произошла ошибка (например, "CallbackQueryHandler")
     */
    public static void sendFriendlyErrorMessage(TelegramLongPollingBot bot, Long chatId, Long userId, 
                                               String technicalError, String context) {
        // Логируем техническую ошибку для разработчика
        log.error("{}: Ошибка для userId={}, chatId={}: {}", context, userId, chatId, technicalError);
        
        // Отправляем дружелюбное сообщение пользователю
        try {
            String friendlyMessage = getRandomFriendlyMessage();
            bot.execute(new SendMessage(chatId.toString(), friendlyMessage));
        } catch (TelegramApiException e) {
            log.error("{}: Не удалось отправить дружелюбное сообщение об ошибке для userId={}: {}", 
                     context, userId, e.getMessage());
        }
    }

    /**
     * Отправляет дружелюбное сообщение об ошибке с исключением.
     * 
     * @param bot — Telegram бот
     * @param chatId — ID чата
     * @param userId — ID пользователя  
     * @param exception — исключение для логирования
     * @param context — контекст ошибки
     */
    public static void sendFriendlyErrorMessage(TelegramLongPollingBot bot, Long chatId, Long userId,
                                               Exception exception, String context) {
        sendFriendlyErrorMessage(bot, chatId, userId, exception.getMessage(), context);
    }

    /**
     * Логирует ошибку без отправки сообщения пользователю.
     * Используется когда пользователь не должен знать об ошибке.
     * 
     * @param userId — ID пользователя
     * @param chatId — ID чата
     * @param error — описание ошибки
     * @param context — контекст ошибки
     */
    public static void logSilentError(Long userId, Long chatId, String error, String context) {
        log.error("{}: Скрытая ошибка для userId={}, chatId={}: {}", context, userId, chatId, error);
    }

    /**
     * Логирует ошибку с исключением без отправки сообщения пользователю.
     */
    public static void logSilentError(Long userId, Long chatId, Exception exception, String context) {
        log.error("{}: Скрытая ошибка для userId={}, chatId={}: {}", 
                 context, userId, chatId, exception.getMessage(), exception);
    }

    /**
     * Возвращает случайное дружелюбное сообщение об ошибке.
     */
    private static String getRandomFriendlyMessage() {
        return FRIENDLY_ERROR_MESSAGES.get(random.nextInt(FRIENDLY_ERROR_MESSAGES.size()));
    }

    /**
     * Отправляет специфическое дружелюбное сообщение (без случайного выбора).
     * 
     * @param bot — Telegram бот
     * @param chatId — ID чата
     * @param userId — ID пользователя
     * @param customMessage — пользовательское дружелюбное сообщение
     * @param technicalError — техническая ошибка для логов
     * @param context — контекст ошибки
     */
    public static void sendCustomFriendlyMessage(TelegramLongPollingBot bot, Long chatId, Long userId,
                                                String customMessage, String technicalError, String context) {
        // Логируем техническую ошибку
        log.error("{}: Ошибка для userId={}, chatId={}: {}", context, userId, chatId, technicalError);
        
        // Отправляем кастомное дружелюбное сообщение
        try {
            bot.execute(new SendMessage(chatId.toString(), customMessage));
        } catch (TelegramApiException e) {
            log.error("{}: Не удалось отправить кастомное сообщение об ошибке для userId={}: {}", 
                     context, userId, e.getMessage());
        }
    }
}