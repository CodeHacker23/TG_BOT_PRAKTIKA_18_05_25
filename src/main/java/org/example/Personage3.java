package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;



public class Personage3 extends PersonageBase {
    private int codeAccuracy;
    private int optimization;

    public Personage3() {
        this.name = "";
        this.level = 0;
        this.energy = 8;
        this.achievementPoints = 0;
        this.currency = 0;
        this.codeAccuracy = 140;
        this.optimization = 160;
        this.status = "Новобранец";
    }

    public SendPhoto PhotoTheoryGeks(Long chatId) {

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
                        "⚙\uFE0FПроцесс оптимизации: " +  optimization + " — Перепишет твой код так, что компилятор удивится..")
                .parseMode("Markdown")
                .build();
    }

    //изменить свойство точность кода
    public void codeAccuracy(int delta){
        this.codeAccuracy += delta;
    }

    //изменить свойство оптимизации
    public void optimization(int delta){
        this.optimization  += delta;
    }


}
