package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * Personage3 — персонаж с уникальными характеристиками: точность кода и оптимизация.
 * Наследует PersonageBase, добавляет свои свойства.
 *
 * Как расширять персонажей:
 *   - Все уникальные свойства (например, codeAccuracy, optimization) — только здесь.
 *   - Общие поля (имя, уровень, энергия и т.д.) — только в PersonageBase.
 *
 * Пример создания:
 *   Personage3 p3 = new Personage3();
 *   p3.setName("Лёха");
 *   p3.codeAccuracy(10);
 *
 * Юмор: если скопируешь codeAccuracy в Personage1 — Архитектор лично напишет тебе в Telegram.
 */
public class Personage3 extends PersonageBase {
    /** Точность кода */
    private int codeAccuracy;
    /** Оптимизация */
    private int optimization;

    /**
     * Конструктор персонажа с предустановленными характеристиками.
     * Здесь создаётся новый герой с уникальными параметрами.
     * Пример:
     *   Personage3 p3 = new Personage3();
     * Юмор: если забудешь вызвать super() — Архитектор лично напишет тебе в Telegram.
     */
    public Personage3() {
        System.out.println("[Personage3] Конструктор — создаём нового персонажа Personage3");
        this.name = "";
        this.level = 0;
        this.energy = 8;
        this.achievementPoints = 0;
        this.currency = 0;
        this.codeAccuracy = 140;
        this.optimization = 160;
        this.status = "Новобранец";
        System.out.println("[Personage3] Конструктор — персонаж создан: " + this);
    }

    /**
     * Получить карточку персонажа для Telegram
     * @param chatId — ID чата Telegram
     * @return SendPhoto — карточка персонажа
     * Пример:
     *   SendPhoto card = p3.PhotoTheoryGeks(chatId);
     *   bot.execute(card);
     */
    public SendPhoto PhotoTheoryGeks(Long chatId) {
        System.out.println("[Personage3] PhotoTheoryGeks() — отправляем карточку персонажа, имя: " + name);
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile("https://ltdfoto.ru/images/2025/07/09/GEKS.jpg"))
                .caption("*" + name + "*" + "\n\n" +
                        "_Статус_: " + status + "\n" +
                        "\uD83C\uDFC6Level: " + level + "\n" +
                        "⚡Энергия: " + energy + "\n" +
                        "⭐Очки достижения: " + achievementPoints + "\n" +
                        "\uD83D\uDCB2Деньги: " + currency + "\n" +
                        "\uD83D\uDD0DТочность кода: " + codeAccuracy  + " — Найдёт баг даже в километре кода. Комментариев нет? Для него это не баг, а квест!\n" +
                        "⚙️Процесс оптимизации: " +  optimization + " — Перепишет твой код так, что компилятор удивится..")
                .parseMode("Markdown")
                .build();
    }

    /**
     * Изменить точность кода
     * @param delta — на сколько изменить
     * Пример:
     *   p3.codeAccuracy(10);
     * Юмор: если точность кода > 9000 — пора писать компилятор.
     */
    public void codeAccuracy(int delta){
        System.out.println("[Personage3] codeAccuracy() — старт, текущее значение: " + codeAccuracy);
        this.codeAccuracy += delta;
        System.out.println("[Personage3] codeAccuracy() — завершено, новое значение: " + codeAccuracy);
    }

    /**
     * Изменить оптимизацию
     * @param delta — на сколько изменить
     * Пример:
     *   p3.optimization(5);
     * Юмор: если оптимизация < 0 — пора на рефакторинг.
     */
    public void optimization(int delta){
        System.out.println("[Personage3] optimization() — старт, текущее значение: " + optimization);
        this.optimization  += delta;
        System.out.println("[Personage3] optimization() — завершено, новое значение: " + optimization);
    }

    /**
     * Получить точность кода
     * @return int — текущее значение точности кода
     * Пример:
     *   int ca = p3.getCodeAccuracy();
     */
    public int getCodeAccuracy() {
        System.out.println("[Personage3] getCodeAccuracy() — возвращаем значение: " + codeAccuracy);
        return codeAccuracy;
    }
    /**
     * Получить оптимизацию
     * @return int — текущее значение оптимизации
     * Пример:
     *   int o = p3.getOptimization();
     */
    public int getOptimization() {
        System.out.println("[Personage3] getOptimization() — возвращаем значение: " + optimization);
        return optimization;
    }

    // --- Советы по расширению ---
    // 1. Все уникальные свойства (например, codeAccuracy, optimization) — только здесь.
    // 2. Не копипасть! Если логика повторяется — выноси в базу.
    // 3. Если добавишь метод без комментария — Архитектор лично напишет тебе в Telegram.
}
