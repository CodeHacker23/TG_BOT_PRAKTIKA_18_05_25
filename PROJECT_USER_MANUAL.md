# МАНУАЛ ПО ЮЗЕРАМ В БОТЕ (UserEntity, UserService и вся эта кухня)

---

## Что такое UserEntity?

**UserEntity** — это твой цифровой раб. Он хранит ВСЁ, что ты хочешь знать о пользователе: Telegram ID, имя, тип персонажа, его статы, прогресс, состояние, и даже то, сколько раз он пытался обмануть систему. Это Java-класс, помеченный как `@Entity`, и каждая его строчка — это столбец в таблице `users` в базе данных.

> **Если ты не понял, что такое Entity — иди гугли Hibernate, а потом возвращайся.**

---

## Где и как создаётся UserEntity?

- **Автоматически** при первом взаимодействии пользователя с ботом (например, при старте или создании персонажа).
- Через `userService.getUserByTgId(userId)`. Если такого пользователя нет — создаём нового, сохраняем в БД.
- Пример:
  ```java
  UserEntity user = userService.getUserByTgId(userId);
  if (user == null) {
      user = new UserEntity();
      user.setTgId(userId);
      userService.saveUser(user);
  }
  ```

---

## Какие поля есть у UserEntity?

- **id** — внутренний ID (PRIMARY KEY)
- **tgId** — Telegram user ID (уникальный)
- **username** — имя пользователя в Telegram
- **characterType** — тип персонажа (Personage1, Personage2, ...)
- **characterName** — имя персонажа
- **state** — текущее состояние пользователя (например, AWAITING_CHARACTER_NAME)
- **energy, level, achievementPoints, currency** — основные игровые параметры (int/double)
- **status** — статус персонажа (например, "Новобранец", "Легионер")
- **deadlineResistance, analytics, humor, communication, codeAccuracy, optimization** — уникальные параметры для разных персонажей (Integer, могут быть null)
- **passedArrayList** — флаг, прошёл ли пользователь сюжетку

> **Если ты добавил новое поле — не забудь про миграцию в БД!**

---

## Как правильно сохранять и обновлять параметры пользователя?

1. **Создаёшь персонажа?**
   - Заполни все нужные поля начальными значениями!
   - Сохрани через `userService.saveUser(user)`.
2. **Меняешь параметры (статус, уровень, аналитику и т.д.)?**
   - Меняешь поле у персонажа и у UserEntity.
   - Сохраняешь через `userService.saveUser(user)`.
3. **Восстанавливаешь персонажа?**
   - Всегда бери значения из UserEntity, а не из дефолтного конструктора!

---

## Пример: как не просрать данные пользователя

```java
// Получаем пользователя
UserEntity user = userService.getUserByTgId(userId);
if (user == null) {
    user = new UserEntity();
    user.setTgId(userId);
    userService.saveUser(user);
}

// Восстанавливаем персонажа
Personage1 p1 = new Personage1();
p1.setName(user.getCharacterName());
p1.setLevel(user.getLevel());
p1.setEnergy(user.getEnergy());
p1.setAchievementPoints(user.getAchievementPoints());
p1.setCurrency(user.getCurrency());
p1.setStatus(user.getStatus());
p1.setDeadlineResistance(user.getDeadlineResistance() != null ? user.getDeadlineResistance() : 0);
p1.setAnalytics(user.getAnalytics() != null ? user.getAnalytics() : 0);

// Меняем параметры
p1.setStatus("Легионер");
p1.setLevel(1);
int delta = 24 + new java.util.Random().nextInt(12);
p1.setAnalytics(p1.getAnalytics() + delta);

// Сохраняем обратно
user.setStatus(p1.getStatus());
user.setLevel(p1.getLevel());
user.setAnalytics(p1.getAnalytics());
userService.saveUser(user);
```

---

## Логирование (чтобы потом не искать баги по ночам)

