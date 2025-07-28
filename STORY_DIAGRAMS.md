# 📊 ДИАГРАММЫ И СХЕМЫ ПРОЕКТА

---

## 📋 СОДЕРЖАНИЕ

1. [Общая архитектура](#общая-архитектура)
2. [Сюжетная линия](#сюжетная-линия)
3. [Поток данных](#поток-данных)
4. [Система персонажей](#система-персонажей)
5. [Боевая система](#боевая-система)
6. [База данных](#база-данных)
7. [Компоненты системы](#компоненты-системы)

---

## 🏗️ ОБЩАЯ АРХИТЕКТУРА

### Архитектура после рефакторинга:

```mermaid
graph TB
    A[Telegram Bot] --> B[Bot.java]
    B --> C[MessageHandlerService]
    C --> D{Тип команды}
    
    D -->|/start| E[StoryStartService]
    D -->|ArrayList команды| F[ArrayListStory]
    D -->|Другие команды| G[Service.java]
    
    E --> H[PersonageCreationService]
    E --> I[PersonageService]
    
    F --> J[BattleActionService]
    F --> K[MessageService]
    F --> L[StatService]
    F --> M[ArrayListSchedulerService]
    F --> N[ArrayListTheoryService]
    
    J --> O[UserService]
    K --> O
    L --> O
    M --> O
    
    O --> P[(PostgreSQL)]
    
    style A fill:#ff9999
    style B fill:#99ccff
    style C fill:#99ccff
    style E fill:#99ff99
    style F fill:#ffcc99
    style J fill:#ff99cc
    style K fill:#cc99ff
    style L fill:#99ffcc
    style M fill:#ffcc99
    style N fill:#ccff99
    style P fill:#cccccc
```

---

## 🎭 СЮЖЕТНАЯ ЛИНИЯ

### Полный поток сюжета:

```mermaid
flowchart TD
    A[Пользователь отправляет /start] --> B[БайтФордж приветствует]
    B --> C[Выбор типа персонажа]
    C --> D[Ввод имени персонажа]
    D --> E[Создание карточки персонажа]
    E --> F[БайтФордж объясняет симуляцию]
    F --> G[Выбор: Войти во врата Рима]
    G --> H[Итераториус встречает]
    H --> I[Теория ArrayList]
    I --> J[Кнопка: 📜 Получить боевой свиток]
    J --> K[Теория удаляется через 20 сек]
    K --> L[Предупреждение о противнике]
    L --> M[Фото Аррейна]
    M --> N[Боевая клавиатура]
    N --> O{Выбор действия}
    
    O -->|🛡 Блокировать| P[Try-catch защита]
    O -->|🔍 Анализировать| Q[Анализ противника]
    O -->|🚨 Вставить в начало| R[Атака с риском]
    
    P --> S[Результат защиты]
    Q --> T[Результат анализа]
    R --> U[Результат атаки]
    
    S --> V[Награды и развитие]
    T --> V
    U --> V
    
    V --> W[Обновленная карточка персонажа]
    W --> X[Продолжение сюжета]
    
    style A fill:#ff9999
    style B fill:#99ccff
    style C fill:#99ff99
    style D fill:#99ff99
    style E fill:#99ff99
    style F fill:#99ccff
    style G fill:#ffcc99
    style H fill:#ffcc99
    style I fill:#ffcc99
    style J fill:#ffcc99
    style K fill:#ffcc99
    style L fill:#ffcc99
    style M fill:#ffcc99
    style N fill:#ffcc99
    style O fill:#ff9999
    style P fill:#ff99cc
    style Q fill:#ff99cc
    style R fill:#ff99cc
    style S fill:#cc99ff
    style T fill:#cc99ff
    style U fill:#cc99ff
    style V fill:#99ffcc
    style W fill:#99ffcc
    style X fill:#cccccc
```

---

## 📊 ПОТОК ДАННЫХ

### Обработка сообщений:

```mermaid
sequenceDiagram
    participant U as Пользователь
    participant T as Telegram
    participant B as Bot.java
    participant M as MessageHandlerService
    participant S as StoryStartService
    participant A as ArrayListStory
    participant BA as BattleActionService
    participant MS as MessageService
    participant SS as StatService
    participant DB as PostgreSQL

    U->>T: Отправляет сообщение
    T->>B: Update
    B->>M: handleMessage()
    M->>S: Обработка /start
    S->>DB: Создание персонажа
    DB-->>S: Подтверждение
    S->>T: Ответ пользователю
    T-->>U: Сообщение

    U->>T: Нажимает кнопку ArrayList
    T->>B: Update
    B->>M: handleMessage()
    M->>A: Обработка ArrayList команды
    A->>BA: processAction()
    BA->>MS: createMessage()
    BA->>SS: generateRewards()
    SS->>DB: Обновление статов
    DB-->>SS: Подтверждение
    BA->>T: Отправка результата
    T-->>U: Сообщение с наградами
```

---

## 👥 СИСТЕМА ПЕРСОНАЖЕЙ

### Иерархия персонажей:

```mermaid
classDiagram
    class PersonageBase {
        <<abstract>>
        +String name
        +Integer level
        +Integer energy
        +Integer achievementPoints
        +Double currency
        +String characterType
        +getRomanArmorCard() String
        +getCharacterDialogue() String
    }
    
    class Personage1 {
        +String name
        +Integer analytics
        +Integer deadlineResistance
        +getRomanArmorCard() String
        +getCharacterDialogue() String
    }
    
    class Personage2 {
        +String name
        +Integer codeAccuracy
        +Integer communication
        +getRomanArmorCard() String
        +getCharacterDialogue() String
    }
    
    class Personage3 {
        +String name
        +Integer codeAccuracy
        +Integer optimization
        +getRomanArmorCard() String
        +getCharacterDialogue() String
    }
    
    PersonageBase <|-- Personage1
    PersonageBase <|-- Personage2
    PersonageBase <|-- Personage3
```

### Статы персонажей:

```mermaid
graph LR
    A[Персонаж] --> B[Базовые статы]
    A --> C[Индивидуальные статы]
    
    B --> D[🏆 Level]
    B --> E[⚡ Энергия]
    B --> F[⭐ Очки достижения]
    B --> G[💲 Деньги]
    
    C --> H[Personage1: 📊 Аналитика]
    C --> I[Personage1: ⌚ Сопротивление дедлайну]
    C --> J[Personage2: 🔍 Точность кода]
    C --> K[Personage2: 💬 Коммуникация]
    C --> L[Personage3: 🔍 Точность кода]
    C --> M[Personage3: ⚙️ Оптимизация]
    
    style A fill:#ff9999
    style B fill:#99ccff
    style C fill:#99ff99
```

---

## ⚔️ БОЕВАЯ СИСТЕМА

### Боевые действия:

```mermaid
graph TD
    A[Боевая клавиатура] --> B{Выбор действия}
    
    B -->|🛡 Блокировать| C[Try-catch защита]
    B -->|🔍 Анализировать| D[Анализ противника]
    B -->|🚨 Вставить в начало| E[Атака с риском]
    
    C --> F[Генерация наград]
    D --> F
    E --> F
    
    F --> G[Стандартные награды]
    F --> H[Кастомные награды]
    F --> I[Индивидуальные статы]
    
    G --> J[⭐ Очки достижения]
    G --> K[💲 Деньги]
    
    H --> L[💲 Деньги уменьшаются]
    H --> M[⭐ Очки достижения растут]
    
    I --> N[Personage1: 📊 Аналитика]
    I --> O[Personage2: 💬 Коммуникация]
    I --> P[Personage3: ⚙️ Оптимизация]
    
    J --> Q[Применение к персонажу]
    K --> Q
    L --> Q
    M --> Q
    N --> Q
    O --> Q
    P --> Q
    
    Q --> R[Обновленная карточка]
    R --> S[Сообщение с результатом]
    
    style A fill:#ff9999
    style B fill:#ffcc99
    style C fill:#ff99cc
    style D fill:#ff99cc
    style E fill:#ff99cc
    style F fill:#cc99ff
    style G fill:#99ffcc
    style H fill:#99ffcc
    style I fill:#99ffcc
    style Q fill:#ccff99
    style R fill:#ccff99
    style S fill:#ccff99
```

### Система наград:

```mermaid
graph LR
    A[Действие игрока] --> B{Тип награды}
    
    B -->|Стандартная| C[Опыт + Деньги]
    B -->|Кастомная| D[Деньги - Опыт +]
    B -->|Индивидуальная| E[Спец. стат +]
    
    C --> F[generateStandardRewards()]
    D --> G[generateCustomStatChanges()]
    E --> H[getIndividualStatForCharacter()]
    
    F --> I[applyStatChanges()]
    G --> I
    H --> I
    
    I --> J[Обновление в БД]
    J --> K[Отображение результата]
    
    style A fill:#ff9999
    style B fill:#ffcc99
    style C fill:#99ffcc
    style D fill:#99ffcc
    style E fill:#99ffcc
    style F fill:#cc99ff
    style G fill:#cc99ff
    style H fill:#cc99ff
    style I fill:#ffcc99
    style J fill:#99ccff
    style K fill:#ccff99
```

---

## 🗄️ БАЗА ДАННЫХ

### ER-диаграмма:

```mermaid
erDiagram
    USERS {
        bigint id PK
        bigint tg_id UK
        varchar username
        varchar current_story
        boolean passed_array_list
        varchar state
    }
    
    PERSONAGES {
        bigint id PK
        bigint user_id FK
        varchar name
        integer level
        integer energy
        integer achievement_points
        double currency
        varchar character_type
        integer analytics
        integer optimization
        integer code_accuracy
        integer communication
        integer humor
        integer deadline_resistance
        varchar status
        bigint tg_id
    }
    
    USERS ||--o{ PERSONAGES : has
```

### Схема таблиц:

```mermaid
graph TB
    subgraph "Таблица USERS"
        A1[id - bigint PK]
        A2[tg_id - bigint UK]
        A3[username - varchar]
        A4[current_story - varchar]
        A5[passed_array_list - boolean]
        A6[state - varchar]
    end
    
    subgraph "Таблица PERSONAGES"
        B1[id - bigint PK]
        B2[user_id - bigint FK]
        B3[name - varchar]
        B4[level - integer]
        B5[energy - integer]
        B6[achievement_points - integer]
        B7[currency - double]
        B8[character_type - varchar]
        B9[analytics - integer]
        B10[optimization - integer]
        B11[code_accuracy - integer]
        B12[communication - integer]
        B13[humor - integer]
        B14[deadline_resistance - integer]
        B15[status - varchar]
        B16[tg_id - bigint]
    end
    
    A1 -.->|1:N| B2
    
    style A1 fill:#ff9999
    style A2 fill:#ff9999
    style B1 fill:#99ccff
    style B2 fill:#99ccff
```

---

## 🔧 КОМПОНЕНТЫ СИСТЕМЫ

### Архитектура сервисов:

```mermaid
graph TB
    subgraph "Основные сервисы"
        A[StoryStartService]
        B[ArrayListStory]
        C[UserService]
        D[PersonageService]
    end
    
    subgraph "ArrayList сервисы"
        E[BattleActionService]
        F[MessageService]
        G[StatService]
        H[ArrayListSchedulerService]
        I[ArrayListTheoryService]
    end
    
    subgraph "Вспомогательные сервисы"
        J[PersonageCreationService]
        K[PhotoService]
        L[KeyboardService]
    end
    
    A --> C
    A --> D
    A --> J
    
    B --> E
    B --> F
    B --> G
    B --> H
    B --> I
    
    E --> C
    F --> C
    G --> C
    
    H --> F
    H --> G
    
    style A fill:#ff9999
    style B fill:#ff9999
    style C fill:#99ccff
    style D fill:#99ccff
    style E fill:#99ff99
    style F fill:#99ff99
    style G fill:#99ff99
    style H fill:#99ff99
    style I fill:#99ff99
    style J fill:#ffcc99
    style K fill:#ffcc99
    style L fill:#ffcc99
```

### Поток обработки команд:

```mermaid
flowchart LR
    A[Telegram Update] --> B[Bot.java]
    B --> C[MessageHandlerService]
    C --> D{Тип команды}
    
    D -->|/start| E[StoryStartService]
    D -->|ArrayList| F[ArrayListStory]
    D -->|Другие| G[Service.java]
    
    E --> H[Создание персонажа]
    E --> I[Диалоги БайтФорджа]
    
    F --> J[Маршрутизация команд]
    J --> K[BattleActionService]
    J --> L[MessageService]
    J --> M[StatService]
    J --> N[ArrayListSchedulerService]
    
    K --> O[Обработка действий]
    L --> P[Создание сообщений]
    M --> Q[Генерация наград]
    N --> R[Планирование событий]
    
    O --> S[Обновление БД]
    P --> T[Отправка сообщений]
    Q --> S
    R --> T
    
    style A fill:#ff9999
    style B fill:#99ccff
    style C fill:#99ccff
    style E fill:#99ff99
    style F fill:#ffcc99
    style G fill:#cc99ff
    style S fill:#99ccff
    style T fill:#99ccff
```

---

## 🚀 ЗАКЛЮЧЕНИЕ

### Ключевые особенности архитектуры:

1. **Модульность** — каждый сервис отвечает за свою область
2. **Масштабируемость** — легко добавлять новые функции
3. **Читаемость** — понятная структура и разделение ответственности
4. **Тестируемость** — изолированные компоненты
5. **Производительность** — эффективная обработка команд

### Преимущества после рефакторинга:

- ✅ **Чистая архитектура** — разделение ответственности
- ✅ **Читаемый код** — понятная структура
- ✅ **Поддерживаемость** — легко добавлять новые функции
- ✅ **Тестируемость** — каждый компонент изолирован
- ✅ **Масштабируемость** — легко расширять функциональность

**Автор: Архитектор (который знает, что хорошая диаграмма — это как хорошая карта: показывает путь и не дает заблудиться)** 😄 