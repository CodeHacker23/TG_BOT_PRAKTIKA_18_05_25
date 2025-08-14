package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.PhotoService.PhotoReam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

 /**
 * CasinoScenarioService — сервис для управления сценариями казино.
 * 
 * Этот класс убирает дублирование кода сценария казино, который повторялся
 * в MessageServiceRound2 в трех разных местах.
 * 
 * Связи с другими классами:
 * - Использует AudioService для отправки аудио
 * - Использует PhotoReam для отправки фото мешка
 * - Используется в MessageServiceRound2 для запуска сценариев казино
 * 
 * Автор: Иларион (который ненавидит повторяющийся код)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CasinoScenarioService {
    
    private final AudioService audioService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    /**
     * 🎰 ЗАПУСКАЕТ СТАНДАРТНЫЙ СЦЕНАРИЙ КАЗИНО
     * 
     * ПОСЛЕДОВАТЕЛЬНОСТЬ:
     * 1. Через 4 сек → фото мешка с билетами  
     * 2. Через 7 сек → аудио "Ставки на код"
     * 
     * @param bot — Telegram бот для отправки
     * @param chatId — ID чата пользователя
     * @param iteratoriusMessage — сообщение от Итераториуса о казино (больше не используется)
     */
    public void startCasinoScenario(TelegramLongPollingBot bot, Long chatId, SendMessage iteratoriusMessage) {
        log.info("CasinoScenarioService: 🎰 Запуск стандартного сценария казино для chatId={}", chatId);
        
        // 1. Через 4 секунды отправляем фото мешка с билетами
        scheduler.schedule(() -> {
            try {
                log.info("CasinoScenarioService: 🎰 Отправка фото мешка для chatId={}", chatId);
                // Отправляем фото мешка с правильным описанием
                SendPhoto bagPhoto = PhotoReam.casinoBag(chatId);
                bagPhoto.setCaption("🎯 В этом волшебном мешке лежат 10 билетов.\nКаждый содержит своё исключение, свой сюрприз...\nИ только ОДИН из них — ДЖЕКПОТ!");
                bot.execute(bagPhoto);
        
                // 2. Через 3 секунды отправляем финальное аудио
                scheduler.schedule(() -> {
                    try {
                        log.info("CasinoScenarioService: 🎰 Отправка финального аудио для chatId={}", chatId);
                        audioService.sendAudio(bot, chatId, "src/main/resources/audio/Ставки на код.mp3");
                    } catch (Exception e) {
                        log.error("CasinoScenarioService: ❌ Ошибка отправки финального аудио для chatId={}: {}", chatId, e.getMessage());
                    }
                }, 3, TimeUnit.SECONDS);
                
            } catch (TelegramApiException e) {
                log.error("CasinoScenarioService: ❌ Ошибка фото мешка для chatId={}: {}", chatId, e.getMessage());
            }
        }, 4, TimeUnit.SECONDS);
    }
    
    /**
     * 🎰 СОЗДАЕТ СООБЩЕНИЕ ИТЕРАТОРИУСА О КАЗИНО
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение от Итераториуса о казино
     */
    public SendMessage createIteratoriusCasinoMessage(Long chatId) {
        log.debug("CasinoScenarioService: Создание сообщения Итераториуса о казино для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус*\n" +
                "Малец, ты прошёл бои и викторины...\n" +
                "Но ArrayList — это не только логика, это ещё и УДАЧА!");
        
        return sendMessage;
    }
}
