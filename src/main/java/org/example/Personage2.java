package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class Personage2 extends PersonageBase {

    //юмор
    private int humor;
    //комуникации
    private int communication;


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

    public SendPhoto PhotoTheoryFloy(Long chatId) {
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
     * Изменить сопротивление дедлайну
     */
    public void humor(int delta) {
        this.humor += delta;
    }

    /**
     * Изменить аналитику
     */
    public void communication(int delta) {
        this.communication += delta;
    }


}
