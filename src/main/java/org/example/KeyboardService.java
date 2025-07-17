package org.example;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(KeyboardService.class);
    /**
     * Клавиатура для старта (Да/Нет/Я изучаю пайтон)
     * @return ReplyKeyboardMarkup — обычная клавиатура
     *
     * Пример:
     *   message.setReplyMarkup(KeyboardService.getStartKeyboardStatic());
     */
    public static ReplyKeyboardMarkup getStartKeyboardStatic() {
        log.info("[KeyboardService] getStartKeyboardStatic() — создаём стартовую клавиатуру.");
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
        log.info("[KeyboardService] getCreatePersonageInlineKeyboard() — создаём инлайн-клавиатуру для создания персонажа.");
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Создать персонажа");
        button.setCallbackData("create_personage");

        List<InlineKeyboardButton> row = List.of(button);
        List<List<InlineKeyboardButton>> keyboard = List.of(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }

    //кнопка для метода Байта с вертолетом
    public static ReplyKeyboardMarkup KeyboardHelicopter() {
        log.info("[KeyboardService] KeyboardHelicopter()  — создаём клавиатуру после отправки фото с вертолетом");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow variant = new KeyboardRow();
        variant.add(new KeyboardButton("✅ Продолжить путь программиста"));
        variant.add(new KeyboardButton("❓ Но что будет со мной?"));
        replyKeyboardMarkup.setKeyboard(List.of(variant));
        return replyKeyboardMarkup;
    }

    /**
     * Метод для удаления клавиатуры (пустая клавиатура)
     * @return ReplyKeyboardRemove — объект для удаления клавиатуры
     */


    public static  ReplyKeyboardRemove removeKeyboard() {
        return new ReplyKeyboardRemove(true);
    }

    public static ReplyKeyboardMarkup KeyboardPlot() {
        log.info("KeyboardPlot() — создаём клавиатуру с кнопкой 'Какая?'");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow which = new KeyboardRow();
        which.add(new KeyboardButton("Какая?"));
        replyKeyboardMarkup.setKeyboard(List.of(which));
        return replyKeyboardMarkup;
    }

//кнопка Я готов - для финального сообщения от Байта форджа
    public static ReplyKeyboardMarkup KeyboardReady(){
        log.info("KeyboardReady() - cоздаем клавиатуру с кнопкой 'Я готов' ");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow ready = new KeyboardRow();
        ready.add(new KeyboardButton("Я готов✅"));
        replyKeyboardMarkup.setKeyboard(List.of(ready));
        return replyKeyboardMarkup;
    }

    public static InlineKeyboardMarkup finalGatesOfRome(){ //завершение первого сюжета 1
        log.info("[KeyboardService] finalButtonByte — создаём инлайн-клавиатуру для создания перехода в сюжетную линию ArrayList.");
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("\uD83C\uDFDB️ Войти во врата Рима");
        button.setCallbackData("enter_arraylist");

        List<InlineKeyboardButton> row = List.of(button);
        List<List<InlineKeyboardButton>> keyboard = List.of(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);


        return markup;
    }

    //заверешение 1 сюжетной ветки
    public static ReplyKeyboardMarkup  gladiatorPlot(Long chatId){
        log.info("gladiatorPlot() - cоздаем клавиатуру с кнопкой 'Принять доспехи'  'Нет, я программист, а не гладиатор' ");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow ready = new KeyboardRow();
        ready.add(new KeyboardButton("Принять доспехи ⚔\uFE0F"));
        ready.add(new KeyboardButton("Нет, я программист, а не гладиатор \uD83E\uDDD1\u200D\uD83D\uDCBB"));

        replyKeyboardMarkup.setKeyboard(List.of(ready));
        return replyKeyboardMarkup;
    }

    //2 сюжетная линия
    public static ReplyKeyboardMarkup  userChoice(Long chatId){
        log.info("gladiatorPlot() - cоздаем клавиатуру с кнопкой '✅ Принять судьбу программиста'  '❌ Сбежать от компиляции' ");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow ready = new KeyboardRow();
        ready.add(new KeyboardButton("✅ Принять судьбу программиста"));
        ready.add(new KeyboardButton("❌ Сбежать от компиляции"));

        replyKeyboardMarkup.setKeyboard(List.of(ready));
        return replyKeyboardMarkup;
    }

    //2 сюжетная линия
    public static ReplyKeyboardMarkup comeBack(Long chatId){
        log.info("gladiatorPlot() - cоздаем клавиатуру с кнопкой '\uD83D\uDCCE Вернуться и скомпилироваться' ");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow ready = new KeyboardRow();
        ready.add(new KeyboardButton("\uD83D\uDCCE Вернуться и скомпилироваться"));

        replyKeyboardMarkup.setKeyboard(List.of(ready));
        return replyKeyboardMarkup;
    }


    // Совет: если хочешь добавить новую клавиатуру (например, для LinkedListStoryService), делай отдельный метод здесь:
    // public static ReplyKeyboardMarkup getLinkedListKeyboard() { ... }
    // и вызывай его из нужного сервиса.

    // Если добавишь клавиатуру без комментария — Архитектор лично напишет тебе в Telegram.


} 