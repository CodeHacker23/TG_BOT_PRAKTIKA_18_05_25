# 🎮 ПОЛНЫЙ МАНУАЛ ARRAYLIST: ОТ КНОПКИ ДО СТАТОВ (АКТУАЛЬНАЯ ВЕРСИЯ)

---

## 📋 СОДЕРЖАНИЕ

1. [Обзор архитектуры](#обзор-архитектуры)
2. [Создание кнопки](#создание-кнопки)
3. [Обработка нажатия](#обработка-нажатия)
4. [Генерация наград](#генерация-наград)
5. [Создание сообщений](#создание-сообщений)
6. [Планирование событий](#планирование-событий)
7. [Полная логическая цепочка](#полная-логическая-цепочка)
8. [Примеры кода](#примеры-кода)

---

## 🏗️ ОБЗОР АРХИТЕКТУРЫ

### Новая чистая архитектура (после рефакторинга):

```
┌─────────────────────────────────────────────────────────────┐
│                    ARRAYLIST АРХИТЕКТУРА                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  🎮 ArrayListStory (главный координатор)                   │
│  ├── Прямая обработка боевых действий                      │
│  ├── Маршрутизация команд                                  │
│  └── Координация между сервисами                           │
│                                                             │
│  📊 StatService (работа со статами)                        │
│  ├── Генерация случайных наград                           │
│  ├── Применение изменений к персонажу                     │
│  ├── Определение индивидуальных статов                     │
│  └── Безопасная работа с null значениями                  │
│                                                             │
│  💬 MessageService (создание сообщений)                    │
│  ├── Создание боевых сообщений                            │
│  ├── Создание сообщений с результатами                     │
│  ├── Создание сообщений с наградами                        │
│  └── Единообразное форматирование                          │
│                                                             │
│  ⏰ ArrayListSchedulerService (планирование)               │
│  ├── Планирование отложенных событий                       │
│  ├── Автоматическое удаление сообщений                     │
│  ├── Сложные последовательности событий                    │
│  └── Универсальные методы планирования                     │
│                                                             │
│  📚 ArrayListTheoryService (теория и контент)             │
│  ├── Форматирование теории                                 │
│  ├── Диалоги персонажей                                    │
│  └── Игровой контент                                       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 🎯 Что изменилось после рефакторинга:

**Было:** 7 классов с лишними слоями абстракции  
**Стало:** 5 классов с четким разделением ответственности

**Убрали говнокод:**
- ❌ `BattleActionService` — 98 строк ненужного кода
- ❌ `ArrayListBattleService` — 218 строк ненужного кода

**Результат:** Убрано 317 строк говнокода, архитектура стала чище и понятнее!

---

## 🔘 СОЗДАНИЕ КНОПКИ

### 1. Где создаются кнопки:
**Файл:** `src/main/java/org/example/bot/KeyboardService/KeyboardReam.java`

### 2. Пример создания кнопки:
```java
public static ReplyKeyboardMarkup BattlArreyn(Long chatId) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    keyboardMarkup.setSelective(true);
    keyboardMarkup.setResizeKeyboard(true);
    keyboardMarkup.setOneTimeKeyboard(false);

    List<KeyboardRow> keyboard = new ArrayList<>();
    
    // Первый ряд кнопок
    KeyboardRow row1 = new KeyboardRow();
    row1.add("🛡 Блокировать \n(try-catch)");
    row1.add("🔍 Уклониться \n и \nпроанализировать");
    
    // Второй ряд кнопок
    KeyboardRow row2 = new KeyboardRow();
    row2.add("🚨 Отразить \nвставкой \n в начало");
    
    keyboard.add(row1);
    keyboard.add(row2);
    keyboardMarkup.setKeyboard(keyboard);
    
    return keyboardMarkup;
}
```

### 3. Важные моменты:
- **Эмодзи обязательны** — они идентифицируют кнопку
- **Текст должен точно совпадать** с обработчиком
- **Переносы строк** (`\n`) важны для форматирования

---

## 🎯 ОБРАБОТКА НАЖАТИЯ (АКТУАЛЬНО)

### 1. Где обрабатываются команды:
**Файл:** `src/main/java/org/example/service/ArrayList/ArrayListStory.java`

### 2. Регистрация обработчика (АКТУАЛЬНО):
```java
// В методе initCommands() — ПРЯМАЯ ОБРАБОТКА БЕЗ ЛИШНИХ СЛОЕВ!
commandsMap.put("🚨 Отразить \nвставкой \n в начало", (bot, msg) -> {
    log.info("ArrayListStory: Обработка команды 'Отразить' для chatId={}", msg.getChatId());
    
    try {
        // 1. Отправляем первое сообщение
        SendMessage firstMessage = messageService.createInsertBeginningMessage(msg.getChatId());
        bot.execute(firstMessage);
        
        // 2. Через 3 секунды отправляем результат от Итераториуса
        schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
            try {
                // Генерируем кастомные изменения статов
                Map<String, Integer> statChanges = statService.generateCustomStatChanges();
                
                // Добавляем индивидуальный стат для персонажа
                var user = userService.getUserByTgId(msg.getChatId());
                if (user != null && user.getPersonage() != null) {
                    String characterType = user.getPersonage().getCharacterType();
                    String individualStat = statService.getIndividualStatForCharacter(characterType);
                    statChanges.put(individualStat, statService.generateRandomReward(15, 25));
                }
                
                // Применяем изменения статов
                statService.applyStatChanges(msg.getChatId(), statChanges);
                
                // Отправляем результат с изменениями статов
                SendMessage resultMessage = messageService.createInsertBeginningResultMessage(msg.getChatId(), statChanges);
                bot.execute(resultMessage);
                
                // 3. Еще через 3 секунды отправляем завершение раунда
                schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                    try {
                        SendMessage endRoundMessage = messageService.endOfRoundOne(msg.getChatId());
                        bot.execute(endRoundMessage);
                    } catch (TelegramApiException e) {
                        log.error("Ошибка отправки завершения раунда", e);
                    }
                }, 3);
                
            } catch (TelegramApiException e) {
                log.error("Ошибка отправки результата", e);
            }
        }, 3);
        
    } catch (TelegramApiException e) {
        log.error("ArrayListStory: Ошибка отправки для chatId={}", msg.getChatId(), e);
    }
});
```

### 3. Структура обработчика (АКТУАЛЬНО):
1. **Логирование** — записываем событие
2. **Прямое создание сообщения** — через `MessageService` (без лишних слоев!)
3. **Отправка** — через `bot.execute()`
4. **Планирование** — через `SchedulerService`
5. **Генерация наград** — через `StatService`
6. **Применение статов** — прямо в обработчике

### 4. Что изменилось:
**Было:** `ArrayListStory` → `BattleActionService` → `ArrayListBattleService` → `MessageService`  
**Стало:** `ArrayListStory` → `MessageService` (прямой вызов, блять!)

**Результат:** Убрали говнокод, сделали прямые вызовы, код стал читаемее и быстрее!

---

## 🎁 ГЕНЕРАЦИЯ НАГРАД

### 1. Где генерируются награды:
**Файл:** `src/main/java/org/example/service/ArrayList/StatService.java`

### 2. Стандартные награды:
```java
public Map<String, Integer> generateStandardRewards() {
    Map<String, Integer> rewards = new HashMap<>();
    rewards.put("achievement_points", generateRandomReward(30, 60));
    rewards.put("currency", generateRandomReward(200, 400));
    return rewards;
}
```

### 3. Кастомные награды (деньги уменьшаются):
```java
public Map<String, Integer> generateCustomStatChanges() {
    Map<String, Integer> changes = new HashMap<>();
    
    // Уменьшаем деньги (отрицательное значение)
    changes.put("money", -generateRandomReward(150, 200));
    
    // Увеличиваем очки достижения
    changes.put("achievement_points", generateRandomReward(30, 50));
    
    return changes;
}
```

### 4. Индивидуальные статы персонажей:
```java
public String getIndividualStatForCharacter(String characterType) {
    switch (characterType) {
        case "Personage1":
            return "analytics"; // Аналитика для Personage1
        case "Personage2":
            return "communication"; // Коммуникация для Personage2
        case "Personage3":
            return "code_accuracy"; // Точность кода для Personage3
        default:
            return "analytics";
    }
}
```

---

## 💬 СОЗДАНИЕ СООБЩЕНИЙ

### 1. Где создаются сообщения:
**Файл:** `src/main/java/org/example/service/ArrayList/MessageService.java`

### 2. Структура сообщения:
```java
public SendMessage createInsertBeginningMessage(Long chatId) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setParseMode("Markdown"); // Важно для форматирования
    
    sendMessage.setText("🚨 Вставить в начало — ва-банк!\n" +
            "🔥 Аррейн получает урон 100(-50) — начинается частичный resize()\n\n" +
            "Ты швыряешь элемент в начало списка — как камень в стеклянную крышу офиса.\n\n" +
            "АРРЕЙОН (звереет):\n" +
            "«Ты что, совсем страх потерял?! Я ТАК не работаю! Сейчас будет больно — тебе, мне и твоему менеджеру.»\n\n" +
            "Массив трещит, но урон наносишь ты");
    
    return sendMessage;
}
```

### 3. Сообщение с результатом и статами:
```java
public SendMessage createInsertBeginningResultMessage(Long chatId, Map<String, Integer> statChanges) {
    // Получаем данные персонажа
    UserEntity user = userService.getUserByTgId(chatId);
    PersonageEntity entity = user.getPersonage();
    
    // Определяем индивидуальный стат
    String characterType = entity.getCharacterType();
    String individualStat = statService.getIndividualStatForCharacter(characterType);
    
    // Получаем изменения
    int achievementChange = statChanges.get("achievement_points");
    int moneyChange = statChanges.get("money");
    int individualChange = statChanges.get(individualStat);
    
    // Создаем сообщение
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setParseMode("Markdown");
    
    sendMessage.setText("*Итераториус:*\n\n" +
            "Смело, но немного безрассудно. Главное, чтобы прод это не увидел.\n" +
            "Иногда даже идиотизм — это стратегия.\n\n" +
            "*Навык повышен:*\n" +
            " +" + achievementChange + " ⭐️ к Очкам Достижения\n" +
            " -" + Math.abs(moneyChange) + " 💲 к Деньгам (за психотерапевта позже)\n" +
            " +" + individualChange + " " + statService.getStatDisplayInfo(individualStat));
    
    return sendMessage;
}
```

---

## ⏰ ПЛАНИРОВАНИЕ СОБЫТИЙ (АКТУАЛЬНО)

### 1. Где планируются события:
**Файл:** `src/main/java/org/example/service/ArrayList/ArrayListSchedulerService.java`

### 2. Универсальные методы планирования (АКТУАЛЬНО):
```java
// Простое планирование события
schedulerService.scheduleEvent(bot, chatId, () -> {
    try {
        SendMessage message = new SendMessage(chatId, "Привет через 5 секунд!");
        bot.execute(message);
    } catch (TelegramApiException e) {
        log.error("Ошибка отправки", e);
    }
}, 5);

// Планирование отправки готового сообщения
SendMessage warningMessage = messageService.createTryCatchDefenseMessage(chatId);
schedulerService.scheduleMessage(bot, warningMessage, 3);

// Планирование удаления сообщения
SendMessage tempMessage = new SendMessage(chatId, "Временное сообщение");
Message sentMsg = bot.execute(tempMessage);
schedulerService.scheduleMessageDeletion(bot, chatId, sentMsg.getMessageId(), 10);

// Сложные последовательности событий
ScheduledEvent[] battleSequence = {
    new ScheduledEvent(() -> {
        try {
            SendMessage warning = new SendMessage(chatId, "⚠️ Внимание! Противник приближается!");
            bot.execute(warning);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки предупреждения", e);
        }
    }, 0), // Сразу
    
    new ScheduledEvent(() -> {
        try {
            SendMessage enemyInfo = new SendMessage(chatId, "👹 Имя: Аррейн\nXP: 150\nЗвание: Призрачный Легат");
            bot.execute(enemyInfo);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки информации о враге", e);
        }
    }, 3), // Через 3 секунды
    
    new ScheduledEvent(() -> {
        try {
            SendMessage attack = new SendMessage(chatId, "💥 Аррейн атакует!");
            bot.execute(attack);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки атаки", e);
        }
    }, 6) // Через 6 секунд
};

schedulerService.createEventSequence(bot, chatId, battleSequence);
```

### 3. Специальный метод для теории (АКТУАЛЬНО):
```java
public void sendTheoryWithAutoDelete(TelegramLongPollingBot bot, Long chatId) {
    // Отправляем теорию с автоудалением через 20 секунд
    // и запуском последовательности событий
    schedulerService.sendTheoryWithAutoDelete(bot, chatId);
}
```

### 4. Что изменилось:
**Было:** Только простые методы планирования  
**Стало:** Универсальные методы для любых сценариев

**Новые возможности:**
- ✅ `scheduleEvent()` — простое планирование
- ✅ `scheduleMessage()` — планирование готовых сообщений  
- ✅ `scheduleMessageDeletion()` — планирование удаления
- ✅ `createEventSequence()` — сложные последовательности
- ✅ `ScheduledEvent` класс — для создания последовательностей

**Результат:** Теперь можно создавать любые временные сценарии, блять!

---

## 🔄 ПОЛНАЯ ЛОГИЧЕСКАЯ ЦЕПОЧКА (АКТУАЛЬНО)

### Пример: Кнопка "🚨 Отразить вставкой в начало"

#### 1. Пользователь нажимает кнопку
```java
// ArrayListStory.java — ПРЯМАЯ ОБРАБОТКА БЕЗ ЛИШНИХ СЛОЕВ!
commandsMap.put("🚨 Отразить \nвставкой \n в начало", (bot, msg) -> {
    log.info("ArrayListStory: Обработка команды 'Отразить' для chatId={}", msg.getChatId());
    
    try {
        // 1. Отправляем первое сообщение
        SendMessage firstMessage = messageService.createInsertBeginningMessage(msg.getChatId());
        bot.execute(firstMessage);
        
        // 2. Через 3 секунды отправляем результат от Итераториуса
        schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
            try {
                // Генерируем кастомные изменения статов
                Map<String, Integer> statChanges = statService.generateCustomStatChanges();
                
                // Добавляем индивидуальный стат для персонажа
                var user = userService.getUserByTgId(msg.getChatId());
                if (user != null && user.getPersonage() != null) {
                    String characterType = user.getPersonage().getCharacterType();
                    String individualStat = statService.getIndividualStatForCharacter(characterType);
                    statChanges.put(individualStat, statService.generateRandomReward(15, 25));
                }
                
                // Применяем изменения статов
                statService.applyStatChanges(msg.getChatId(), statChanges);
                
                // Отправляем результат с изменениями статов
                SendMessage resultMessage = messageService.createInsertBeginningResultMessage(msg.getChatId(), statChanges);
                bot.execute(resultMessage);
                
                // 3. Еще через 3 секунды отправляем завершение раунда
                schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
                    try {
                        SendMessage endRoundMessage = messageService.endOfRoundOne(msg.getChatId());
                        bot.execute(endRoundMessage);
                    } catch (TelegramApiException e) {
                        log.error("Ошибка отправки завершения раунда", e);
                    }
                }, 3);
                
            } catch (TelegramApiException e) {
                log.error("Ошибка отправки результата", e);
            }
        }, 3);
        
    } catch (TelegramApiException e) {
        log.error("ArrayListStory: Ошибка отправки для chatId={}", msg.getChatId(), e);
    }
});
```

#### 2. Что происходит пошагово:

**Шаг 1:** Пользователь нажимает кнопку → `ArrayListStory` получает команду

**Шаг 2:** `ArrayListStory` создает первое сообщение через `MessageService`:
```java
SendMessage firstMessage = messageService.createInsertBeginningMessage(msg.getChatId());
bot.execute(firstMessage);
```

**Шаг 3:** `ArrayListStory` планирует отложенный результат через `SchedulerService`:
```java
schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
    // Логика результата
}, 3);
```

**Шаг 4:** Через 3 секунды генерируются изменения статов через `StatService`:
```java
Map<String, Integer> statChanges = statService.generateCustomStatChanges();
// Деньги уменьшаются, другие статы растут
```

**Шаг 5:** Добавляется индивидуальный стат для персонажа:
```java
String individualStat = statService.getIndividualStatForCharacter(characterType);
statChanges.put(individualStat, statService.generateRandomReward(15, 25));
```

**Шаг 6:** Применяются изменения к персонажу:
```java
statService.applyStatChanges(msg.getChatId(), statChanges);
```

**Шаг 7:** Создается сообщение с результатом через `MessageService`:
```java
SendMessage resultMessage = messageService.createInsertBeginningResultMessage(msg.getChatId(), statChanges);
bot.execute(resultMessage);
```

**Шаг 8:** Планируется завершение раунда еще через 3 секунды:
```java
schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
    SendMessage endRoundMessage = messageService.endOfRoundOne(msg.getChatId());
    bot.execute(endRoundMessage);
}, 3);
```

#### 3. Что изменилось:
**Было:** Сложная цепочка делегирования через несколько сервисов  
**Стало:** Прямая обработка в `ArrayListStory` с координацией сервисов

**Результат:** Код стал читаемее, быстрее и понятнее, блять!

---

## 📝 ПРИМЕРЫ КОДА (АКТУАЛЬНО)

### Добавление новой кнопки:

#### 1. Создай кнопку в KeyboardReam.java:
```java
public static ReplyKeyboardMarkup NewBattleKeyboard(Long chatId) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    keyboardMarkup.setSelective(true);
    keyboardMarkup.setResizeKeyboard(true);
    keyboardMarkup.setOneTimeKeyboard(false);

    List<KeyboardRow> keyboard = new ArrayList<>();
    
    KeyboardRow row1 = new KeyboardRow();
    row1.add("⚡ Новая атака");
    
    keyboard.add(row1);
    keyboardMarkup.setKeyboard(keyboard);
    
    return keyboardMarkup;
}
```

#### 2. Добавь обработчик в ArrayListStory.java (АКТУАЛЬНО):
```java
// В методе initCommands()
commandsMap.put("⚡ Новая атака", (bot, msg) -> {
    log.info("ArrayListStory: Обработка команды 'Новая атака' для chatId={}", msg.getChatId());
    
    try {
        // 1. Отправляем сообщение об атаке
        SendMessage attackMessage = messageService.createNewAttackMessage(msg.getChatId());
        bot.execute(attackMessage);
        
        // 2. Через 4 секунды отправляем результат
        schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
            try {
                // Генерируем награды
                Map<String, Integer> rewards = Map.of(
                    "achievement_points", statService.generateRandomReward(40, 60),
                    "currency", statService.generateRandomReward(180, 250)
                );
                statService.applyStatChanges(msg.getChatId(), rewards);
                
                // Отправляем результат
                SendMessage resultMessage = messageService.createNewAttackResultMessage(msg.getChatId(), rewards);
                bot.execute(resultMessage);
                
            } catch (TelegramApiException e) {
                log.error("Ошибка отправки результата новой атаки", e);
            }
        }, 4);
        
    } catch (TelegramApiException e) {
        log.error("Ошибка отправки новой атаки", e);
    }
});
```

#### 3. Создай методы в MessageService.java:
```java
public SendMessage createNewAttackMessage(Long chatId) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setParseMode("Markdown");
    sendMessage.setText("⚡ Новая атака!\n\n" +
            "Ты используешь новую технику...\n" +
            "Аррейн получает урон!");
    
    return sendMessage;
}