- Логируй ВСЁ: создание, обновление, сохранение пользователя, любые изменения параметров.
- Используй `log.info` и `log.error` с подробным описанием:
  ```java
  log.info("[UserService] getUserByTgId() — ищем пользователя по tgId={}", userId);
  log.info("[UserService] saveUser() — сохраняем пользователя: {}", user);
  log.info("[Personage1] Присвоен новый статус: {} и уровень: {}. Аналитика увеличена на {} (итого: {})", p1.getStatus(), p1.getLevel(), delta, p1.getAnalytics());
  log.error("[UserService] Ошибка при сохранении пользователя: {}", e.getMessage());
  ```

---

## Как расширять UserEntity и не словить депрессию

- Добавил новое поле? Добавь его в класс, в миграцию БД, и везде, где идёт восстановление/сохранение.
- Не пихай бизнес-логику в UserEntity — только данные!
- Для новых персонажей — добавляй уникальные поля только для них (например, humor для Personage2).
- Не забывай про null для Integer!

---

## FAQ для самых тупых

**Q: Почему у меня всегда статус null?**
A: Ты не сохраняешь статус в UserEntity после изменения. Меняй и сохраняй!

**Q: Почему после нажатия кнопки параметры сбрасываются?**
A: Ты не восстанавливаешь их из UserEntity, а берёшь из дефолтного конструктора.

**Q: Почему у меня NPE на getDeadlineResistance()?**
A: Ты не проверяешь Integer на null. Пиши: `user.getDeadlineResistance() != null ? user.getDeadlineResistance() : 0`

**Q: Как добавить новый параметр?**
A: Добавь поле в UserEntity, в миграцию, в восстановление/сохранение, и не забудь про null!

**Q: Можно ли хранить бизнес-логику в UserEntity?**
A: Нет! Только данные. За бизнес-логику — бан и личное сообщение от Архитектора.

---

## Советы от Архитектора

- Логируй всё, что делаешь с пользователем.
- Не забывай про saveUser() после любого изменения.
- Не бойся делать restore-методы для персонажей, если их станет много.
- Не копипасть — выноси повторяющееся в методы.
- Если что-то не работает — смотри логи, а не гадай на кофейной гуще.

---

## Чёрный юмор и реальная боль

- Если ты забыл сохранить пользователя — твои пользователи будут страдать, а ты будешь страдать ещё больше.
- Если ты не логируешь — баги найдут тебя ночью.
- Если ты не проверяешь на null — жди NPE и проклятий в свой адрес.
- Если ты не понял этот мануал — возможно, тебе стоит сменить профессию. Или перечитать ещё раз.

---

## TL;DR (для совсем ленивых)

- Всегда сохраняй изменения пользователя через userService.saveUser(user)
- Всегда восстанавливай параметры из UserEntity, а не из дефолтного конструктора
- Логируй всё
- Не пихай бизнес-логику в UserEntity
- Проверяй Integer на null
- Не тупи — читай мануал! 

---

## Как красиво выводить карточку персонажа с изменёнными параметрами (статус, аналитика +delta)

### Проблема

Ты хочешь, чтобы после изменения параметров (например, повышения статуса или увеличения аналитики) бот отправлял пользователю карточку с:
- Новым статусом (например, "Легионер")
- Актуальной аналитикой и явно показанным приростом: `📊Аналитика: 110 (+24)`

### Как это сделать правильно

1. **Создай отдельный метод в классе персонажа**

