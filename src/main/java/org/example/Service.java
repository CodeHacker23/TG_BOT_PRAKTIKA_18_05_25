package org.example;


import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class Service {


    public String getWay(String request) {

        String answer = "";

        switch (request) {
            case "/start" -> {
                answer = startCommand();
            }
            case "/list" -> {
                answer = listCommand();
            }
            case "/ArrayList" -> {
                answer = formatArrayListInfo();
//            case " " -> {answer=}
//            case " " -> {answer=}
//            case " " -> {answer=}
            }
        }
        return answer;
    }


    private String listCommand() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("List - это коллекция которая сохраняет порядок добавленных элементов\n");
        stringBuilder.append("Пример создания : List<String> namesList;\n");
        return stringBuilder.toString();
    }


    private String formatArrayListInfo(  ) {
        StringBuilder sb = new StringBuilder();
        System.out.println("В обработке на фото");
        // getPhotoTheory();


        // Основное описание
        sb.append("\uD83E\uDDE9  ArrayList - это динамический массив, реализующий интерфейс List.\n");
        sb.append("Он автоматически меняет размер при добавлении/удалении элементов,\n");
        sb.append("но операции вставки/удаления в середине списка могут быть медленными\n");
        sb.append("из-за необходимости копирования элементов.\n\n");

        // Пример создания
        sb.append("\uD83D\uDCE6 Пример создания:\n");
        sb.append("---------\n");
        sb.append("ArrayList<String> box = new ArrayList<>();\n");
        sb.append("---------\n\n");

        // Методы
        sb.append("\uD83D\uDEE0\uFE0F Основные методы:\n\n");

        // add()
        sb.append("\uFE0F add(E element) - Добавляет элемент в конец списка.\n");
        sb.append("\n");
        sb.append("ArrayList<String> toys = new ArrayList<>();\n");
        sb.append("toys.add(\"Машинка\"); // Добавили машинку в коробку\n");
        sb.append("toys.add(\"Кукла\");   // Добавили куклу\n");
        sb.append("---------\n\n");

        // get()
        sb.append("\uD83D\uDD0D get(int index) - Получает элемент по индексу.\n");
        sb.append("\n");
        sb.append("String firstToy = toys.get(0); // Получаем первую игрушку (индекс 0)\n");
        sb.append("System.out.println(firstToy);  // Выведет: Машинка\n");
        sb.append("---------\n\n");

        // set()
        sb.append("\uD83D\uDD04 set(int index, E element) - Заменяет элемент.\n");
        sb.append("\n");
        sb.append("toys.set(1, \"Робот\"); // Заменяем куклу на робота\n");
        sb.append("---------\n\n");

        // remove()
        sb.append("\uD83D\uDDD1\uFE0F remove(int index) - Удаляет элемент по индексу.\n");
        sb.append("\n");
        sb.append("toys.remove(0); // Удаляем машинку (индекс 0)\n");
        sb.append("---------\n\n");

        // size()
        sb.append("\uD83D\uDCC4 size() - Возвращает количество элементов.\n");
        sb.append("\n");
        sb.append("int count = toys.size();\n");
        sb.append("System.out.println(\"В коробке \" + count + \" игрушек\");\n");
        sb.append("---------\n");

        //add(int index, E element)
        sb.append("\n⛓\uFE0F\u200D\uD83D\uDCA5 add(int index, E element) — вставка по индексу. \n");
        sb.append("Позволяет вставить элемент не только в конец , но и в любое место списка." +
                "\nНапример, вставить \"Новую игрушку\" между \"Машинкой\" и \"Куклой\":");
        sb.append("\n\ntoys.add(1, \"Новая игрушка\"); // Теперь порядок: Машинка, Новая игрушка, Кукла. ");
        sb.append("\n‼\uFE0F Однако вставка в середину списка требует сдвига всех последующих элементов, что может быть медленным для больших списков!! \n");
        sb.append("---------\n");
        return sb.toString();

    }

    public String getArrayListInfo() { // Сделали что бы все было по людски, по ООП, метод об Array должен быть приватным
        return formatArrayListInfo();
    }


    public SendPhoto getPhoto(Long chatId) {
        SendPhoto sendPhoto = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile("https://cdn-images.mn.ru/images/2025/05/mem-o-kak-size_834x1015.jpg"))
                .build();
        return sendPhoto;
    }


    private SendPhoto getPhotoTheory( ) {
        SendPhoto sendPhotoTheory = SendPhoto
                .builder()
                .photo(new InputFile("https://thepresentation.ru/img/tmb/4/355544/cfe8189200c24b3ef3cec4bbae5c92b1-800x.jpg"))
                .build();
        return sendPhotoTheory;


    }
    private static SendPhoto getSendPhotoTheory() {
        SendPhoto sendPhotoTheory = SendPhoto
                .builder()
                .photo(new InputFile("https://thepresentation.ru/img/tmb/4/355544/cfe8189200c24b3ef3cec4bbae5c92b1-800x.jpg"))
                .build();
        return sendPhotoTheory;
    }


    private String startCommand() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("bot for lesson collection\n");
        stringBuilder.append("/list\n"); // я
        stringBuilder.append("/ArrayList\n");
        stringBuilder.append("/LinkedList\n");
        stringBuilder.append("************\n");
        stringBuilder.append("/Set\n");
        stringBuilder.append("/HashSet\n");
        stringBuilder.append("/linkedHashSet\n");
        stringBuilder.append("/TreeSet\n");
        stringBuilder.append("************\n");
        stringBuilder.append("/Map\n");
        stringBuilder.append("/HashMap\n");
        stringBuilder.append("/LinkedHashMap\n");
        stringBuilder.append("/TreeMap\n");
        stringBuilder.append("************\n");
        stringBuilder.append("Comparator\n");
        stringBuilder.append("Iterable");

        return stringBuilder.toString();
    }
}





