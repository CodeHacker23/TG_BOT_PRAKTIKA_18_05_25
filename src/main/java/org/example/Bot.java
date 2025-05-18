package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
   private Service service;


    public Bot(String botToken) {
        super(botToken);
        this.service = new Service();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!(update.hasMessage() && update.getMessage().hasText())) return;

        //get info request
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        //get answer in text
        String result = service.getWay(text);

        // builder object for answer
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(result);
        sendMessage.setChatId(chatId);

        //send answer (sendMessage )
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public String getBotUsername() {
        return "Collection_bot";
    }


}
