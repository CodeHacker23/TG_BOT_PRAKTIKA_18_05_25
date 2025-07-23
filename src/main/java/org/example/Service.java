package org.example;

import lombok.RequiredArgsConstructor;
import org.example.service.ArrayListStory;
import org.example.service.ArrayListStoryService;
import org.example.service.UserService;


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
    private final ArrayListStoryService arrayListStoryService;

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
            // case "📜 Получить боевой свиток" -> {
            //     // Обработка этой команды делегируется в ArrayListStory через MessageHandlerService
            // }
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






