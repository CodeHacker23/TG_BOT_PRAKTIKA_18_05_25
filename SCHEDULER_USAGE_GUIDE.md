# 📋 **Руководство по использованию ArrayListSchedulerService**

## 🎯 **Обзор**

`ArrayListSchedulerService` — это универсальный сервис для планирования задач в Telegram боте. Он предоставляет мощные инструменты для создания сложных временных сценариев.

## 🛠️ **Доступные методы**

### **1. `scheduleEvent()` — Простое планирование**

**Описание:** Планирует выполнение одного события через заданное время.

**Сигнатура:**
```java
public void scheduleEvent(TelegramLongPollingBot bot, Long chatId, Runnable event, int delaySeconds)
```

**Пример использования:**
```java
@Autowired
private ArrayListSchedulerService schedulerService;

// Планируем отправку сообщения через 5 секунд
schedulerService.scheduleEvent(bot, chatId, () -> {
    try {
        SendMessage message = new SendMessage(chatId, "Привет через 5 секунд!");
        bot.execute(message);
    } catch (TelegramApiException e) {
        log.error("Ошибка отправки", e);
    }
}, 5);
```

**Когда использовать:**
- Отправка одного сообщения с задержкой
- Выполнение простого действия через время
- Создание эффекта "ожидания"

---

### **2. `scheduleMessage()` — Планирование отправки сообщения**

**Описание:** Планирует отправку готового сообщения через заданное время.

**Сигнатура:**
```java
public void scheduleMessage(TelegramLongPollingBot bot, SendMessage message, int delaySeconds)
```

**Пример использования:**
```java
@Autowired
private MessageService messageService;

// Создаем сообщение
SendMessage warningMessage = messageService.createTryCatchDefenseMessage(chatId);

// Планируем его отправку через 3 секунды
schedulerService.scheduleMessage(bot, warningMessage, 3);
```

**Когда использовать:**
- Отправка готовых сообщений с задержкой
- Когда сообщение уже создано другим сервисом
- Простая планировка без дополнительной логики

---

### **3. `scheduleMessageDeletion()` — Планирование удаления сообщения**

**Описание:** Планирует автоматическое удаление сообщения через заданное время.

**Сигнатура:**
```java
public void scheduleMessageDeletion(TelegramLongPollingBot bot, Long chatId, Integer messageId, int delaySeconds)
```

**Пример использования:**
```java
// Отправляем временное сообщение
SendMessage tempMessage = new SendMessage(chatId, "Это сообщение исчезнет через 10 секунд");
Message sentMsg = bot.execute(tempMessage);

// Планируем его удаление через 10 секунд
schedulerService.scheduleMessageDeletion(bot, chatId, sentMsg.getMessageId(), 10);
```

**Когда использовать:**
- Создание эффекта "самоуничтожающихся" сообщений
- Очистка чата от временной информации
- Создание интерактивных эффектов

---

### **4. `createEventSequence()` — Сложные последовательности**

**Описание:** Создает сложную последовательность событий с разными интервалами.

**Сигнатура:**
```java
public void createEventSequence(TelegramLongPollingBot bot, Long chatId, ScheduledEvent... events)
```

**Пример использования:**
```java
// Создаем последовательность для боевой сцены
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

// Запускаем последовательность
schedulerService.createEventSequence(bot, chatId, battleSequence);
```

**Когда использовать:**
- Создание сложных сценариев с множественными событиями
- Боевые последовательности
- Интерактивные истории
- Создание атмосферы "живого" бота

---

### **5. `sendTheoryWithAutoDelete()` — Специальный метод для теории**

**Описание:** Отправляет теорию с автоматическим удалением и запуском последовательности событий.

**Сигнатура:**
```java
public void sendTheoryWithAutoDelete(TelegramLongPollingBot bot, Long chatId)
```

**Пример использования:**
```java
// Отправляем теорию, которая удалится через 20 секунд
// и запустит последовательность событий
schedulerService.sendTheoryWithAutoDelete(bot, chatId);
```

**Когда использовать:**
- Создание эффекта "свиток самоуничтожится"
- Запуск игровых сценариев после теории
- Создание интерактивного обучения

---

## 🎮 **Практические примеры**

### **Пример 1: Создание боевой сцены**

```java
@Autowired
private ArrayListSchedulerService schedulerService;
@Autowired
private MessageService messageService;

public void createBattleScene(TelegramLongPollingBot bot, Long chatId) {
    // Создаем последовательность боевых событий
    ScheduledEvent[] battleEvents = {
        new ScheduledEvent(() -> {
            try {
                SendMessage warning = new SendMessage(chatId, "⚔️ Бой начинается!");
                bot.execute(warning);
            } catch (TelegramApiException e) {
                log.error("Ошибка", e);
            }
        }, 0),
        
        new ScheduledEvent(() -> {
            try {
                SendMessage enemy = new SendMessage(chatId, "👹 Противник появляется!");
                bot.execute(enemy);
            } catch (TelegramApiException e) {
                log.error("Ошибка", e);
            }
        }, 2),
        
        new ScheduledEvent(() -> {
            try {
                SendMessage attack = new SendMessage(chatId, "💥 Атака!");
                bot.execute(attack);
            } catch (TelegramApiException e) {
                log.error("Ошибка", e);
            }
        }, 4)
    };
    
    schedulerService.createEventSequence(bot, chatId, battleEvents);
}
```

