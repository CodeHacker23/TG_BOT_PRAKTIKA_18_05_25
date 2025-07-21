# Мануал по работе с персонажем 1 (Personage1)

## 1. Что такое Personage1

`Personage1` — это Java-класс, описывающий одного из игровых персонажей бота. Он наследует базовые характеристики из `PersonageBase` и добавляет свои уникальные параметры (например, сопротивление дедлайну и аналитику).

---

## 2. Структура класса Personage1

```java
public class Personage1 extends PersonageBase {
    private int deadlineResistance; // Сопротивление дедлайну
    private int analytics;          // Аналитика

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

    // Методы для изменения характеристик
    public void changeDeadlineResistance(int delta) { this.deadlineResistance += delta; }
    public void changeAnalytics(int delta) { this.analytics += delta; }

    // Геттеры
    public int getDeadlineResistance() { return deadlineResistance; }
    public int getAnalytics() { return analytics; }

    // Метод для отправки карточки персонажа
    public SendPhoto getSendPhotoTheory(Long chatId) { ... }
}
```

---

## 3. Как обновлять характеристики персонажа в БД

1. Получить пользователя по chatId:
   ```java
   UserEntity user = userService.getUserByTgId(chatId);
   ```
2. Получить его персонажа:
   ```java
   PersonageEntity personage = user.getPersonage();
   ```
3. Изменить нужные параметры:
   ```java
   personage.setStatus("Легионер");
   personage.setLevel(1);
   personage.setAchievementPoints(50);
   personage.setCurrency(450.0);
   personage.setAnalytics(145);
   // и т.д.
   ```
4. Сохранить изменения:
   ```java
   personageRepository.save(personage);
   ```

---

## 4. Универсальный метод обновления характеристик

```java
public void updatePersonageStats(PersonageEntity personage, int levelDelta, int achievementDelta, double currencyDelta, int analyticsDelta) {
    personage.setLevel(personage.getLevel() + levelDelta);
    personage.setAchievementPoints(personage.getAchievementPoints() + achievementDelta);
    personage.setCurrency(personage.getCurrency() + currencyDelta);
    personage.setAnalytics(personage.getAnalytics() + analyticsDelta);
    personageRepository.save(personage);
}
```

---

## 5. Как выводить карточку персонажа с нужным текстом и фото

В классе `Personage1` делаем метод:

```java
public SendPhoto getRomanArmorCard(Long chatId) {
    String caption = "*" + name + "*\n\n" +
        "_Статус_: 'Изменено' = Легионер\n" +
        "🏆Level: " + level + " (+1)\n" +
        "⚡️Энергия: " + energy + "\n" +
        "⭐️Очки достижения: " + achievementPoints + " (+50)\n" +
        "💲Деньги: " + currency + " (+450)\n" +
        "⌚️Сопротивление дедлайну: " + deadlineResistance + " — Привык работать под давлением сроков, но не всегда этому рад.\n" +
        "📊Аналитика: " + analytics + " (+35) — Умение находить скрытые связи в коде\n\n" +
        "📜 Комментарий от Итераториуса:\n" +
        "_Не каждый новичок носит доспех. Но каждый герой — начинал в нём._\n" +
        "держи +35 к 📊 аналитике, за храбрость.";

    return SendPhoto.builder()
        .chatId(chatId.toString())
        .photo(new InputFile("https://ltdfoto.ru/image/sCNCjY"))
        .caption(caption)
        .parseMode("Markdown")
        .build();
}
```

---

## 6. Как вызывать это из обработчика кнопки

