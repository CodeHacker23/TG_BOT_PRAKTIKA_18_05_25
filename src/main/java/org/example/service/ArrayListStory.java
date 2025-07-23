package org.example.service;

import org.example.repository.PersonageRepository;
import org.example.service.PhotoService.PhotoStart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ArrayListStory { //наша ветка по сюжетке Array
    private static final Logger log = LoggerFactory.getLogger(StoryStartService.class);
    // Сервис с методами для отправки фото и теории (название "Service" — это боль, не повторяй так)
    private final org.example.Service service;
    // Сервис для централизованной логики создания персонажа
    private final PersonageCreationService personageCreationService;
    // Сервис для работы с пользователями
    public final UserService userService;
    public final PhotoStart photoStart;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final PersonageRepository personageRepository;

    public ArrayListStory(org.example.Service service, PersonageCreationService personageCreationService, UserService userService, PhotoStart photoStart, PersonageRepository personageRepository) {
        this.service = service;
        this.personageCreationService = personageCreationService;
        this.userService = userService;
        this.photoStart = photoStart;
        this.personageRepository = personageRepository;
    }

    /**
     * Предупреждающее смс от Итераториуса
     * @param chatId
     * @return sendMessage
     *
     */
    public static SendMessage ReamIteratorius(Long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*Итераториус* \n\n" +
                "⚔\uFE0F Твоя первая цель — ArrayList.\n" +
                "Не дай простоте тебя обмануть.\n" +
                "Он вроде как списочек…\n" +
                "Но стоит переполнить — и тебя отбрасывает в древнюю арену newCapacity().");
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }




    /**
     * Формирует текст теории по ArrayList (вынесено отдельно для переиспользования)
     * @return String — текст теории
     */
    private static String formatArrayListInfo() {
        StringBuilder sb = new StringBuilder();

        // Пример экранирования
        sb.append("\uD83E\uDDE0 *Внимание: свиток самоуничтожится через 20 секунд. Успей зацепить главное!*");
        sb.append("ArrayList - это динамический массив, реализующий интерфейс List.\n");
        sb.append("Он автоматически меняет свой размер на 50 - 100% при добавлении/удалении элементов,\n");
        sb.append("но операции вставки/удаления в середине списка могут быть медленными\n");
        sb.append("из-за необходимости копирования элементов.\n\n");

        // Пример создания
        sb.append("Пример создания:\n");
        sb.append("```\n");
        sb.append("ArrayList<String> box = new ArrayList<>();\n");
        sb.append("```\n\n");

        // Методы
        sb.append("Основные методы:\n\n");

        // add()
        sb.append("add(E element) - Добавляет элемент в конец списка.\n");
        sb.append("```\n");
        sb.append("ArrayList<String> toys = new ArrayList<>();\n");
        sb.append("toys.add(\"Машинка\"); // Добавили машинку в коробку\n");
        sb.append("toys.add(\"Кукла\");   // Добавили куклу\n");
        sb.append("```\n\n");

        // get()
        sb.append("get(int index) - Получает элемент по индексу.\n");
        sb.append("```\n");
        sb.append("String firstToy = toys.get(0); // Получаем первую игрушку (индекс 0)\n");
        sb.append("System.out.println(firstToy);  // Выведет: Машинка\n");
        sb.append("```\n\n");

        // set()
        sb.append("set(int index, E element) - Заменяет элемент.\n");
        sb.append("```\n");
        sb.append("toys.set(1, \"Робот\"); // Заменяем куклу на робота\n");
        sb.append("```\n\n");

        // remove()
        sb.append("remove(int index) - Удаляет элемент по индексу.\n");
        sb.append("```\n");
        sb.append("toys.remove(0); // Удаляем машинку (индекс 0)\n");
        sb.append("```\n\n");

        // size()
        sb.append("size() - Возвращает количество элементов.\n\n");
        sb.append("```\n");
        sb.append("int count = toys.size();\n");
        sb.append("System.out.println(\"В коробке \" + count + \" игрушек\");\n");
        sb.append("```\n\n");

        // add(int index, E element)
        sb.append("add(int index, E element) — вставка по индексу. \n");
        sb.append("Позволяет вставить элемент не только в конец , но и в любое место списка.\n");
        sb.append("Например, вставить \"Новую игрушку\" между \"Машинкой\" и \"Куклой\":\n");
        sb.append("```\n");
        sb.append("toys.add(1, \"Новая игрушка\"); // Теперь порядок: Машинка, Новая игрушка, Кукла. ");
        sb.append("```\n\n");
        sb.append("Однако вставка в середину/начало списка требует сдвига всех последующих элементов, что может быть медленным для больших списков!! \n");

        //  System.out.println("Формируемая теория: " + sb.toString());

        return sb.toString();
    }

    /**
     * Получить теорию по ArrayList (для других сервисов)
     * @return String — текст теории
     */
    public static String getArrayListInfo(Long chatId) {
        return formatArrayListInfo();
    }



}
