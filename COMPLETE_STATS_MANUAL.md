# 🎯 ПОЛНОЕ РУКОВОДСТВО ПО РАБОТЕ СО СТАТАМИ ПЕРСОНАЖЕЙ
## *Мануал для дебилов с подробными комментариями*

---

## 📋 ОБЗОР: ЧТО ЗА ХУЙНЯ ТУТ ПРОИСХОДИТ?

В твоем проекте есть система статов персонажей, которая работает следующим образом:

1. **Пользователь** (`UserEntity`) имеет **персонажа** (`PersonageEntity`)
2. **Персонаж** имеет различные **статы** (деньги, аналитика, достижения и т.д.)
3. **Статы можно изменять** через специальные сервисы
4. **Изменения сохраняются в БД** автоматически
5. **Пользователю показываются красивые сообщения** с результатами

---

## 🏗️ АРХИТЕКТУРА: КТО ЗА ЧТО ОТВЕЧАЕТ

### 📊 **StatService** - Главный ебашка для статов
**Локация:** `src/main/java/org/example/service/ArrayList/StatService.java`

**За что отвечает:**
- ✅ Генерация случайных наград
- ✅ Применение изменений к статам персонажа
- ✅ Сохранение изменений в БД
- ✅ Null-безопасная работа с полями
- ✅ Определение индивидуальных статов для типов персонажей

**Основные методы:**
```java
// 🎲 Генерация случайных наград
Map<String, Integer> generateStandardRewards()
Map<String, Integer> generateCustomStatChanges()

// 🔄 Применение изменений к БД
void applyStatChanges(Long chatId, Map<String, Integer> statChanges)

// 📊 Утилиты для статов
String getIndividualStatForCharacter(String characterType)
String getStatDisplayInfo(String statName)
```

### 🎭 **StatsDisplayService** - Показуха для пользователей
**Локация:** `src/main/java/org/example/service/ArrayList/StatsDisplayService.java`

**За что отвечает:**
- ✅ Форматирование статов для отображения
- ✅ Создание красивых строк с эмодзи
- ✅ Показ текущих статов персонажа

### 👤 **UserService** - Работа с пользователями
**За что отвечает:**
- ✅ Получение пользователя по Telegram ID
- ✅ Сохранение изменений в БД

### 🎮 **PersonageService** - Работа с персонажами
**За что отвечает:**
- ✅ Обновление статов через старые методы (менее удобно)
- ✅ Случайные изменения статов

---

## 💾 ПОДДЕРЖИВАЕМЫЕ СТАТЫ

### 🔄 **Основные статы (все персонажи)**
```java
"achievement_points"  // ⭐️ Очки Достижения
"currency"           // 💲 Деньги
"optimization"       // ⚙️ Оптимизация  
"humor"             // 😄 Юмор
```

### 🎯 **Индивидуальные статы (зависят от типа персонажа)**
```java
// Personage1 (Аналитик)
"analytics"         // 📊 Аналитика

// Personage2 (Коммуникатор)  
"communication"     // 💬 Коммуникация

// Personage3 (Кодер)
"code_accuracy"     // 🎯 Точность кода
```

---

## 🚀 КАК БЛЯТЬ ИСПОЛЬЗОВАТЬ ЭТУ ХУЙНЮ

### 📖 **Вариант 1: Я хочу простые награды (деньги + достижения)**

```java
@RequiredArgsConstructor
@Service
public class MyAwesomeService {
    private final StatService statService;  // НЕ ЗАБУДЬ АВТОВАЙР!
    
    public SendMessage giveSimpleReward(Long chatId) {
        // 1. Генерируем стандартные награды (30-60 достижений, 200-400 денег)
        Map<String, Integer> rewards = statService.generateStandardRewards();
        
        // 2. СОХРАНЯЕМ В БД (ЭТО ВАЖНО, БЛЯТЬ!)
        statService.applyStatChanges(chatId, rewards);
        
        // 3. Возвращаем сообщение пользователю
        return new SendMessage(chatId.toString(), 
            "🎉 Ты получил награды!\n" +
            "+" + rewards.get("achievement_points") + " ⭐️ Достижений\n" +
            "+" + rewards.get("currency") + " 💲 Денег");
    }
}
```

