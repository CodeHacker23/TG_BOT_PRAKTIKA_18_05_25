package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.bot.KeyboardService.KeyboardReam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Round2SequenceService — сервис для планирования последовательностей раунда 2.
 * 
 * Этот класс убирает сложную логику планирования из MessageServiceRound2,
 * делая код более читаемым и понятным.
 * 
 * Связи с другими классами:
 * - Используется в MessageServiceRound2 для запуска последовательностей
 * - Работает с PhotoReam для отправки фото
 * - Использует собственный планировщик для надежности
 * 
 * Автор: Иларион (который любит простоту)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class Round2SequenceService {
    
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final CasinoScenarioService casinoScenarioService;
    
    /**
     * 🚀 ЗАПУСКАЕТ ПОЛНУЮ ЦЕПОЧКУ РАУНДА 2
     * 
     * ПОСЛЕДОВАТЕЛЬНОСТЬ:
     * 1. Фото с объявлением раунда 2 и статами (сразу)
     * 2. Через 5 сек → sendMessageText2Rond
     * 3. Через 8 сек → messageArreyon2Rond  
     * 4. Через 12 сек → messageIteratorius2Rond
     * 
     * @param bot — Telegram бот для отправки
     * @param chatId — ID чата пользователя
     * @param round2Photo — фото с объявлением раунда 2
     * @param textMessage — текстовое сообщение
     * @param arreyonMessage — сообщение от Аррейна
     * @param iteratoriusMessage — сообщение от Итераториуса
     */
    public void startRound2Sequence(TelegramLongPollingBot bot, Long chatId, 
                                  SendPhoto round2Photo, SendMessage textMessage, 
                                  SendMessage arreyonMessage, SendMessage iteratoriusMessage) {
        log.info("Round2SequenceService: 🚀 Запуск цепочки раунда 2 для chatId={}", chatId);
        
        try {
            // 1. СРАЗУ: Отправляем фото с объявлением раунда 2
            bot.execute(round2Photo);
            log.info("Round2SequenceService: ✅ Фото раунда 2 отправлено для chatId={}", chatId);
            
            // 2. ЧЕРЕЗ 5 СЕК: sendMessageText2Rond
            scheduler.schedule(() -> {
                try {
                    bot.execute(textMessage);
                    log.info("Round2SequenceService: ✅ sendMessageText2Rond отправлено для chatId={}", chatId);
                } catch (Exception e) {
                    log.error("Round2SequenceService: ❌ Ошибка sendMessageText2Rond для chatId={}: {}", chatId, e.getMessage(), e);
                }
            }, 5, TimeUnit.SECONDS);
            
            // 3. ЧЕРЕЗ 8 СЕК: messageArreyon2Rond
            scheduler.schedule(() -> {
                try {
                    bot.execute(arreyonMessage);
                    log.info("Round2SequenceService: ✅ messageArreyon2Rond отправлено для chatId={}", chatId);
                } catch (Exception e) {
                    log.error("Round2SequenceService: ❌ Ошибка messageArreyon2Rond для chatId={}: {}", chatId, e.getMessage(), e);
                }
            }, 8, TimeUnit.SECONDS);
            
            // 4. ЧЕРЕЗ 12 СЕК: messageIteratorius2Rond
            scheduler.schedule(() -> {
                try {
                    bot.execute(iteratoriusMessage);
                    log.info("Round2SequenceService: ✅ messageIteratorius2Rond отправлено для chatId={}", chatId);
                } catch (Exception e) {
                    log.error("Round2SequenceService: ❌ Ошибка messageIteratorius2Rond для chatId={}: {}", chatId, e.getMessage(), e);
                }
            }, 12, TimeUnit.SECONDS);
            
            log.info("Round2SequenceService: 🎯 Все задачи раунда 2 запланированы для chatId={}", chatId);
            
        } catch (TelegramApiException e) {
            log.error("Round2SequenceService: ❌ Критическая ошибка запуска раунда 2 для chatId={}: {}", chatId, e.getMessage(), e);
        }
    }
    
    /**
     * 🧠 ЗАПУСКАЕТ ЦЕПОЧКУ ENSURE CAPACITY
     * 
     * ПРАВИЛЬНАЯ ПОСЛЕДОВАТЕЛЬНОСТЬ:
     * 1. Сразу → основное сообщение ensureCapacity (ответ на кнопку)
     * 2. Через 5 сек → ответ Аррейна
     * 3. Через 8 сек → ответ Итераториуса про понимание методов + навыки
     * 4. Через 11 сек → сообщение Итераториуса про удачу
     * 
     * @param bot — Telegram бот для отправки
     * @param chatId — ID чата пользователя
     * @param mainMessage — основное сообщение ensureCapacity (ответ на кнопку)
     * @param arreyonResponse — ответ Аррейна
     * @param iteratoriusResponse — ответ Итераториуса про понимание методов + навыки
     */
    public void startEnsureCapacitySequence(TelegramLongPollingBot bot, Long chatId,
                                          SendMessage mainMessage, SendMessage arreyonResponse, 
                                          SendMessage iteratoriusResponse) {
        log.info("Round2SequenceService: 🧠 Запуск цепочки ensureCapacity для chatId={}", chatId);
        
        try {
            // 1. СРАЗУ: Отправляем основное сообщение (ответ на кнопку)
            bot.execute(mainMessage);
            log.info("Round2SequenceService: ✅ Основное сообщение ensureCapacity (ответ на кнопку) отправлено для chatId={}", chatId);
            
            // 2. ЧЕРЕЗ 5 СЕК: Ответ Аррейна
            scheduler.schedule(() -> {
                try {
                    bot.execute(arreyonResponse);
                    log.info("Round2SequenceService: ✅ Ответ Аррейна отправлен для chatId={}", chatId);
                } catch (Exception e) {
                    log.error("Round2SequenceService: ❌ Ошибка отправки ответа Аррейна для chatId={}: {}", chatId, e.getMessage());
                }
            }, 5, TimeUnit.SECONDS);
            
            // 3. ЧЕРЕЗ 8 СЕК: Ответ Итераториуса про понимание методов + навыки
            scheduler.schedule(() -> {
                try {
                    bot.execute(iteratoriusResponse);
                    log.info("Round2SequenceService: ✅ Ответ Итераториуса про понимание методов отправлен для chatId={}", chatId);
                } catch (Exception e) {
                    log.error("Round2SequenceService: ❌ Ошибка отправки ответа Итераториуса для chatId={}: {}", chatId, e.getMessage());
                }
            }, 8, TimeUnit.SECONDS);
            
            // 4. ЧЕРЕЗ 11 СЕК: Сообщение Итераториуса про удачу
            scheduler.schedule(() -> {
                try {
                    SendMessage luckMessage = new SendMessage();
                    luckMessage.setChatId(chatId);
                    luckMessage.setParseMode("Markdown");
                    luckMessage.setText("*Итераториус*\n" +
                            "Малец, ты прошёл бои и викторины...\n" +
                            "Но ArrayList — это не только логика, это ещё и УДАЧА!");
                    
                    bot.execute(luckMessage);
                    log.info("Round2SequenceService: ✅ Сообщение Итераториуса про удачу отправлено для chatId={}", chatId);
                    
                    // 🎰 ЗАПУСКАЕМ КАЗИНО-СЦЕНАРИЙ ПОСЛЕ СООБЩЕНИЯ ПРО УДАЧУ
                    // Через 4 секунды после сообщения про удачу запускаем казино
                    scheduler.schedule(() -> {
                        try {
                            log.info("Round2SequenceService: 🎰 Запуск казино-сценария после сообщения про удачу для chatId={}", chatId);
                            casinoScenarioService.startCasinoScenario(bot, chatId, null);

                        } catch (Exception e) {
                            log.error("Round2SequenceService: ❌ Ошибка запуска казино для chatId={}: {}", chatId, e.getMessage());
                        }
                    }, 4, TimeUnit.SECONDS);
                    
                } catch (Exception e) {
                    log.error("Round2SequenceService: ❌ Ошибка отправки сообщения про удачу для chatId={}: {}", chatId, e.getMessage());
                }
            }, 11, TimeUnit.SECONDS);
            
            log.info("Round2SequenceService: 🎯 Цепочка ensureCapacity запланирована для chatId={}", chatId);
            
        } catch (TelegramApiException e) {
            log.error("Round2SequenceService: ❌ Критическая ошибка ensureCapacity для chatId={}: {}", chatId, e.getMessage());
        }
    }
}
