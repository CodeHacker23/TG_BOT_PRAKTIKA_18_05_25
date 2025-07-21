package org.example.model.personage;

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
     * Здесь создаётся новый герой с уникальными параметрами.
     * Пример:
     *   Personage1 p1 = new Personage1();
     * Юмор: если забудешь вызвать super() — Архитектор лично напишет тебе в Telegram.
     */
    public Personage1() {
        System.out.println("[Personage1] Конструктор — создаём нового персонажа Personage1");
        this.name = "";
        this.level = 0;
        this.energy = 8;
        this.achievementPoints = 0;
        this.currency = 0;
        this.deadlineResistance = 90;
        this.analytics = 110;
        this.status = "Новобранец";
        System.out.println("[Personage1] Конструктор — персонаж создан: " + this);
    }

    /**
     * Изменить сопротивление дедлайну
     * @param delta — на сколько изменить
     * Пример:
     *   p1.changeDeadlineResistance(5);
     * Юмор: если сопротивление стало отрицательным — пора брать отпуск.
     */
    public void changeDeadlineResistance(int delta) {
        System.out.println("[Personage1] changeDeadlineResistance() — старт, текущее значение: " + deadlineResistance);
        this.deadlineResistance += delta;
        System.out.println("[Personage1] changeDeadlineResistance() — завершено, новое значение: " + deadlineResistance);
    }

    /**
     * Изменить аналитику
     * @param delta — на сколько изменить
     * Пример:
     *   p1.changeAnalytics(10);
     * Юмор: если аналитика > 9000 — ты уже Архитектор.
     */
    public void changeAnalytics(int delta) {
        System.out.println("[Personage1] changeAnalytics() — старт, текущее значение: " + analytics);
        this.analytics += delta;
        System.out.println("[Personage1] changeAnalytics() — завершено, новое значение: " + analytics);
    }

    /**
     * Получить сопротивление дедлайну
     * @return int — текущее значение сопротивления дедлайну
     * Пример:
     *   int dr = p1.getDeadlineResistance();
     */
    public int getDeadlineResistance() {
        System.out.println("[Personage1] getDeadlineResistance() — возвращаем значение: " + deadlineResistance);
        return deadlineResistance;
    }
    /**
     * Получить аналитику
     * @return int — текущее значение аналитики
     * Пример:
     *   int a = p1.getAnalytics();
     */
    public int getAnalytics() {
        System.out.println("[Personage1] getAnalytics() — возвращаем значение: " + analytics);
        return analytics;
    }

    /**
     * Получить объект SendPhoto с фото и описанием персонажа для Telegram
     * @param chatId — ID чата Telegram
     * @return SendPhoto — карточка персонажа
     * Пример:
     *   SendPhoto card = p1.getSendPhotoTheory(chatId);
     *   bot.execute(card);
     */
    public SendPhoto getSendPhotoTheory(Long chatId) {
        System.out.println("[Personage1] getSendPhotoTheory() — отправляем карточку персонажа, имя: " + name);
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
    public SendPhoto ReamPersonage1(Long chatId) {
        System.out.println("[Personage1] ReamPersonage1 — отправляем карточку персонажа в доспехаха Рима, имя: " + name);
        return SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile("https://ltdfoto.ru/image/sCNCjY"))
                .caption( "*" + name + "*" + "\n\n" +
                        "_Статус_: " + status  + "\n" +
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
 