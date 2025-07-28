# 🎯 РУКОВОДСТВО ПО КАСТОМНЫМ ИЗМЕНЕНИЯМ СТАТОВ

---

## 📋 ОБЗОР

Создана универсальная система для изменения статов персонажа с возможностью:
- **Уменьшения денег** (отрицательные значения)
- **Увеличения других статов** (положительные значения)
- **Гибкой настройки** любых комбинаций изменений

---

## 🔧 ДОБАВЛЕННЫЕ МЕТОДЫ

### 1. **`generateCustomStatChanges()`**
```java
/**
 * Генерирует изменения статов для персонажа.
 * 
 * @return Map<String, Integer> — карта изменений статов
 * 
 * Пример возвращаемого значения:
 * {
 *   "money": -95,        // Уменьшаем деньги на 95
 *   "analytics": +18,    // Увеличиваем аналитику на 18
 *   "optimization": +12, // Увеличиваем оптимизацию на 12
 *   "code_accuracy": +8  // Увеличиваем точность кода на 8
 * }
 */
public Map<String, Integer> generateCustomStatChanges()
```

**Генерируемые изменения:**
- 💲 **Деньги**: -80 до -120 (уменьшение)
- 📊 **Аналитика**: +15 до +25 (увеличение)
- ⚙️ **Оптимизация**: +10 до +20 (увеличение)
- 🎯 **Точность кода**: +8 до +15 (увеличение)
- 💬 **Коммуникация**: +5 до +12 (увеличение)
- 😄 **Юмор**: +3 до +8 (увеличение)

### 2. **`processCustomRewards(Long chatId, Map<String, Integer> statChanges)`**
```java
/**
 * Обрабатывает кастомные изменения статов персонажа.
 * 
 * @param chatId — ID чата пользователя
 * @param statChanges — Map с изменениями статов
 */
public void processCustomRewards(Long chatId, Map<String, Integer> statChanges)
```

**Поддерживаемые статы:**
- `money` — Деньги
- `analytics` — Аналитика
- `optimization` — Оптимизация
- `code_accuracy` — Точность кода
- `communication` — Коммуникация
- `humor` — Юмор
- `achievement_points` — Очки Достижения

### 3. **`createCustomResultMessage(Long chatId, Map<String, Integer> statChanges)`**
```java
/**
 * Создает сообщение с кастомными изменениями статов от Итераториуса.
 * 
 * @param chatId — ID чата пользователя
 * @param statChanges — Map с изменениями статов
 * @return SendMessage — сообщение с результатом изменений
 */
public SendMessage createCustomResultMessage(Long chatId, Map<String, Integer> statChanges)
```

---

## 🎮 ПРИМЕРЫ ИСПОЛЬЗОВАНИЯ

### **Пример 1: Простое использование**
```java
// В ArrayListSchedulerService.answerIteratoriys()
public void answerIteratoriys(TelegramLongPollingBot bot, Long chatId) {
    scheduler.schedule(() -> {
        // Генерируем изменения статов
        Map<String, Integer> statChanges = battleService.generateCustomStatChanges();
        
        // Обрабатываем изменения в базе данных
        battleService.processCustomRewards(chatId, statChanges);
        
        // Создаем сообщение с результатом
        SendMessage sendMessage = battleService.createCustomResultMessage(chatId, statChanges);
        
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }, 3, TimeUnit.SECONDS);
}
```

### **Пример 2: Кастомные изменения**
```java
// Создаем свои изменения
Map<String, Integer> customChanges = new HashMap<>();
customChanges.put("money", -150);           // Уменьшаем деньги на 150
customChanges.put("analytics", 25);         // Увеличиваем аналитику на 25
customChanges.put("optimization", 20);      // Увеличиваем оптимизацию на 20
customChanges.put("code_accuracy", 15);     // Увеличиваем точность кода на 15

// Применяем изменения
battleService.processCustomRewards(chatId, customChanges);

// Создаем сообщение
SendMessage message = battleService.createCustomResultMessage(chatId, customChanges);
```

### **Пример 3: Тематические изменения**
```java
// Для кнопки "Купить книгу"
Map<String, Integer> bookChanges = new HashMap<>();
bookChanges.put("money", -100);
bookChanges.put("analytics", 30);
bookChanges.put("code_accuracy", 20);

// Для кнопки "Пойти на курсы"
Map<String, Integer> courseChanges = new HashMap<>();
courseChanges.put("money", -200);
courseChanges.put("optimization", 35);
courseChanges.put("communication", 25);
```

---

## 📊 ПРИМЕР СООБЩЕНИЯ

```
*Итераториус:*

Интересный выбор! Инвестиции в себя — лучшие инвестиции.

*Изменения статов:*
  -95 💲 Деньги
  +18 📊 Аналитика
  +12 ⚙️ Оптимизация
  +8 🎯 Точность кода
  +5 💬 Коммуникация
  +3 😄 Юмор
```

---

## 🚀 ПРЕИМУЩЕСТВА

### ✅ **Гибкость**
- Легко настраивать любые комбинации статов
- Поддержка положительных и отрицательных изменений
- Универсальность для разных кнопок

### ✅ **Читаемость**
- Понятные названия статов
- Эмодзи для визуального восприятия
- Структурированные сообщения

### ✅ **Переиспользование**
- Один метод для всех типов изменений
- Легко добавлять новые статы
- Консистентность в коде

### ✅ **Тестируемость**
- Легко тестировать разные сценарии
- Изолированная логика
- Подробное логирование

---

## 🔧 НАСТРОЙКА

### **Изменение диапазонов в `generateCustomStatChanges()`:**
```java
// Уменьшаем деньги на 100-200
changes.put("money", -generateRandomReward(100, 200));

// Увеличиваем аналитику на 20-40
changes.put("analytics", generateRandomReward(20, 40));
```

### **Добавление новых статов:**
1. Добавить в `generateCustomStatChanges()`
2. Добавить в `processCustomRewards()` (switch case)
3. Добавить в `getStatDisplayName()` и `getStatEmoji()`

---

## 🎯 ГОТОВО К ИСПОЛЬЗОВАНИЮ!

Система готова для создания кнопок, которые:
- Уменьшают деньги
- Увеличивают индивидуальные статы
- Показывают красивые сообщения с результатами

**Просто используй `generateCustomStatChanges()` и `processCustomRewards()`!** 🚀 