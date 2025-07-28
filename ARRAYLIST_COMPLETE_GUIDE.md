# 🎮 ПОЛНЫЙ МАНУАЛ ARRAYLIST: ОТ КНОПКИ ДО СТАТОВ

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
│  🎮 ArrayListStory (фасад)                                 │
│  ├── Маршрутизация команд                                  │
│  └── Делегирование специализированным сервисам             │
│                                                             │
│  ⚔️ BattleActionService                                   │
│  ├── Обработка боевых действий                             │
│  ├── Проверки возможности участия                          │
│  └── Получение статистики                                  │
│                                                             │
│  📊 StatService                                            │
│  ├── Генерация случайных наград                           │
│  ├── Применение изменений к персонажу                     │
│  ├── Определение индивидуальных статов                     │
│  └── Безопасная работа с null значениями                  │
│                                                             │
│  💬 MessageService                                         │
│  ├── Создание боевых сообщений                            │
│  ├── Создание сообщений с результатами                     │
│  ├── Создание сообщений с наградами                        │
│  └── Единообразное форматирование                          │
│                                                             │
│  ⏰ ArrayListSchedulerService                              │
│  ├── Планирование отложенных событий                       │
│  ├── Автоматическое удаление сообщений                     │
│  └── Последовательное выполнение действий                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

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

## 🎯 ОБРАБОТКА НАЖАТИЯ

### 1. Где обрабатываются команды:
**Файл:** `src/main/java/org/example/service/ArrayList/ArrayListStory.java`

### 2. Регистрация обработчика:
```java
// В конструкторе или методе инициализации
commandsMap.put("🚨 Отразить \nвставкой \n в начало", (bot, msg) -> {
    log.info("ArrayListStory: Обработка команды 'Отразить' для chatId={}", msg.getChatId());
    
    try {
        // Отправляем первое сообщение
        SendMessage firstMessage = battleService.createInsertBeginningMessage(msg.getChatId());
        bot.execute(firstMessage);
        
        // Через 3 секунды отправляем результат
        schedulerService.sendInsertBeginningResult(bot, msg.getChatId());
    } catch (TelegramApiException e) {
        log.error("ArrayListStory: Ошибка отправки для chatId={}", msg.getChatId(), e);
    }
});
```

### 3. Структура обработчика:
1. **Логирование** — записываем событие
2. **Создание сообщения** — через MessageService
3. **Отправка** — через bot.execute()
4. **Планирование** — через SchedulerService

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

## ⏰ ПЛАНИРОВАНИЕ СОБЫТИЙ

### 1. Где планируются события:
**Файл:** `src/main/java/org/example/service/ArrayList/ArrayListSchedulerService.java`

### 2. Отложенная отправка результата:
```java
public void sendInsertBeginningResult(TelegramLongPollingBot bot, Long chatId) {
    scheduler.schedule(() -> {
        log.info("ArrayListSchedulerService: Отправка результата 'Вставить в начало' для chatId={}", chatId);
        
        try {
            // Создаем сообщение с результатом и изменениями статов
            SendMessage resultMessage = battleService.BattleResultInsertBeginning(chatId, 0, 0);
            bot.execute(resultMessage);
            log.info("ArrayListSchedulerService: Результат отправлен для chatId={}", chatId);
        } catch (TelegramApiException e) {
            log.error("ArrayListSchedulerService: Ошибка отправки для chatId={}", chatId, e);
        }
    }, 3, TimeUnit.SECONDS); // Задержка 3 секунды
}
```

### 3. Автоматическое удаление сообщений:
```java
public void sendTheoryWithAutoDelete(TelegramLongPollingBot bot, Long chatId) {
    // Отправляем теорию
    SendMessage theoryMessage = new SendMessage(chatId.toString(), theory);
    Message sentMsg = bot.execute(theoryMessage);
    Integer messageId = sentMsg.getMessageId();
    
    // Планируем удаление через 20 секунд
    scheduler.schedule(() -> {
        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId.toString());
            deleteMessage.setMessageId(messageId);
            bot.execute(deleteMessage);
            
            // После удаления запускаем последовательность событий
            startEventSequence(bot, chatId);
        } catch (Exception e) {
            log.error("Ошибка при удалении сообщения", e);
        }
    }, 20, TimeUnit.SECONDS);
}
```

---

## 🔄 ПОЛНАЯ ЛОГИЧЕСКАЯ ЦЕПОЧКА

### Пример: Кнопка "🚨 Отразить вставкой в начало"