### 🎯 **Вариант 2: Кастомные награды (я знаю, что хочу)**

```java
public SendMessage giveCustomReward(Long chatId) {
    // 1. Создаем свои изменения статов
    Map<String, Integer> customChanges = new HashMap<>();
    customChanges.put("achievement_points", 150);  // +150 достижений
    customChanges.put("currency", 500);           // +500 денег
    customChanges.put("analytics", 25);           // +25 аналитики (если персонаж Personage1)
    
    // 2. ПРИМЕНЯЕМ К БД
    statService.applyStatChanges(chatId, customChanges);
    
    // 3. Возвращаем сообщение
    return new SendMessage(chatId.toString(), 
        "🏆 ЭПИЧЕСКАЯ НАГРАДА!\n" +
        "+150 ⭐️ Достижений\n" +
        "+500 💲 Денег\n" +
        "+25 📊 Аналитики");
}
```

### 💸 **Вариант 3: Штрафы и смешанные изменения**

```java
public SendMessage applyPenalty(Long chatId) {
    // 1. Создаем изменения с отрицательными значениями
    Map<String, Integer> changes = new HashMap<>();
    changes.put("currency", -200);              // ШТРАФ: -200 денег
    changes.put("achievement_points", 50);      // КОМПЕНСАЦИЯ: +50 достижений
    changes.put("humor", 10);                   // БОНУС: +10 юмора
    
    // 2. ПРИМЕНЯЕМ К БД
    statService.applyStatChanges(chatId, changes);
    
    // 3. Возвращаем сообщение
    return new SendMessage(chatId.toString(), 
        "💔 Ты проебался, но получил опыт:\n" +
        "-200 💲 Денег (штраф за говнокод)\n" +
        "+50 ⭐️ Достижений (опыт)\n" +
        "+10 😄 Юмора (посмеялись над тобой)");
}
```

### 🎰 **Вариант 4: Казино-билеты (твой случай)**

```java
@RequiredArgsConstructor
@Service  
public class CasinoTicketService {
    private final StatService statService;
    private final UserService userService;  // Для проверки пользователя
    
    public SendMessage ticket1(Long chatId) {
        // 1. ПРОВЕРЯЕМ, что пользователь существует
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            return new SendMessage(chatId.toString(), "❌ Персонаж не найден!");
        }
        
        // 2. Определяем награды для билета 1
        Map<String, Integer> rewards = Map.of(
            "achievement_points", 100,  // +100 достижений
            "currency", 300            // +300 денег
        );
        
        // 3. СОХРАНЯЕМ НАГРАДЫ В БД
        statService.applyStatChanges(chatId, rewards);
        
        // 4. Возвращаем красивое сообщение
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode("Markdown");
        message.setText("🎁 *БИЛЕТ 1: \"NullPointerException\"*\n\n" +
                "*Итераториус:*\n" +
                "Смело, но немного безрассудно. Главное, чтобы прод это не увидел.\n" +
                "Иногда даже идиотизм — это стратегия.\n\n" +
                "*Навык повышен:*\n" +
                " +100 ⭐️ к Очкам Достижения\n" +
                " +300 💲 к Деньгам");
        
        return message;
    }
    
    public SendMessage ticket10_JACKPOT(Long chatId) {
        // ДЖЕКПОТ - больше наград!
        Map<String, Integer> jackpotRewards = Map.of(
            "achievement_points", 500,  // +500 достижений  
            "currency", 1000,          // +1000 денег
            "analytics", 50,           // +50 индивидуального стата
            "humor", 25               // +25 юмора (бонус)
        );
        
        statService.applyStatChanges(chatId, jackpotRewards);
        
        return new SendMessage(chatId.toString(),
            "💎 *ДЖЕКПОТ! БЛЯТЬ, ТЫ СОРВАЛ БАНК!* 💎\n\n" +
            "🎉 ЭПИЧЕСКИЕ НАГРАДЫ:\n" +
            "+500 ⭐️ Достижений\n" +
            "+1000 💲 Денег\n" +
            "+50 📊 Аналитики\n" +
            "+25 😄 Юмора\n\n" +
            "*Итераториус:* Невозможно! Это 0.1% шанс!");
    }
}
```

