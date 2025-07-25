package org.example.bot.KeyboardService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

/**
 * KeyboardService — фабрика всех клавиатур для бота в особенности сюжета про РИМ
 * Здесь создаются и настраиваются все ReplyKeyboardMarkup и InlineKeyboardMarkup.
 *
 * Почему нельзя лепить клавиатуры прямо в сервисах? Потому что иначе твой код быстро превратится в "клавиатурный ад".
 *
 * Пример использования:
 *   message.setReplyMarkup(KeyboardService.getStartKeyboardStatic());
 *   message.setReplyMarkup(KeyboardService.getCreatePersonageInlineKeyboard());
 *
 * Если хочешь добавить новую клавиатуру — делай отдельный метод здесь!
 *
 * Юмор: если добавишь клавиатуру в другом классе — Архитектор пришлёт тебе мем про SpaghettiCode.
 */
@Service
public class KeyboardReam {
    private static final Logger log = LoggerFactory.getLogger(KeyboardService.class);

    /**
     * Клавиатура для старта (Да/Нет/Я изучаю пайтон)
     * @return ReplyKeyboardMarkup — обычная клавиатура
     *
     * Пример:
     *   message.setReplyMarkup(KeyboardService.getStartKeyboardStatic());
     */
    public static ReplyKeyboardMarkup getStartKeyboardStatic() {
        log.info("[KeyboardService] getStartKeyboardStatic() — создаём стартовую клавиатуру. для chatId  ");
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Да"));
        row.add(new KeyboardButton("Нет"));
        row.add(new KeyboardButton("Я изучаю пайтон"));
        keyboard.setKeyboard(List.of(row));
        return keyboard;
    }

    /**
     * Cоздана кнопка для преображения персонажа пользвоателя в Римские доспехи
     * @param chatId
     * @return replyKeyboardMarkup
     */
    public static ReplyKeyboardMarkup  gladiatorPlot(Long chatId){
        log.info("gladiatorPlot() - cоздаем клавиатуру с кнопкой 'Принять доспехи'  'Я лучше пойду обновлю IDE' для chatId = {}", chatId);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow ready = new KeyboardRow();
        ready.add(new KeyboardButton("Принять доспехи ⚔\uFE0F"));
        ready.add(new KeyboardButton("Я лучше пойду обновлю IDE"));

        replyKeyboardMarkup.setKeyboard(List.of(ready));
        return replyKeyboardMarkup;
    }

    /**
     * Cоздана кнопка для получения теории по ArrayList
     * @param chatId
     * @return replyKeyboardMarkup
     */
    public static ReplyKeyboardMarkup BattleList(Long chatId){
        log.info("BattleList() - cоздаем клавиатуру с кнопкой 'Получить боевой свиток' для chatId = {}", chatId);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("\uD83D\uDCDC Получить боевой свиток")); //создание нашей кнопки
        replyKeyboardMarkup.setKeyboard(List.of(row));
        return replyKeyboardMarkup;
    }

    /**
     * Кнопки на ответ атаки Арейна, 3 варианта (try-catch,Уклониться,вставкой в начало)
     * @param chatId
     * @return
     */
    public static ReplyKeyboardMarkup BattlArreyn(Long chatId){
        log.info("BattlArreyn() - cоздаем клавиатуру с кнопками 'Блокировать (try-catch)',' Уклониться и проанализировать', '\uD83D\uDEA8 Отразить вставкой в начало' для chatId = {}", chatId);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("\uD83D\uDEE1 Блокировать \n" +
                " (try-catch)"));//создание нашей кнопки
        row.add(new KeyboardButton("\uD83D\uDD0D Уклониться \n" +
                " и \n" +
                "проанализировать"));//создание нашей кнопки
        row.add(new KeyboardButton("\uD83D\uDEA8 Отразить \n" +
                "вставкой \n" +
                " в начало"));//создание нашей кнопки


        replyKeyboardMarkup.setKeyboard(List.of(row));
        return replyKeyboardMarkup;
    }

}