```java
/**
 * Возвращает карточку персонажа с обновлённой аналитикой и новым статусом.
 * @param chatId — ID чата Telegram
 * @param delta — на сколько увеличилась аналитика (показывается в скобках)
 * @return SendPhoto — карточка персонажа с красивым выводом
 */
public SendPhoto getSendPhotoWithDelta(Long chatId, int delta) {
    return SendPhoto.builder()
            .chatId(chatId.toString())
            .photo(new InputFile("https://ltdfoto.ru/image/sCNCjY"))
            .caption(
                "*" + name + "*" + "\n\n" +
                "_Статус_: " + status + "\n" +
                "\uD83C\uDFC6Level: " + level + "\n" +
                "⚡Энергия: " + energy + "\n" +
                "⭐Очки достижения: " + achievementPoints + "\n" +
                "\uD83D\uDCB2Деньги: " + currency + "\n" +
                "⌚Сопротивление дедлайну: " + deadlineResistance + " — Привык работать под давлением сроков, но не всегда этому рад. \n" +
                "📊Аналитика: " + analytics + " (+" + delta + ") — Умение находить скрытые связи в коде\n\n" +
                "\uD83D\uDCDC Комментарий от Итераториуса:\n" +
                "_Не каждый новичок носит доспех. Но каждый герой — начинал в нём._"
            )
            .parseMode("Markdown")
            .build();
}
```

2. **Удаляй старые нерабочие реализации**

Если у тебя остались старые return'ы с кривой конкатенацией или синтаксическими ошибками (например, `... + "= )"` или неправильный вызов `.parseMode("Markdown")` не на билдере), их нужно удалить. Иначе компилятор будет ругаться, а бот — страдать.

3. **Используй этот метод в обработчиках кнопок**

В обработчике, где ты меняешь параметры:
```java
p1.setStatus("Легионер");
int delta = ...; // твой рандом
p1.setAnalytics(p1.getAnalytics() + delta);

SendPhoto photo = p1.getSendPhotoWithDelta(chatId, delta);
bot.execute(photo);
```

4. **Почему так?**
- Ты всегда показываешь пользователю актуальные значения и прирост.
- Код становится чище, не дублируется, легко поддерживать.
- Нет синтаксических ошибок и багов с форматированием.

5. **Чёрный юмор**
- Если ты оставишь старые return'ы с ошибками — компилятор будет материться, а Архитектор лично напишет тебе в Telegram.
- Если ты не покажешь пользователю, сколько он получил аналитики — он подумает, что бот его кидает, и уйдёт к конкурентам.

--- 

---

## Как выводить карточку персонажа с приростом (дельтой) по каждому параметру

### Зачем это нужно?

- Пользователь должен видеть не только итоговое значение, но и сколько он получил за действие (например, +50 к очкам достижения, +25 к аналитике).
- Это мотивирует, делает механику прозрачной и вызывает у пользователя чувство прогресса (а у тебя — меньше вопросов в поддержку).
- Такой подход облегчает отладку: ты всегда видишь, что и как изменилось.

### Как это реализовать правильно

1. **В классе персонажа делай универсальный метод для карточки с дельтами:**

```java
/**
 * Возвращает карточку персонажа с изменёнными очками достижения и аналитикой.
 * Показывает старое значение, прирост и итог для каждого параметра.
 */
public SendPhoto getSendPhotoWithDeltas(Long chatId, int achievementDelta, int analyticsDelta) {
    return SendPhoto.builder()
            .chatId(chatId.toString())
            .photo(new InputFile("https://ltdfoto.ru/image/sCNCjY"))
            .caption(
                "*" + name + "*" + "\n\n" +
                "_Статус_: " + status + "\n" +
                "🏆Level: " + level + "\n" +
                "⚡️Энергия: " + energy + "\n" +
                "⭐️Очки достижения: " + (achievementPoints - achievementDelta) + " (+" + achievementDelta + ") = " + achievementPoints + "\n" +
                "💲Деньги: " + currency + "\n" +
                "⌚️Сопротивление дедлайну: " + deadlineResistance + " — Привык работать под давлением сроков, но не всегда этому рад.\n" +
                "📊Аналитика: " + (analytics - analyticsDelta) + " (+" + analyticsDelta + ") = " + analytics + " — Умение находить скрытые связи в коде\n\n" +
                "📜 Комментарий от Итераториуса:\n" +
                "_Не каждый новичок носит доспех. Но каждый герой — начинал в нём._"
            )
            .parseMode("Markdown")
            .build();
}
```