```java
UserEntity user = userService.getUserByTgId(chatId);
PersonageEntity personage = user.getPersonage();

// Обновляем параметры
personage.setStatus("Легионер");
personage.setLevel(personage.getLevel() + 1);
personage.setAchievementPoints(personage.getAchievementPoints() + 50);
personage.setCurrency(personage.getCurrency() + 450.0);
personage.setAnalytics(personage.getAnalytics() + 35);
personageRepository.save(personage);

// Получаем объект персонажа (например, Personage1)
Personage1 p1 = new Personage1();
p1.setName(personage.getName());
p1.setLevel(personage.getLevel());
p1.setEnergy(personage.getEnergy());
p1.setAchievementPoints(personage.getAchievementPoints());
p1.setCurrency(personage.getCurrency());
p1.setStatus(personage.getStatus());
p1.changeAnalytics(personage.getAnalytics() - p1.getAnalytics());
p1.changeDeadlineResistance(personage.getDeadlineResistance() - p1.getDeadlineResistance());

// Отправляем карточку
SendPhoto photo = p1.getRomanArmorCard(chatId);
bot.execute(photo);
```

---

## 7. Как добавить новые характеристики

- Добавь новое поле в `PersonageBase` или в `Personage1`.
- Добавь геттер/сеттер.
- Добавь обработку в методах обновления и вывода карточки.

---

## 8. FAQ и советы

- **Все изменения характеристик — через сущность персонажа и репозиторий.**
- **Вывод карточки — через метод в классе персонажа, который возвращает SendPhoto с нужным текстом и фоткой.**
- **Добавлять новые характеристики — просто: поле, геттер/сеттер, обработка в сервисе и карточке.**
- **Для каждого персонажа — свой метод карточки, свой шаблон текста.**

---

## 9. Чёрный юмор и напутствие

Если ты всё ещё не понял, как обновлять характеристики персонажа — иди и перечитай этот мануал ещё раз. Если и после этого не понял — зови Архитектора, он тебе объяснит на пальцах (и, возможно, на твоём же коде). Не бойся экспериментировать — хуже, чем было, уже не будет! 

---

# Как правильно обновлять характеристики персонажа с рандомом, считать дельту и не словить боль JPA/Spring

## 1. Схема таблиц (упрощённо)

```mermaid
erDiagram
    USERS ||--o| PERSONAGES : has
    USERS {
        Long id
        Long tgId
        String username
        ...
    }
    PERSONAGES {
        Long id
        Long user_id
        String name
        int level
        int analytics
        ...
    }
```

## 2. Как обновлять характеристики с рандомом

- Перед апдейтом сохраняй старое значение аналитики:
  ```java
  int oldAnalytics = personage.getAnalytics() != null ? personage.getAnalytics() : 0;
  ```
- Вызывай сервис с рандомом:
  ```java
  personageService.updateStats(personage, 1, 50, 450.0, 25, 30, true);
  ```
- После апдейта получай новое значение и дельту:
  ```java
  int newAnalytics = personage.getAnalytics() != null ? personage.getAnalytics() : 0;
  int analyticsDelta = newAnalytics - oldAnalytics;
  ```

## 3. Как формировать карточку с дельтой

- Передавай дельту в метод карточки:
  ```java
  bot.execute(personage1.getRomanArmorCard(chatId, analyticsDelta));
  ```
- В карточке:
  ```java
  "📊Аналитика: " + analytics + " (+" + analyticsDelta + ") — Умение находить скрытые связи в коде"
  ```

## 4. Как избежать боли с JPA/Spring

- **Не делай статических сервисов!** Всегда внедряй сервисы через конструктор и @Service/@RequiredArgsConstructor.
- **Не вызывай toString() у связанных сущностей!** Это вызывает рекурсию и StackOverflow.
- **Не храни бизнес-логику в контроллере/хендлере — выноси в сервисы.**
- **Проверяй, что все зависимости внедряются через Spring, а не через new.**
- **Проверяй, что все методы, которые должны быть экземплярными, не объявлены static.**

## 5. Чёрный юмор и советы

- Если ты всё ещё ловишь NullPointerException — проверь, не забыл ли ты внедрить сервис через конструктор.
- Если у тебя в карточке всегда +0 — ты забыл считать дельту до апдейта.
- Если бот падает с рекурсией — убери связанные сущности из toString().
- Если ты всё ещё не понял, перечитай этот мануал, потом спроси у Архитектора, потом выпей кофе и попробуй ещё раз.

---

**Теперь ты умеешь делать карточки с рандомом, дельтой и без боли!** 