package org.example.model.personage;

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
 * Юмор: если скопируешь поле humor в Personage3 — Иларион лично напишет тебе в Telegram.
 */
public class Personage2 extends PersonageBase {
    /** Юмор */
    private int humor;
    /** Коммуникации */
    private int communication;

    /**
     * Конструктор персонажа с предустановленными характеристиками.
     * Здесь создаётся новый герой с уникальными параметрами.
     * Пример:
     *   Personage2 p2 = new Personage2();
     * Юмор: если забудешь вызвать super() — Иларион лично напишет тебе в Telegram.
     */
    public Personage2() {
        System.out.println("[Personage2] Конструктор — создаём нового персонажа Personage2");
        this.name = "";
        this.level = 0;
        this.energy = 8;
        this.achievementPoints = 0;
        this.currency = 0;
        this.humor = 140;
        this.communication = 160;
        this.status = "Новобранец";
        System.out.println("[Personage2] Конструктор — персонаж создан: " + this);
    }

    /**
     * Заполнить поля персонажа из сущности PersonageEntity (универсально)
     * @param entity — сущность персонажа из БД
     */
    public void fillFromEntity(org.example.model.entity.PersonageEntity entity) {
        this.name = entity.getName();
        this.level = entity.getLevel();
        this.energy = entity.getEnergy();
        this.achievementPoints = entity.getAchievementPoints();
        this.currency = entity.getCurrency();
        this.status = entity.getStatus();
        this.humor = entity.getHumor() != null ? entity.getHumor() : 0;
        this.communication = entity.getCommunication() != null ? entity.getCommunication() : 0;
    }

    /**
     * Получить карточку персонажа для Telegram
     * @param chatId — ID чата Telegram
     * @return SendPhoto — карточка персонажа
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
                        "\uD83D\uDDE3\uFE0FНавыки коммуникации: " + communication + " — Объяснит баг так, что ты начнёшь сомневаться в себе...")
                .parseMode("Markdown")
                .build();
    }



    public  SendPhoto getRomanFloy (Long chatId, int communicationDelta) {
        String caption = "*" + name + "*\n\n" +
                "_Статус_: 'Изменено' = Легионер\n" +
                "🏆Level: " + level + " (+1)\n" +
                "⚡️Энергия: " + energy + "\n" +
                "⭐️Очки достижения: " + achievementPoints + " (+50)\n" +
                "💲Деньги: " + currency + " (+450)\n" +
                "\uD83D\uDE01Юмор:  " + humor + " — Его мемы так же опасны, как баги в пятницу. \n" +
                "\uD83D\uDDE3\uFE0F Навыки коммуникации: " + communication  + " (+" + communicationDelta + ") — Объяснит баг так, что ты начнёшь сомневаться в себе... \n\n" +
                "📜 *Комментарий от Итераториуса:*\n" +
                "_Не каждый новичок носит доспехи. Но каждый герой — начинал c них._\n" +
                "держи +" + communicationDelta + " к \uD83D\uDDE3\uFE0F коммуникации, за храбрость! ";

        return  SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new org.telegram.telegrambots.meta.api.objects.InputFile("https://ltdfoto.ru/image/sYkmCW"))
                .caption(caption)
                .parseMode("Markdown")
                .build();
    }

    /**
     * Изменить юмор
     * @param delta — на сколько изменить
     * Пример:
     *   p2.humor(10);
     * Юмор: если юмор < 0 — пора на стендап.
     */
    public void humor(int delta) {
        System.out.println("[Personage2] humor() — старт, текущее значение: " + humor);
        this.humor += delta;
        System.out.println("[Personage2] humor() — завершено, новое значение: " + humor);
    }

    /**
     * Изменить коммуникации
     * @param delta — на сколько изменить
     * Пример:
     *   p2.communication(5);
     * Юмор: если коммуникации > 9000 — ты уже тимлид.
     */
    public void communication(int delta) {
        System.out.println("[Personage2] communication() — старт, текущее значение: " + communication);
        this.communication += delta;
        System.out.println("[Personage2] communication() — завершено, новое значение: " + communication);
    }

    /**
     * Получить значение юмора
     * @return int — текущее значение юмора
     * Пример:
     *   int h = p2.getHumor();
     */
    public int getHumor() {
        System.out.println("[Personage2] getHumor() — возвращаем значение: " + humor);
        return humor;
    }
    /**
     * Получить значение коммуникаций
     * @return int — текущее значение коммуникаций
     * Пример:
     *   int c = p2.getCommunication();
     */
    public int getCommunication() {
        System.out.println("[Personage2] getCommunication() — возвращаем значение: " + communication);
        return communication;
    }

    @Override
    public void levelUp() {
        int exp = 38 + (int)(Math.random() * (55 - 38 + 1));
        int cash = 200 + (int)(Math.random() * (350 - 200 + 1));
        this.achievementPoints += exp;
        this.currency += cash;
    }

    // --- Советы по расширению ---
    // 1. Все уникальные свойства (например, юмор, коммуникации) — только здесь.
    // 2. Не копипасть! Если логика повторяется — выноси в базу.
    // 3. Если добавишь метод без комментария — Иларион лично напишет тебе в Telegram.
}
