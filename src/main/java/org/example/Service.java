package org.example;


import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class Service{

    private final UserService userService;

    public String getWay(String request) {

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
                answer = formatArrayListInfo();
            }
            default -> {
                answer = "";
            }
        }
        return answer;
    }


    private String listCommand(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("List - это коллекция которая сохраняет порядок добавленных элементов\n");
        stringBuilder.append("Пример создания : List<String> namesList;\n");
        return stringBuilder.toString();
    }


    private static String formatArrayListInfo() {
        StringBuilder sb = new StringBuilder();

        // Пример экранирования
        sb.append("ArrayList - это динамический массив, реализующий интерфейс List.\n");
        sb.append("Он автоматически меняет размер на 50% при добавлении/удалении элементов,\n");
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
        sb.append("Однако вставка в середину списка требует сдвига всех последующих элементов, что может быть медленным для больших списков!! \n");

        //  System.out.println("Формируемая теория: " + sb.toString());

        return sb.toString();
    }

    public static String getArrayListInfo() {// Сделали что бы все было по людски, по ООП, метод об Array должен быть приватным
        return Service.formatArrayListInfo();
    }


    public SendPhoto getPhoto(Long chatId) { //
        SendPhoto sendPhoto = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile("https://cdn-images.mn.ru/images/2025/05/mem-o-kak-size_834x1015.jpg"))
                .build();
        return sendPhoto;
    }


    public SendPhoto getPhotoTheory(Long chatId) { // должно быть фото к посту об ArrayList
        SendPhoto sendPhotoTheory = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile("https://thepresentation.ru/img/tmb/4/355544/cfe8189200c24b3ef3cec4bbae5c92b1-800x.jpg"))
                .build();
        return sendPhotoTheory;
    }

    public SendPhoto photoStart(Long chatId){
        SendPhoto sendPhotoStart = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile("https://imgfoto.host/i/IMG-6301.cWiDQ5"))
                .caption("Приветствую, тебя воин! \n" +
                        "Я потомок JVM! \n" +
                        "Moй StackOverflow переполнен, но я все равно стою, как Римская империя!!")
                .build();
        return sendPhotoStart;
    }


     public SendMessage startCommand(Long chatId) {
        UserEntity user = new UserEntity();
        user.setTgId(chatId);
        user.setUsername("test"); // подумать

        userService.saveUser(user);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(
                "Ты попал в мир Java-коллекций — Увлекательного приключения!\n" +
                "Где ты узнаешь, как работают ArrayList, LinkedList, и др структуры данных. \n" +
                "В этом мире тебе предстоит  сражаться за уровни, решать задачи, собирать знания, искать пасхалки и получать боевые награды! \uD83C\uDFC6 \n" +
                "\n\uD83D\uDD39 Начни с теории — если ты новичок! \n" + "\uD83D\uDD39 Пройти тест — если чувствуешь себя уверенно.\n" + "\uD83D\uDD39Испытать квест — если любишь головоломки и приключения.\n" +
                "Готов начать? Тогда выбери свой первый путь воин! ");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("\uD83D\uDE80 Начать с теории"));
        row.add(new KeyboardButton("\uD83D\uDCA1 Пройти тест на знания"));
        row.add(new KeyboardButton("\uD83C\uDFAE Испытать квест"));

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setText(stringBuilder.toString());
        message.setChatId(chatId.toString());
        message.setReplyMarkup(keyboardMarkup);


        return message;
    }
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



//    public static String escapeMarkdown(String text) {
//        return text.replace("_", "\\_")
//                .replace("*", "\\*")
//                .replace("[", "\\[")
//                .replace("]", "\\]")
//                .replace("(", "\\(")
//                .replace(")", "\\)")
//                .replace("~", "\\~")
//                .replace("`", "\\`")
//                .replace(">", "\\>")
//                .replace("#", "\\#")
//                .replace("+", "\\+")
//                .replace("-", "\\-")
//                .replace("=", "\\=")
//                .replace("|", "\\|")
//                .replace("{", "\\{")
//                .replace("}", "\\}")
//                .replace(".", "\\.")
//                .replace("!", "\\!");






