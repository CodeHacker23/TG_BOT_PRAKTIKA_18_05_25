package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bot.KeyboardService.KeyboardReam;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * ArrayListTheoryService — сервис для работы с теорией и контентом ArrayList.
 * 
 * Этот класс отвечает за:
 * - Формирование теоретического материала по ArrayList
 * - Создание статических сообщений для сюжетной линии
 * - Предоставление контента для обучения пользователей
 * 
 * Связи с другими классами:
 * - Используется в ArrayListStory для получения теоретического контента
 * - Используется в ArrayListSchedulerService для отправки теории
 * - Работает с UserService для получения данных пользователей
 * 
 * Принцип работы:
 * 1. Формирует структурированную теорию по ArrayList с примерами кода
 * 2. Создает игровые сообщения от персонажей (Итераториус, Аррейн)
 * 3. Предоставляет контент для разных этапов обучения
 * 
 * Автор: Иларион (который любит чистый код и подробные комментарии)
 * 
 * Пример использования:
 *   @Autowired
 *   private ArrayListTheoryService theoryService;
 *   
 *   String theory = theoryService.formatArrayListInfo();
 *   SendMessage message = theoryService.createIteratoriusMessage(chatId);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArrayListTheoryService {
    
    private final UserService userService;
    private final StatsDisplayService statsDisplayService;
    
    /**
     * Формирует полный текст теории по ArrayList с примерами кода.
     * 
     * Этот метод создает структурированное описание ArrayList, включая:
     * - Основные концепции и особенности
     * - Примеры создания и использования
     * - Описание основных методов (add, get, set, remove, size)
     * - Предупреждения о производительности
     * 
     * Метод используется для:
     * - Отправки теории пользователям при изучении ArrayList
     * - Автоматического удаления через 20 секунд (в ArrayListSchedulerService)
     * - Формирования базы знаний для викторин
     * 
     * @return String — полный текст теории с Markdown разметкой
     * 
     * Пример использования:
     *   String theory = theoryService.formatArrayListInfo();
     *   SendMessage message = new SendMessage(chatId, theory);
     *   message.setParseMode("Markdown");
     */
    public String formatArrayListInfo() {
        log.debug("ArrayListTheoryService: Формирование теории по ArrayList");
        
        StringBuilder sb = new StringBuilder();

        // Предупреждение о самоуничтожении (игровой элемент)
        sb.append("\uD83E\uDDE0 *Внимание: свиток самоуничтожится через 20 секунд. Успей зацепить главное!*\n\n");
        
        // Основная теория
        sb.append("ArrayList - это динамический массив, реализующий интерфейс List.\n");
        sb.append("Он автоматически меняет свой размер на 50 - 100% при добавлении/удалении элементов,\n");
        sb.append("но операции вставки/удаления в середине списка могут быть медленными\n");
        sb.append("из-за необходимости копирования элементов.\n\n");

        // Пример создания
        sb.append("Пример создания:\n");
        sb.append("```\n");
        sb.append("ArrayList<String> box = new ArrayList<>();\n");
        sb.append("```\n\n");

        // Описание основных методов
        sb.append("Основные методы:\n\n");

        // add() - добавление элемента
        sb.append("add(E element) - Добавляет элемент в конец списка.\n");
        sb.append("```\n");
        sb.append("ArrayList<String> toys = new ArrayList<>();\n");
        sb.append("toys.add(\"Машинка\"); // Добавили машинку в коробку\n");
        sb.append("toys.add(\"Кукла\");   // Добавили куклу\n");
        sb.append("```\n\n");

        // get() - получение элемента по индексу
        sb.append("get(int index) - Получает элемент по индексу.\n");
        sb.append("```\n");
        sb.append("String firstToy = toys.get(0); // Получаем первую игрушку (индекс 0)\n");
        sb.append("System.out.println(firstToy);  // Выведет: Машинка\n");
        sb.append("```\n\n");

        // set() - замена элемента
        sb.append("set(int index, E element) - Заменяет элемент.\n");
        sb.append("```\n");
        sb.append("toys.set(1, \"Робот\"); // Заменяем куклу на робота\n");
        sb.append("```\n\n");

        // remove() - удаление элемента
        sb.append("remove(int index) - Удаляет элемент по индексу.\n");
        sb.append("```\n");
        sb.append("toys.remove(0); // Удаляем машинку (индекс 0)\n");
        sb.append("```\n\n");

        // size() - размер списка
        sb.append("size() - Возвращает количество элементов.\n\n");
        sb.append("```\n");
        sb.append("int count = toys.size();\n");
        sb.append("System.out.println(\"В коробке \" + count + \" игрушек\");\n");
        sb.append("```\n\n");

        // add(int index, E element) - вставка по индексу
        sb.append("add(int index, E element) — вставка по индексу. \n");
        sb.append("Позволяет вставить элемент не только в конец , но и в любое место списка.\n");
        sb.append("Например, вставить \"Новую игрушку\" между \"Машинкой\" и \"Куклой\":\n");
        sb.append("```\n");
        sb.append("toys.add(1, \"Новая игрушка\"); // Теперь порядок: Машинка, Новая игрушка, Кукла. ");
        sb.append("```\n\n");
        sb.append("Однако вставка в середину/начало списка требует сдвига всех последующих элементов, что может быть медленным для больших списков!! \n");

        log.debug("ArrayListTheoryService: Теория сформирована, длина: {} символов", sb.length());
        return sb.toString();
    }

    /**
     * Создает предупреждающее сообщение от Итераториуса (персонаж-наставник).
     * 
     * Итераториус — это игровой персонаж, который предупреждает пользователя о сложностях
     * работы с ArrayList и подготавливает к изучению.
     * 
     * Сообщение содержит:
     * - Предупреждение о сложности ArrayList
     * - Упоминание о методе newCapacity()
     * - Кнопки для начала изучения
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение с предупреждением и клавиатурой
     * 
     * Пример использования:
     *   SendMessage warning = theoryService.createIteratoriusMessage(chatId);
     *   bot.execute(warning);
     */
    public static SendMessage createIteratoriusMessage(Long chatId) {
        log.debug("ArrayListTheoryService: Создание предупреждающего сообщения от Итераториуса для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*Итераториус* \n\n" +
                "⚔\uFE0F Твоя первая цель — ArrayList.\n\n" +
                "Не дай простоте тебя обмануть.\n" +
                "Он вроде как списочек…\n" +
                "Но стоит переполнить — и тебя отбрасывает в древнюю арену newCapacity().");
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(KeyboardReam.BattleList(chatId));
        
        log.debug("ArrayListTheoryService: Сообщение от Итераториуса создано");
        return sendMessage;
    }

    /**
     * Создает сообщение о приближении противника (Аррейн).
     * 
     * Это сообщение отправляется после изучения теории и предупреждает
     * о начале боевой части обучения.
     * 
     * Аррейн — это игровой противник, представляющий сложности работы с ArrayList.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о приближении противника
     * 
     * Пример использования:
     *   SendMessage warning = theoryService.createIteratoriusWarningMessage(chatId);
     *   bot.execute(warning);
     */
    public SendMessage createIteratoriusWarningMessage(Long chatId) {
        log.debug("ArrayListTheoryService: Создание предупреждения о противнике для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*Итераториус* \n\n" +
                "\uD83D\uDDE1Внимание!\n" +
                "Первый противник приближается...\n" +
                "Это он...\n\n" +
                "*Aррейн* — коварный и быстрый.\n" +
                "Он дублирует элементы. Он путает порядок.\n" +
                "И он ненавидит..._(обращение прервано...)_");
        sendMessage.setParseMode("Markdown");
        
        log.debug("ArrayListTheoryService: Предупреждение о противнике создано");
        return sendMessage;
    }

    /**
     * Создает сообщение с представлением Аррейна (противника).
     * 
     * Аррейн — это персонаж, представляющий сложности и особенности ArrayList.
     * В игровой вселенной он объясняет, почему ArrayList может быть сложным.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение с представлением Аррейна
     * 
     * Пример использования:
     *   SendMessage intro = theoryService.createArrayenIntroMessage(chatId);
     *   bot.execute(intro);
     */
    public SendMessage createArrayenIntroMessage(Long chatId) {
        log.debug("ArrayListTheoryService: Создание представления Аррейна для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Аррейн*\n\n" +
                "Думаешь, я просто список? Я — чертов ArrayList, дружище.\n" +
                "И когда ты лезешь ко мне с вставкой по индексу — я пересоздаю себя.\n" +
                "Полностью.\n" +
                "Целиком, мать его.\n" +
                "Потому что в Java всё просто — пока не становится ПИЗ''Ц как сложно.");
        
        log.debug("ArrayListTheoryService: Представление Аррейна создано");
        return sendMessage;
    }

    /**
     * Создает информационное сообщение от Аррейна о производительности.
     * 
     * Это сообщение объясняет, почему вставка в начало ArrayList может быть медленной,
     * используя игровую терминологию.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — информационное сообщение о производительности
     * 
     * Пример использования:
     *   SendMessage info = theoryService.createArrayenInfoMessage(chatId);
     *   bot.execute(info);
     */
    public SendMessage createArrayenInfoMessage(Long chatId) {
        log.debug("ArrayListTheoryService: Создание информационного сообщения Аррейна для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Аррейн*\n\n" +
                "Я не против, если ты добавишь (Е element) в конец.\n" +
                "Но попробуй вставить в индекс 0 — и ты узнаешь, что такое боль....\" ");
        
        log.debug("ArrayListTheoryService: Информационное сообщение Аррейна создано");
        return sendMessage;
    }

    /**
     * Создает сообщение с выбором действий для пользователя.
     * 
     * Это сообщение предоставляет пользователю кнопки для выбора дальнейших действий
     * в процессе изучения ArrayList.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение с кнопками выбора
     * 
     * Пример использования:
     *   SendMessage choice = theoryService.createChoiceMessage(chatId);
     *   bot.execute(choice);
     */
    public SendMessage createChoiceMessage(Long chatId) {
        log.debug("ArrayListTheoryService: Создание сообщения с выбором для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("" +
                "\uD83C\uDFAE Тебе доступны действия: \uD83C\uDFAE");
        sendMessage.setReplyMarkup(KeyboardReam.BattlArreyn(chatId));
        
        log.debug("ArrayListTheoryService: Сообщение с выбором создано");
        return sendMessage;
    }

    /**
     * Создает сообщение с информацией о раунде 1.
     * 
     * Это сообщение отображает текущие характеристики персонажа пользователя
     * и объявляет начало первого раунда обучения.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение с информацией о раунде и статами персонажа
     * 
     * Пример использования:
     *   SendMessage round = theoryService.createRound1Message(chatId);
     *   bot.execute(round);
     */
    public SendMessage createRound1Message(Long chatId) {
        log.debug("ArrayListTheoryService: Создание сообщения о раунде 1 для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);

        // Получаем пользователя и персонажа
        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("ArrayListTheoryService: Пользователь или персонаж не найден для chatId={}", chatId);
            sendMessage.setText("Ошибка: персонаж не найден.");
            return sendMessage;
        }
        
        PersonageEntity entity = user.getPersonage();

        // Формируем строку статов через StatsDisplayService
        String statsLine = statsDisplayService.buildStatsLine(entity);

        sendMessage.setText(
            "*Раунд 1 — 'Код под давлением'*\n\n" +
            "Твои статы:\n" +
            statsLine
        );
        
        log.debug("ArrayListTheoryService: Сообщение о раунде 1 создано");
        return sendMessage;
    }



    /**
     * Создает сообщение с объявлением раунда 3.
     * 
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о раунде 3
     */
    public SendMessage createRound3Message(Long chatId) {
        log.debug("ArrayListTheoryService: Создание сообщения о раунде 3 для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);
        sendMessage.setText(" \uD83D\uDCDC Раунд 3 — *'Последний выбор'*");
        
        log.debug("ArrayListTheoryService: Сообщение о раунде 3 создано");
        return sendMessage;
    }


} 