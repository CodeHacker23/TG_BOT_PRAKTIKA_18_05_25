package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * Персонаж "Кодыч" с уникальными характеристиками.
 * Наследует основные поля из PersonageBase и добавляет свои.
 */
public class Personage1 extends PersonageBase {
    /** Сопротивление дедлайну */
    private int deadlineResistance;
    /** Аналитика */
    private int analytics;

    /**
     * Конструктор персонажа с предустановленными характеристиками.
     */
    public Personage1() {
        this.name = "";
        this.level = 0;
        this.energy = 8;
        this.achievementPoints = 0;
        this.currency = 0;
        this.deadlineResistance = 90;
        this.analytics = 110;
        this.status = "Новобранец";
    }

    /**
     * Изменить сопротивление дедлайну
     */
    public void changeDeadlineResistance(int delta) {
        this.deadlineResistance += delta;
    }

    /**
     * Изменить аналитику
     */
    public void changeAnalytics(int delta) {
        this.analytics += delta;
    }

    /**
     * Получить объект SendPhoto с фото и описанием персонажа для Telegram
     */
    public SendPhoto getSendPhotoTheory(Long chatId) {
        return SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile("https://ltdfoto.ru/images/2025/07/02/KODYC.jpg"))
                .caption( "*" + name + "*" + "\n\n" +
                        "_Статус_: " + status + "\n" +
                        "\uD83C\uDFC6Level: " + level + "\n" +
                        "⚡Энергия: " + energy + "\n" +
                        "⭐Очки достижения: " + achievementPoints + "\n" +
                        "\uD83D\uDCB2Деньги: " + currency + "\n" +
                        "⌚Сопротивление дедлайну: " + deadlineResistance + " — Привык работать под давлением сроков, но не всегда этому рад. \n" +
                        "\uD83D\uDCCAАналитика: " + analytics + " — Умение находить скрытые связи в коде")
                .parseMode("Markdown")
                .build();
    }
}
 