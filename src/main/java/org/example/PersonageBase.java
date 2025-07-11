package org.example;


import lombok.Data;


import java.util.List;
import java.util.Random;

@Data
public abstract class PersonageBase {
    protected String name;
    protected int level;
    protected  int energy;
    protected int achievementPoints;
    protected double currency;
    protected String status;

    // Методы для увеличения/уменьшения характеристикм в дальнейшем думаю их использовать под разную сигнатуру метода
    public void changeLevel(int delta) {
        this.level += delta;
    }

    public  void energy(int delta) {
        this.energy += delta;

    }

    public void changeAchievementPoints(int delta) {
        this.achievementPoints += delta;
    }

    public void changeCurrency(int delta) {
        this.currency += delta;
    }

    public void setName(String name) {
        this.name = name;
    }



     public static PersonageBase getRandomPersonage() {
        List<PersonageBase> personages = List.of( new Personage1(),new Personage2(),new Personage3());
        return personages.get(new Random().nextInt(personages.size()));
    }



}