#### 1. Пользователь нажимает кнопку
```java
// ArrayListStory.java
commandsMap.put("🚨 Отразить \nвставкой \n в начало", (bot, msg) -> {
    // Обработка нажатия
});
```

#### 2. Создается первое сообщение
```java
// MessageService.java
SendMessage firstMessage = messageService.createInsertBeginningMessage(chatId);
bot.execute(firstMessage);
```

#### 3. Планируется отложенный результат
```java
// ArrayListSchedulerService.java
schedulerService.sendInsertBeginningResult(bot, chatId);
```

#### 4. Генерируются изменения статов
```java
// StatService.java
Map<String, Integer> statChanges = statService.generateCustomStatChanges();
// Добавляем индивидуальный стат
String individualStat = statService.getIndividualStatForCharacter(characterType);
statChanges.put(individualStat, statService.generateRandomReward(15, 25));
```

#### 5. Применяются изменения к персонажу
```java
// StatService.java
statService.applyStatChanges(chatId, statChanges);
```

#### 6. Создается сообщение с результатом
```java
// MessageService.java
SendMessage resultMessage = messageService.createInsertBeginningResultMessage(chatId, statChanges);
```

#### 7. Отправляется результат через 3 секунды
```java
// ArrayListSchedulerService.java
bot.execute(resultMessage);
```

---

## 📝 ПРИМЕРЫ КОДА

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

#### 2. Добавь обработчик в ArrayListStory.java:
```java
commandsMap.put("⚡ Новая атака", (bot, msg) -> {
    log.info("ArrayListStory: Обработка новой атаки для chatId={}", msg.getChatId());
    
    try {
        // Отправляем сообщение об атаке
        SendMessage attackMessage = battleService.createNewAttackMessage(msg.getChatId());
        bot.execute(attackMessage);
        
        // Планируем результат
        schedulerService.sendNewAttackResult(bot, msg.getChatId());
    } catch (TelegramApiException e) {
        log.error("ArrayListStory: Ошибка отправки для chatId={}", msg.getChatId(), e);
    }
});
```

#### 3. Создай метод в MessageService.java:
```java
public SendMessage createNewAttackMessage(Long chatId) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setParseMode("Markdown");
    sendMessage.setText("⚡ Твоя новая атака!\n\n" +
            "Описание атаки здесь...");
    
    return sendMessage;
}
```

#### 4. Создай метод в BattleActionService.java:
```java
public void processNewAttackAction(TelegramLongPollingBot bot, Long chatId) {
    log.info("BattleActionService: Обработка новой атаки для chatId={}", chatId);
    
    try {
        SendMessage attackMessage = messageService.createNewAttackMessage(chatId);
        bot.execute(attackMessage);
        
        // Генерируем и применяем награды
        Map<String, Integer> rewards = statService.generateStandardRewards();
        statService.applyStatChanges(chatId, rewards);
        
    } catch (TelegramApiException e) {
        log.error("BattleActionService: Ошибка отправки для chatId={}", chatId, e);
    }
}
```

#### 5. Создай метод в ArrayListSchedulerService.java:
```java
public void sendNewAttackResult(TelegramLongPollingBot bot, Long chatId) {
    scheduler.schedule(() -> {
        log.info("ArrayListSchedulerService: Отправка результата новой атаки для chatId={}", chatId);
        
        try {
            // Генерируем награды
            Map<String, Integer> rewards = statService.generateStandardRewards();
            statService.applyStatChanges(chatId, rewards);
            
            // Создаем и отправляем результат
            SendMessage resultMessage = messageService.createNewAttackResultMessage(chatId, rewards);
            bot.execute(resultMessage);
            
        } catch (TelegramApiException e) {
            log.error("ArrayListSchedulerService: Ошибка отправки для chatId={}", chatId, e);
        }
    }, 2, TimeUnit.SECONDS);
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

## 🚀 ЗАКЛЮЧЕНИЕ

Теперь у тебя есть полная картина того, как работает ArrayList система:

1. **Кнопка создается** в KeyboardReam.java
2. **Обработчик регистрируется** в ArrayListStory.java
3. **Действие обрабатывается** в BattleActionService.java
4. **Сообщения создаются** в MessageService.java
5. **Награды генерируются** в StatService.java
6. **События планируются** в ArrayListSchedulerService.java

**Автор: Архитектор (который знает, что хороший код — это как хороший анекдот: короткий, понятный и с изюминкой)** 😄 