package org.example;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * Service — универсальный сервис для обработки команд, которые не относятся к конкретному сюжету.
 * Здесь можно делать роутинг команд типа /list, /ArrayList, /LinkedList и т.д.
 *
 * Почему нельзя лепить всё сюда? Потому что если начнёшь писать бизнес-логику прямо тут — твой сервис быстро превратится в помойку.
 *
 * Пример расширения:
 *   - Хочешь добавить новую команду? Добавь case в switch и делегируй обработку в отдельный StoryService (например, LinkedListStoryService).
 *   - Не пихай всю логику прямо сюда — делегируй!
 *
 * Юмор: если добавишь 100 case'ов в switch — Архитектор лично напишет тебе в Telegram.
 */
@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class Service {

    private final UserService userService;

    /**
     * Обрабатывает команды и делегирует их в нужные StoryService
     * @param request — команда пользователя (например, /list, /ArrayList)
     * @return String — ответ для пользователя
     *
     * Пример:
     *   String answer = service.getWay("/list");
     *   bot.execute(new SendMessage(chatId, answer));
     */
    public String getWay(String request) {
        System.out.println("[Service] getWay() — обработка команды: " + request);
        String answer = "";
        switch (request) {
            case "/start" -> {
                // startCommand обрабатывается отдельно в StartCommandService
                answer = "";
            }
            case "/list" -> {
                answer = listCommand();
            }
            case "/ArrayList" -> {
                // Делегируй обработку в ArrayListStoryService!
                // answer = arrayListStoryService.getArrayListInfo();
            }
            case "" -> {

            }
            default -> {
                answer = "";
            }
        }
        return answer;
    }

    /**
     * Возвращает теорию по List (общая информация)
     * @return String — теория по List
     *
     * Пример:
     *   String info = service.listCommand();
     */
    private String listCommand() {
        System.out.println("[Service] listCommand() — возвращаем теорию по List");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("List - это коллекция которая сохраняет порядок добавленных элементов\n");
        stringBuilder.append("Пример создания : List<String> namesList;\n");
        return stringBuilder.toString();
    }

    // --- Советы по расширению ---
    // 1. Для каждой новой коллекции (LinkedList, Set, Map и т.д.) делай отдельный StoryService.
    // 2. В этом сервисе только роутинг и общая информация, не пихай сюда бизнес-логику.
    // 3. Если логика повторяется — выноси в абстрактные классы/интерфейсы.
    // 4. Если добавишь 100 case'ов — Архитектор лично напишет тебе в Telegram.
}


//        stringBuilder.append("bot for lesson collection\n");
//        stringBuilder.append("/list\n"); // я
//        stringBuilder.append("/ArrayList\n");
//        stringBuilder.append("/LinkedList\n");
//        stringBuilder.append("************\n");
//        stringBuilder.append("/Set\n");
//        stringBuilder.append("/HashSet\n");
//        stringBuilder.append("/linkedHashSet\n");
//        stringBuilder.append("/TreeSet\n");
//        stringBuilder.append("************\n"); // C
//        stringBuilder.append("/Map\n");
//        stringBuilder.append("/HashMap\n");
//        stringBuilder.append("/LinkedHashMap\n");
//        stringBuilder.append("/TreeMap\n");
//        stringBuilder.append("************\n");
//        stringBuilder.append("Comparator\n");
//        stringBuilder.append("Iterable");

//        return stringBuilder.toString();






