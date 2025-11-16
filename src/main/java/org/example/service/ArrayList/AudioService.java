package org.example.service.ArrayList;

import lombok.extern.slf4j.Slf4j;
import org.example.bot.KeyboardService.KeyboardReam;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.File;

/**
 * Сервис для отправки голосовых сообщений в Telegram боте.
 * 
 * Этот класс отвечает за:
 * - Отправку голосовых сообщений пользователям
 * - Обработку ошибок при отправке
 * - Логирование операций с голосовыми сообщениями
 */
@Slf4j
@Service
public class AudioService {

    /**
     * Отправляет голосовое сообщение пользователю в указанный чат.
     *
     * @param bot       - экземпляр бота для отправки
     * @param chatId    - ID чата куда отправлять голосовое сообщение
     * @param audioPath - путь к аудио файлу (будет конвертирован в голосовое)
     * @return true если голосовое сообщение отправлено успешно, false если произошла ошибка
     */
    public boolean sendAudio(TelegramLongPollingBot bot, Long chatId, String audioPath) {
        try {
            log.info("AudioService: Отправка голосового сообщения в чат {}, файл: {}", chatId, audioPath);
            
            // Проверяем существование файла
            File audioFile = new File(audioPath);
            if (!audioFile.exists()) {
                log.error("AudioService: Файл не найден: {}", audioPath);
                return false;
            }
            
            // Создаем InputFile для Telegram API
            InputFile inputFile = new InputFile(audioFile);
            
            // Создаем команду отправки голосового сообщения
            SendVoice sendVoice = new SendVoice();
            sendVoice.setChatId(chatId.toString());
            sendVoice.setVoice(inputFile);

            
            // Отправляем голосовое сообщение с клавиатурой
            sendVoice.setReplyMarkup(KeyboardReam.casinoRound2(chatId));
            bot.execute(sendVoice);
            
            log.info("AudioService: Голосовое сообщение успешно отправлено в чат {}", chatId);
            return true;
            
        } catch (TelegramApiException e) {
            log.error("AudioService: Ошибка при отправке голосового сообщения в чат {}: {}", chatId, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("AudioService: Неожиданная ошибка при отправке голосового сообщения в чат {}: {}", chatId, e.getMessage());
            return false;
        }
    }
}