---

## 🔧 КАК СОЗДАВАТЬ УТИЛИТНЫЕ МЕТОДЫ

### 🎨 **Универсальный метод создания сообщений**

```java
/**
 * Создает красивое сообщение о наградах
 * @param chatId - ID чата
 * @param title - заголовок (например, "БИЛЕТ 1")  
 * @param description - описание от Итераториуса
 * @param rewards - карта наград
 * @return готовое сообщение
 */
private SendMessage createRewardMessage(Long chatId, String title, String description, 
                                      Map<String, Integer> rewards) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setParseMode("Markdown");
    
    StringBuilder text = new StringBuilder();
    text.append("🎁 *").append(title).append("*\n\n");
    
    if (description != null && !description.isEmpty()) {
        text.append("*Итераториус:*\n").append(description).append("\n\n");
    }
    
    text.append("*Награды:*\n");
    rewards.forEach((stat, value) -> {
        String emoji = getEmojiForStat(stat);
        String name = getDisplayNameForStat(stat);
        String sign = value > 0 ? "+" : "";
        text.append(" ").append(sign).append(value).append(" ").append(emoji).append(" ").append(name).append("\n");
    });
    
    message.setText(text.toString());
    return message;
}

// Эмодзи для статов
private String getEmojiForStat(String stat) {
    return switch (stat) {
        case "achievement_points" -> "⭐️";
        case "currency" -> "💲";
        case "analytics" -> "📊";
        case "communication" -> "💬";
        case "code_accuracy" -> "🎯";
        case "optimization" -> "⚙️";
        case "humor" -> "😄";
        default -> "📈";
    };
}

// Названия статов
private String getDisplayNameForStat(String stat) {
    return switch (stat) {
        case "achievement_points" -> "к Достижениям";
        case "currency" -> "к Деньгам";
        case "analytics" -> "к Аналитике";
        case "communication" -> "к Коммуникации";
        case "code_accuracy" -> "к Точности кода";
        case "optimization" -> "к Оптимизации";
        case "humor" -> "к Юмору";
        default -> "к Статам";
    };
}
```

### 🎯 **Использование утилитного метода**

```java
public SendMessage ticket5(Long chatId) {
    Map<String, Integer> rewards = Map.of(
        "achievement_points", 75,
        "currency", 250,
        "humor", 15
    );
    
    statService.applyStatChanges(chatId, rewards);
    
    return createRewardMessage(chatId, 
        "БИЛЕТ 5: \"ClassCastException\"", 
        "Приведение типов — дело тонкое. Но ты справился!",
        rewards);
}
```

---

## ⚠️ ВАЖНЫЕ ПРАВИЛА (ЧТОБЫ НЕ ОБОСРАТЬСЯ)

### 1. **ВСЕГДА используй StatService.applyStatChanges()**
```java
// ✅ ПРАВИЛЬНО
statService.applyStatChanges(chatId, rewards);

// ❌ НЕПРАВИЛЬНО - изменения не сохранятся в БД!
// Только создание сообщения без сохранения
```

### 2. **НЕ забывай @RequiredArgsConstructor и зависимости**
```java
// ✅ ПРАВИЛЬНО
@RequiredArgsConstructor
@Service
public class MyService {
    private final StatService statService;
    private final UserService userService;
}

// ❌ НЕПРАВИЛЬНО - зависимости будут null
@Service  
public class MyService {
    private StatService statService; // null!
}
```

### 3. **ПРОВЕРЯЙ пользователя перед применением статов**
```java
// ✅ ПРАВИЛЬНО
UserEntity user = userService.getUserByTgId(chatId);
if (user == null || user.getPersonage() == null) {
    return new SendMessage(chatId.toString(), "Персонаж не найден!");
}
statService.applyStatChanges(chatId, rewards);

// ❌ НЕПРАВИЛЬНО - может быть NullPointerException
statService.applyStatChanges(chatId, rewards); // Бум если юзер не найден
```