### **Пример 2: Создание временного сообщения**

```java
public void createTemporaryMessage(TelegramLongPollingBot bot, Long chatId) {
    try {
        // Отправляем временное сообщение
        SendMessage tempMsg = new SendMessage(chatId, "⏰ Это сообщение исчезнет через 5 секунд");
        Message sentMsg = bot.execute(tempMsg);
        
        // Планируем его удаление
        schedulerService.scheduleMessageDeletion(bot, chatId, sentMsg.getMessageId(), 5);
        
    } catch (TelegramApiException e) {
        log.error("Ошибка отправки временного сообщения", e);
    }
}
```

### **Пример 3: Создание интерактивного обучения**

```java
public void createInteractiveLesson(TelegramLongPollingBot bot, Long chatId) {
    ScheduledEvent[] lessonEvents = {
        new ScheduledEvent(() -> {
            try {
                SendMessage intro = new SendMessage(chatId, "📚 Начинаем урок по ArrayList");
                bot.execute(intro);
            } catch (TelegramApiException e) {
                log.error("Ошибка", e);
            }
        }, 0),
        
        new ScheduledEvent(() -> {
            try {
                SendMessage theory = new SendMessage(chatId, "📖 ArrayList — это динамический массив");
                Message sentMsg = bot.execute(theory);
                // Планируем удаление теории через 15 секунд
                schedulerService.scheduleMessageDeletion(bot, chatId, sentMsg.getMessageId(), 15);
            } catch (TelegramApiException e) {
                log.error("Ошибка", e);
            }
        }, 2),
        
        new ScheduledEvent(() -> {
            try {
                SendMessage practice = new SendMessage(chatId, "💻 Теперь попробуйте сами!");
                bot.execute(practice);
            } catch (TelegramApiException e) {
                log.error("Ошибка", e);
            }
        }, 17)
    };
    
    schedulerService.createEventSequence(bot, chatId, lessonEvents);
}
```

## ⚠️ **Важные замечания**

### **1. Обработка ошибок**
Всегда оборачивайте код в try-catch блоки:
```java
new ScheduledEvent(() -> {
    try {
        // Ваш код здесь
        SendMessage message = new SendMessage(chatId, "Текст");
        bot.execute(message);
    } catch (TelegramApiException e) {
        log.error("Ошибка отправки сообщения", e);
    }
}, delaySeconds)
```

### **2. Логирование**
Используйте логирование для отладки:
```java
log.debug("Планируем событие через {} секунд", delaySeconds);
```

### **3. Производительность**
- Не создавайте слишком много одновременных задач
- Используйте разумные интервалы времени
- Очищайте ресурсы при необходимости

### **4. Потокобезопасность**
- `ScheduledExecutorService` потокобезопасен
- Не изменяйте общие данные без синхронизации
- Используйте локальные переменные в лямбдах

## 🎯 **Лучшие практики**

### **1. Структурирование кода**
```java
// Хорошо: Разделение на методы
private void sendWarning(TelegramLongPollingBot bot, Long chatId) {
    try {
        SendMessage warning = new SendMessage(chatId, "⚠️ Внимание!");
        bot.execute(warning);
    } catch (TelegramApiException e) {
        log.error("Ошибка отправки предупреждения", e);
    }
}

// Использование
ScheduledEvent[] events = {
    new ScheduledEvent(() -> sendWarning(bot, chatId), 0),
    new ScheduledEvent(() -> sendAttack(bot, chatId), 3)
};
```

### **2. Константы для времени**
```java
private static final int WARNING_DELAY = 0;
private static final int ENEMY_DELAY = 3;
private static final int ATTACK_DELAY = 6;

ScheduledEvent[] events = {
    new ScheduledEvent(() -> sendWarning(bot, chatId), WARNING_DELAY),
    new ScheduledEvent(() -> sendEnemy(bot, chatId), ENEMY_DELAY),
    new ScheduledEvent(() -> sendAttack(bot, chatId), ATTACK_DELAY)
};
```

### **3. Валидация параметров**
```java
public void scheduleEvent(TelegramLongPollingBot bot, Long chatId, Runnable event, int delaySeconds) {
    if (bot == null || chatId == null || event == null) {
        log.error("Некорректные параметры для планирования события");
        return;
    }
    
    if (delaySeconds < 0) {
        log.warn("Отрицательная задержка, используем 0");
        delaySeconds = 0;
    }
    
    // Планирование события
    scheduler.schedule(event, delaySeconds, TimeUnit.SECONDS);
}
```

## 🚀 **Заключение**

`ArrayListSchedulerService` предоставляет мощные инструменты для создания интерактивных сценариев в Telegram боте. Используйте эти методы для:

- ✅ Создания динамических историй
- ✅ Реализации игровых механик
- ✅ Создания обучающих сценариев
- ✅ Добавления интерактивности
- ✅ Управления временными эффектами

**Автор: Архитектор (который знает, что время — это не просто переменная)** 😄 