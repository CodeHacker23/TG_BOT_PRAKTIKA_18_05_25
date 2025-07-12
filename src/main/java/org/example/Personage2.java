package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * Personage2 — персонаж с уникальными характеристиками: юмор и коммуникации.
 * Наследует PersonageBase, добавляет свои свойства.
 *
 * Как расширять персонажей:
 *   - Все уникальные свойства (например, юмор, коммуникации) — только здесь.
 *   - Общие поля (имя, уровень, энергия и т.д.) — только в PersonageBase.
 *
 * Пример создания:
 *   Personage2 p2 = new Personage2();
 *   p2.setName("Петя");
 *   p2.humor(10);
 *
 * Юмор: если скопируешь поле humor в Personage3 — Архитектор лично напишет тебе в Telegram.
 */
public class Personage2 extends PersonageBase {
    /** Юмор */
    private int humor;
    /** Коммуникации */
    private int communication;

    /**
     * Конструктор персонажа с предустановленными характеристиками.
     */
    public Personage2() {
        this.name = "";
        this.level = 0;
        this.energy = 8;
        this.achievementPoints = 0;
        this.currency = 0;
        this.humor = 140;
        this.communication = 160;
        this.status = "Новобранец";
    }

    /**
     * Получить карточку персонажа для Telegram
     * @param chatId — ID чата Telegram
     * @return SendPhoto — карточка персонажа
     *
     * Пример:
     *   SendPhoto card = p2.PhotoTheoryFloy(chatId);
     *   bot.execute(card);
     */
    public SendPhoto PhotoTheoryFloy(Long chatId) {
        System.out.println("[Personage2] PhotoTheoryFloy() — отправляем карточку персонажа, имя: " + name);
        return SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile("https://ltdfoto.ru/image/soo913"))
                .caption("*" + name + "*" + "\n\n" +
                        "_Статус_: " + status + "\n" +
                        "\uD83C\uDFC6Level: " + level + "\n" +
                        "⚡Энергия: " + energy + "\n" +
                        "⭐Очки достижения: " + achievementPoints + "\n" +
                        "\uD83D\uDCB2Деньги: " + currency + "\n" +
                        "\uD83D\uDE01Юмор: " + humor + " — Его мемы так же опасны, как баги в пятницу.  \n" +
                        "\uD83D\uDDE3\uFE0FНавыки коммуникации: " + communication + " — Объяснит баг так, что ты начнешь сомневаться в себе...")
                .parseMode("Markdown")
                .build();
    }

    /**
     * Изменить юмор
     * @param delta — на сколько изменить
     */
    public void humor(int delta) {
        this.humor += delta;
        System.out.println("[Personage2] humor() — новое значение: " + humor);
    }

    /**
     * Изменить коммуникации
     * @param delta — на сколько изменить
     */
    public void communication(int delta) {
        this.communication += delta;
        System.out.println("[Personage2] communication() — новое значение: " + communication);
    }

    // --- Советы по расширению ---
    // 1. Все уникальные свойства (например, юмор, коммуникации) — только здесь.
    // 2. Не копипасть! Если логика повторяется — выноси в базу.
    // 3. Если добавишь метод без комментария — Архитектор лично напишет тебе в Telegram.
}