### 4. **ИСПОЛЬЗУЙ правильные названия статов**
```java
// ✅ ПРАВИЛЬНО
Map.of("achievement_points", 100, "currency", 300);

// ❌ НЕПРАВИЛЬНО - такие статы не поддерживаются
Map.of("experience", 100, "money", 300); // Будут проигнорированы!
```

---

## 🎭 ПРИМЕРЫ ДЛЯ РАЗНЫХ СИТУАЦИЙ

### 🏆 **Победа в квизе**
```java
public SendMessage handleQuizVictory(Long chatId) {
    Map<String, Integer> rewards = Map.of(
        "achievement_points", 200,
        "currency", 600,
        "analytics", 30  // Индивидуальный стат (если подходит персонажу)
    );
    
    statService.applyStatChanges(chatId, rewards);
    
    return createRewardMessage(chatId, 
        "🏆 ПОБЕДА В КВИЗЕ!", 
        "Блестящая работа! Твои знания впечатляют.",
        rewards);
}
```

### 💸 **Покупка в магазине**
```java
public SendMessage buyItem(Long chatId, String itemName, int price, Map<String, Integer> bonuses) {
    Map<String, Integer> changes = new HashMap<>();
    changes.put("currency", -price);  // Тратим деньги
    changes.putAll(bonuses);          // Получаем бонусы от предмета
    
    statService.applyStatChanges(chatId, changes);
    
    return createRewardMessage(chatId, 
        "🛒 ПОКУПКА: " + itemName, 
        "Инвестиции в себя — лучшие инвестиции!",
        changes);
}
```

### 💀 **Провал в задании**
```java
public SendMessage handleFailure(Long chatId) {
    Map<String, Integer> penalty = Map.of(
        "currency", -100,           // Штраф деньгами
        "achievement_points", 25,   // Немного опыта все же получаем
        "humor", 5                 // Хотя бы посмеялись
    );
    
    statService.applyStatChanges(chatId, penalty);
    
    return createRewardMessage(chatId, 
        "💀 ПРОВАЛ!", 
        "Не расстраивайся, на ошибках учатся. Главное - не в проде!",
        penalty);
}
```

---

## 🔍 КАК ОТЛАЖИВАТЬ ПРОБЛЕМЫ

### 📋 **Логирование**
StatService автоматически логирует все изменения:
```java
log.debug("StatService: Изменены деньги на {} для chatId={}", change, chatId);
log.info("StatService: Изменения статов применены для chatId={}", chatId);
```

### 🔧 **Проверка в БД**
После применения статов, данные сохраняются в таблице `personage_entity`. Можешь проверить:
```sql
SELECT * FROM personage_entity WHERE user_id = (
    SELECT id FROM user_entity WHERE tg_id = YOUR_CHAT_ID
);
```

### 🚨 **Частые ошибки**
1. **NullPointerException** - забыл проверить пользователя
2. **Статы не сохраняются** - забыл вызвать `applyStatChanges()`
3. **Зависимости null** - забыл `@RequiredArgsConstructor`

---

## 🎯 РЕЗЮМЕ: ЧТО НУЖНО ЗАПОМНИТЬ

1. **StatService.applyStatChanges()** - твой лучший друг для сохранения в БД
2. **Всегда проверяй пользователя** перед применением статов
3. **Используй правильные названия статов** (см. список выше)
4. **Создавай утилитные методы** для переиспользования кода
5. **Логируй все** для отладки
6. **Не забывай зависимости** через `@RequiredArgsConstructor`

## 🚀 ТЕПЕРЬ ТЫ МАСТЕР СТАТОВ!

С этим мануалом ты можешь создавать любые системы наград, штрафов, магазинов, квизов и казино! Главное - не забывай сохранять изменения в БД через `StatService.applyStatChanges()`. 

**Удачи, воин кода!** 💪