2. **В обработчике кнопки:**
- Получи пользователя и восстанови персонажа из UserEntity.
- Сохрани старые значения параметров (например, oldAchievement, oldAnalytics).
- Измени параметры (добавь дельты, измени статус, уровень и т.д.).
- Сохрани новые значения обратно в UserEntity и в БД.
- Вызови метод getSendPhotoWithDeltas, передав дельты.

```java
int oldAchievement = p1.getAchievementPoints();
int achievementDelta = 50;
int oldAnalytics = p1.getAnalytics();
int analyticsDelta = 25;
p1.setAchievementPoints(oldAchievement + achievementDelta);
p1.setAnalytics(oldAnalytics + analyticsDelta);
p1.setStatus("Изменено = Легионер");
p1.setLevel(1);
user.setAchievementPoints(p1.getAchievementPoints());
user.setAnalytics(p1.getAnalytics());
user.setStatus(p1.getStatus());
user.setLevel(p1.getLevel());
userService.saveUser(user);
SendPhoto photo = p1.getSendPhotoWithDeltas(chatId, achievementDelta, analyticsDelta);
bot.execute(photo);
```

3. **Почему это важно?**
- Пользователь видит, что его действия реально влияют на параметры.
- Ты всегда можешь проверить, что именно изменилось (и не словить багу).
- Такой подход легко расширять: добавляй новые параметры и дельты по аналогии.

4. **Советы и чёрный юмор**
- Не забывай сохранять новые значения в UserEntity и в БД, иначе пользователь увидит старые статы и будет думать, что бот его кидает.
- Не копипасть — делай универсальные методы для карточек с дельтами.
- Если ты не показываешь дельты — пользователь не поймёт, за что его наградили, и уйдёт к конкурентам.
- Если ты не понял этот раздел — перечитай ещё раз, а потом спроси у Архитектора.

--- 

---

## Переход на отдельную таблицу персонажа (PersonageEntity): зачем, как, и почему это круто

### Зачем выносить персонажа в отдельную таблицу?

- **Чистота архитектуры:** Не мешаем всё в одну кучу. UserEntity — только про пользователя, PersonageEntity — только про персонажа.
- **Гибкость:** Легко расширять, добавлять новые поля, уникальные параметры, не боясь сломать всю таблицу users.
- **Производительность:** Быстрее искать, проще мигрировать, меньше шансов словить багу при изменениях.
- **Безопасность:** Меньше риска случайно затереть или перепутать данные пользователя и персонажа.

---

### Как теперь устроено хранение данных

- **UserEntity** — хранит только данные пользователя (tgId, username и т.д.) и ссылку на персонажа:
  ```java
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private PersonageEntity personage;
  ```
- **PersonageEntity** — отдельная сущность, где лежат ВСЕ игровые параметры, статы, уникальные поля, статус, уровень, аналитика и т.д.:
  ```java
  @Entity
  @Table(name = "personages")
  public class PersonageEntity {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      @OneToOne
      @JoinColumn(name = "user_id", nullable = false, unique = true)
      private UserEntity user;

      private String characterType;
      private String name;
      private int level;
      private int energy;
      private int achievementPoints;
      private double currency;
      private String status;
      private Integer deadlineResistance;
      private Integer analytics;
      // ... и любые другие поля
  }
  ```

---

### Как теперь работать с персонажем

**Создать персонажа:**
```java
UserEntity user = userService.getUserByTgId(userId);
if (user.getPersonage() == null) {
    PersonageEntity p = new PersonageEntity();
    p.setUser(user);
    p.setCharacterType("Personage1");
    p.setName("Максим");
    p.setLevel(0);
    p.setEnergy(8);
    p.setAchievementPoints(0);
    p.setCurrency(0.0);
    p.setStatus("Новобранец");
    p.setDeadlineResistance(90);
    p.setAnalytics(110);
    personageRepository.save(p);
    user.setPersonage(p);
    userService.saveUser(user);
}
```

