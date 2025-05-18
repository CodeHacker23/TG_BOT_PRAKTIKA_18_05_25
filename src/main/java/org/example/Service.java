package org.example;

public class Service {


    public String getWay(String request){
        String answer = "";

        switch (request){
            case "/start" -> {answer= startCommand();}
            case "/list" -> {answer= listCommand();}
//            case " " -> {answer=}
//            case " " -> {answer=}
//            case " " -> {answer=}
//            case " " -> {answer=}
        }

        return answer;
    }

    private String listCommand() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("List - это коллекция которая сохраняет порядок добавленных элементов\n");
        stringBuilder.append("Пример создания : List<String> namesList;\n");
        return stringBuilder.toString();
    }

    private String startCommand() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("bot for lesson collection\n");
        stringBuilder.append("/list\n");
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
