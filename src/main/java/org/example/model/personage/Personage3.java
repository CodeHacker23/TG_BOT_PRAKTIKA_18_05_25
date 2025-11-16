package org.example.model.personage;

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
 * Юмор: если скопируешь codeAccuracy в Personage1 — Иларион лично напишет тебе в Telegram.
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
     * Юмор: если забудешь вызвать super() — Иларион лично напишет тебе в Telegram.
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
        this.codeAccuracy = entity.getCodeAccuracy() != null ? entity.getCodeAccuracy() : 0;
        this.optimization = entity.getOptimization() != null ? entity.getOptimization() : 0;
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



    public  SendPhoto getRomanPersonage3  (Long chatId, int optimizationDelta) {
        String caption = "*" + name + "*\n\n" +
                "_Статус_: 'Изменено' = Легионер\n" +
                "🏆Level: " + level + " (+1)\n" +
                "⚡️Энергия: " + energy + "\n" +
                "⭐️Очки достижения: " + achievementPoints + " (+50)\n" +
                "💲Деньги: " + currency + " (+450)\n" +
                "\uD83D\uDD0DТочность кода: " + codeAccuracy  + " — Найдёт баг даже в километре кода. Комментариев нет? Для него это не баг, а квест!\n" +
                "⚙️Процесс оптимизации: " +  optimization + " (+" + optimizationDelta + ") — Перепишет твой код так, что компилятор удивится..\n\n" +
                "📜 *Комментарий от Итераториуса:*\n" +
                "_Не каждый новичок носит доспехи. Но каждый герой — начинал c них._\n" +
                "держи +" + optimizationDelta + " к ⚙️ оптимизации, за храбрость! ";

        return  SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new org.telegram.telegrambots.meta.api.objects.InputFile("https://ltdfoto.ru/image/sYkNt3"))
                .caption(caption)
                .parseMode("Markdown")
                .build();
    }
    @Override
    public void levelUp() {
        int exp = 38 + (int)(Math.random() * (55 - 38 + 1));
        int cash = 200 + (int)(Math.random() * (350 - 200 + 1));
        this.achievementPoints += exp;
        this.currency += cash;
    }
    // --- Советы по расширению ---
    // 1. Все уникальные свойства (например, codeAccuracy, optimization) — только здесь.
    // 2. Не копипасть! Если логика повторяется — выноси в базу.
    // 3. Если добавишь метод без комментария — Иларион лично напишет тебе в Telegram.
}