**Изменить параметры:**
```java
PersonageEntity p = user.getPersonage();
int oldAnalytics = p.getAnalytics();
int analyticsDelta = 30;
p.setAnalytics(oldAnalytics + analyticsDelta);
personageRepository.save(p);
```

---

### Как расширять и не словить депрессию

- Все новые игровые поля добавляй только в PersonageEntity (и в миграцию БД).
- Для новых персонажей просто добавляй уникальные поля (или делай наследование, если захочешь усложнить архитектуру).
- Не пихай бизнес-логику в сущности — только данные!
- Если захочешь несколько персонажей на пользователя — меняй связь на @OneToMany.

---

### Плюсы такой архитектуры

- **Масштабируемость:** Легко добавлять новые поля, не боясь сломать users.
- **Чистота:** Нет каши из полей, всё по полочкам.
- **Гибкость:** Можно сделать хоть 100 персонажей на пользователя.
- **Отладка:** Проще искать баги, меньше шансов случайно затереть данные.

---

### Чёрный юмор и советы

- Если ты опять начнёшь хранить всё в одной таблице — Архитектор лично напишет тебе в Telegram.
- Если забудешь добавить новое поле в миграцию — будешь ловить NPE и страдать.
- Если не понял, зачем это всё — перечитай ещё раз, а потом спроси у Архитектора.

---

### TL;DR
- UserEntity — только про пользователя.
- PersonageEntity — только про персонажа.
- Все игровые параметры теперь лежат в personages.
- Не тупи — читай мануал! 

---

## Как правильно формировать карточку персонажа с дельтами по всем параметрам (и не словить багу с нулями)

### Проблема

Если ты не восстанавливаешь реальные значения параметров из БД перед изменением, у тебя всегда будут нули, и пользователь будет думать, что бот его кидает. Особенно это касается дедлайн-резистанса, аналитики, денег и других статов, у которых есть дефолтные значения (например, 90, 110, 0.0).

### Как делать правильно

1. **Перед изменением ВСЕГДА восстанавливай параметры из UserEntity (или PersonageEntity), а если null — подставляй дефолт:**
   ```java
   int oldDeadline = user.getDeadlineResistance() != null ? user.getDeadlineResistance() : 90;
   int oldAnalytics = user.getAnalytics() != null ? user.getAnalytics() : 110;
   double oldCurrency = user.getCurrency();
   // ... и так далее
   ```
2. **Сохраняй старые значения до изменения, чтобы корректно считать дельты.**
3. **Формируй строку вывода строго по шаблону пользователя:**
   ```java
   String msg =
       "Статус: " + newStatus + "\n" +
       "🏆Level: " + oldLevel + " (+" + levelDelta + ") = " + newLevel + "\n" +
       "⚡️Энергия: " + energy + "\n" +
       "⭐️Очки достижения: " + oldAchievement + " (+" + achievementDelta + ") = " + newAchievement + "\n" +
       "💲Деньги: " + String.format("%.1f", oldCurrency) + " (+" + String.format("%.0f", currencyDelta) + ") = " + String.format("%.0f", newCurrency) + " руб.\n" +
       "⌚️Сопротивление дедлайну: " + oldDeadline + " — Привык работать под давлением сроков, но не всегда этому рад.\n" +
       "📊Аналитика: " + oldAnalytics + " (+" + analyticsDelta + ") = " + newAnalytics + " — Умение находить скрытые связи в коде\n\n" +
       "📜 Комментарий от Итераториуса:\n" +
       "Не каждый новичок носит доспех. Но каждый герой — начинал в нём.";
   ```
