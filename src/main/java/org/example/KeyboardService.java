package org.example;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

/**
 * KeyboardService — фабрика всех клавиатур для бота.
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
public class KeyboardService  {
    /**
     * Клавиатура для старта (Да/Нет/Я изучаю пайтон)
     * @return ReplyKeyboardMarkup — обычная клавиатура
     *
     * Пример:
     *   message.setReplyMarkup(KeyboardService.getStartKeyboardStatic());
     */
    public static ReplyKeyboardMarkup getStartKeyboardStatic() {
        System.out.println("[KeyboardService] getStartKeyboardStatic() — создаём стартовую клавиатуру.");
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
     * Инлайн-клавиатура для создания персонажа
     * @return InlineKeyboardMarkup — инлайн-клавиатура с одной кнопкой
     *
     * Пример:
     *   message.setReplyMarkup(KeyboardService.getCreatePersonageInlineKeyboard());
     */
    public static InlineKeyboardMarkup getCreatePersonageInlineKeyboard() {
        System.out.println("[KeyboardService] getCreatePersonageInlineKeyboard() — создаём инлайн-клавиатуру для создания персонажа.");
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Создать персонажа");
        button.setCallbackData("create_personage");

        List<InlineKeyboardButton> row = List.of(button);
        List<List<InlineKeyboardButton>> keyboard = List.of(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }

    // Совет: если хочешь добавить новую клавиатуру (например, для LinkedListStoryService), делай отдельный метод здесь:
    // public static ReplyKeyboardMarkup getLinkedListKeyboard() { ... }
    // и вызывай его из нужного сервиса.

    // Если добавишь клавиатуру без комментария — Архитектор лично напишет тебе в Telegram.
} 