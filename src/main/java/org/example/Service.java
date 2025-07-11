package org.example;


import lombok.RequiredArgsConstructor;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;


@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class Service {

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

            }

            case "" -> {

            }
            default -> {
                answer = "";
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


    public SendPhoto getPhotoTheory(Long chatId) { // должно быть фото к посту об ArrayList
        SendPhoto sendPhotoTheory = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile("https://thepresentation.ru/img/tmb/4/355544/cfe8189200c24b3ef3cec4bbae5c92b1-800x.jpg"))
                .build();
        return sendPhotoTheory;
    }

    public SendPhoto getPhoto(Long chatId) { // мем с котом
        SendPhoto sendPhoto = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile("https://cdn-images.mn.ru/images/2025/05/mem-o-kak-size_834x1015.jpg"))
                .build();
        return sendPhoto;
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