4. **Сохраняй новые значения обратно в БД!**

### Почему это важно?
- Пользователь видит честную статистику, а не нули.
- Ты не ловишь баги с обнулением параметров.
- Код становится прозрачным и легко поддерживаемым.

### Чёрный юмор и советы
- Если ты не восстанавливаешь параметры из БД — пользователь будет думать, что бот его кидает, а Архитектор лично напишет тебе в Telegram.
- Если ты не показываешь дельты — пользователь не поймёт, за что его наградили, и уйдёт к конкурентам.
- Если ты не понял этот раздел — перечитай ещё раз, а потом спроси у Архитектора.

--- 

---

# Как правильно отправлять карточки, описания и статусы через SendPhoto (2024, с болью и юмором)

## TL;DR
- Всё, что раньше было в SendMessage.setText(), теперь в SendPhoto.setCaption().
- Проверяй длину caption (1024 символа, иначе Telegram тебя пошлёт).
- Используй parseMode (MarkdownV2, если есть спецсимволы).
- Все переменные вычисляй ДО формирования строки.
- Вызов только один: bot.execute(photo).
- Не вызывай SendMessage для карточек/описаний, если отправляешь фото.

---

## Пошаговый туториал (с примерами и чёрным юмором)

### 1. Формируй весь текст заранее
```java
String msg = "*Имя:* " + name + "\n" +
             "_Статус:_ " + status + "\n" +
             "Level: " + oldLevel + " (+" + levelDelta + ") = " + newLevel;
```

### 2. Проверь длину caption
```java
if (msg.length() > 1024) {
    msg = msg.substring(0, 1021) + "..."; // Telegram не любит длинные подписи
}
```

### 3. Используй SendPhoto с caption
```java
SendPhoto photo = SendPhoto.builder()
    .chatId(chatId)
    .photo(new InputFile(photoUrl))
    .caption(msg)
    .parseMode("MarkdownV2") // или "Markdown", если не используешь спецсимволы
    .build();
bot.execute(photo); // Только один вызов!
```

### 4. Не вызывай SendMessage для карточек/описаний
- Если ты отправляешь фото с caption — не надо дублировать текст через SendMessage.
- Если отправишь и то, и то — пользователь подумает, что у тебя раздвоение личности.

### 5. Все переменные вычисляй ДО формирования строки
```java
int oldLevel = ...;
int newLevel = ...;
int levelDelta = newLevel - oldLevel;
// Формируй msg только после всех вычислений!
```

### 6. Пример полной замены
**Было:**
```java
SendMessage message = new SendMessage(chatId.toString(), msg);
bot.execute(message);
```
**Стало:**
```java
if (msg.length() > 1024) msg = msg.substring(0, 1021) + "...";
SendPhoto photo = SendPhoto.builder()
    .chatId(chatId)
    .photo(new InputFile(photoUrl))
    .caption(msg)
    .parseMode("MarkdownV2")
    .build();
bot.execute(photo);
```

---

## FAQ (с болью и сарказмом)
- **Q:** А если caption длиннее 1024?  
  **A:** Telegram тебя пошлёт. Обрезай или дели на части.
- **Q:** Можно ли отправить и фото, и текст отдельно?  
  **A:** Можно, но пользователь подумает, что ты бот-спамер.
- **Q:** А если забыть parseMode?  
  **A:** MarkdownV2 спасёт твои эмодзи и жирные буквы. Без него всё будет уныло.
- **Q:** Почему нельзя просто оставить SendMessage?  
  **A:** Потому что Архитектор будет материться, а пользователи — уходить.

---

## Итог
- Всё, что раньше было в SendMessage для карточек/статусов/описаний, теперь в caption у SendPhoto.
- Проверяй длину, вычисляй переменные заранее, используй parseMode, и не дублируй сообщения.
- Если сделаешь не так — компилятор будет ругаться, а Архитектор напишет тебе ночью.

--- 