public SendMessage createNewAttackResultMessage(Long chatId, Map<String, Integer> rewards) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setParseMode("Markdown");
    sendMessage.setText("*Итераториус:*\n\n" +
            "Отличная новая техника!\n" +
            "*Навык повышен:*\n" +
            " +" + rewards.get("achievement_points") + " ⭐️ к Очкам Достижения\n" +
            " +" + rewards.get("currency") + " 💲 к Деньгам");
    
    return sendMessage;
}
```





---

## 🎯 ВАЖНЫЕ МОМЕНТЫ

### 1. Логирование
- **Всегда логируй** начало и конец обработки
- **Логируй ошибки** с полным стектрейсом
- **Используй разные уровни** (INFO, DEBUG, ERROR)

### 2. Обработка ошибок
- **Обертывай в try-catch** все вызовы bot.execute()
- **Проверяй null** перед обращением к объектам
- **Используй безопасные методы** для работы со статами

### 3. Производительность
- **Не блокируй основной поток** — используй планировщик
- **Кэшируй часто используемые данные**
- **Оптимизируй запросы к БД**

### 4. Читаемость кода
- **Используй говорящие имена** методов и переменных
- **Добавляй комментарии** к сложной логике
- **Разделяй ответственность** между сервисами

---

## 🚀 ЗАКЛЮЧЕНИЕ (АКТУАЛЬНО)

### 🎉 Что получили после рефакторинга:

#### **✅ Упрощенная архитектура:**
- **Было:** 7 классов с лишними слоями абстракции
- **Стало:** 5 классов с четким разделением ответственности
- **Убрали:** 317 строк говнокода

#### **✅ Прямые вызовы:**
- **Было:** `ArrayListStory` → `BattleActionService` → `ArrayListBattleService` → `MessageService`
- **Стало:** `ArrayListStory` → `MessageService` (прямой вызов, блять!)

#### **✅ Универсальные инструменты:**
- `scheduleEvent()` — простое планирование
- `scheduleMessage()` — планирование готовых сообщений
- `scheduleMessageDeletion()` — планирование удаления
- `createEventSequence()` — сложные последовательности
- `ScheduledEvent` класс — для создания последовательностей

#### **✅ Улучшенная производительность:**
- Меньше кода = меньше багов
- Прямые вызовы = быстрее выполнение
- Оптимизированная архитектура

### 🚀 Как теперь работает ArrayList система (АКТУАЛЬНО):

1. **Кнопка создается** в `KeyboardReam.java`
2. **Обработчик регистрируется** в `ArrayListStory.java` (прямая обработка!)
3. **Сообщения создаются** в `MessageService.java`
4. **Награды генерируются** в `StatService.java`
5. **События планируются** в `ArrayListSchedulerService.java`

### 🎮 Пример быстрого добавления новой функции:
```java
// 1. Кнопка
row1.add("⚡ Новая атака");

// 2. Обработчик (прямо в ArrayListStory!)
commandsMap.put("⚡ Новая атака", (bot, msg) -> {
    SendMessage attack = messageService.createNewAttackMessage(msg.getChatId());
    bot.execute(attack);
    
    schedulerService.scheduleEvent(bot, msg.getChatId(), () -> {
        // Результат через 3 секунды
    }, 3);
});

// 3. Сообщения
public SendMessage createNewAttackMessage(Long chatId) {
    // Создание сообщения
}
```

### 🎯 Результат:
**Код стал читаемее, быстрее и понятнее!** Теперь можно легко добавлять новые функции без лишних слоев абстракции. Архитектура чистая, логика понятная, производительность отличная!

**Автор: Архитектор (который знает, что лучший код — это тот, которого нет)** 😄 