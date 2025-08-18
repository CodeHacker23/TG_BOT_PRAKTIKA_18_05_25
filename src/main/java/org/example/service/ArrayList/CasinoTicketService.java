package org.example.service.ArrayList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.PersonageService;
import org.example.service.PersonageStatManager;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CasinoTicketService {
    private final StatsDisplayService statsDisplayService;
    private final UserService userService;           // Для получения юзера
    private final PersonageService personageService; // Для обновления статов
    private final StatService statService;


    public SendMessage handleTicketSelection(Long chatId, String buttonText) {
        switch (buttonText) {
            case "1⃣" -> {
                return ticket1(chatId);
            }
            default -> {
                return new SendMessage(chatId.toString(), "❌ Неизвестный билет!");
            }
        }
    }


    public SendMessage ticket1(Long chatId) {
        log.debug("ticket1: Создание результата билета 1 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", 100,
                "currency", 300
        ));

        return new SendMessage(chatId.toString(),
                " 🎁 БИЛЕТ 1: ```NullPointerException111\n" +
                        " +300 💲 денег," +
                        " +100 ⭐️ опыта\n" +
                        "*Ха! Null оказался не таким уж плохим!* \n" +
                        "_Иногда даже отсутствие данных — это подарок!_");
    }

    public SendMessage ticket2(Long chatId) {
        log.debug("ticket2: Создание результата билета 1 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", -50,
                "currency", -200
        ));

        return new SendMessage(chatId.toString(),
                "\uD83D\uDC80 БИЛЕТ 2: ```OutOfMemoryError```\n" +
                        "-200💲денег,\n" +
                        "-50 ⭐️ опыта \n" +
                        "*Память закончилась... как и твои деньги!*\n" +
                        "_Welcome to the real world, малец!_"
        );
    }

    public SendMessage ticket3(Long chatId) {
        log.debug("ticket3: Создание результата билета 1 для chatId={}", chatId);
        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", -100,
                "currency", -250
        ));
        return new SendMessage(chatId.toString(),

                "\uD83D\uDC80 БИЛЕТ 3: ```StackOverflowError```\n" +
                        "-250 💲денег,\n" +
                        "-100 ⭐️опыта\n" +
                        "*Стек переполнился... как и твои проблемы!*\n" +
                        "_Рекурсия — это как алкоголизм, только для кода!_"
        );
    }

    public SendMessage ticket4(Long chatId) {
        log.debug("ticket4: Создание результата билета 1 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", -120,
                "currency", -300
        ));
        return new SendMessage(chatId.toString(),
                "\uD83D\uDC80 БИЛЕТ 4: ```ClassCastException```\n" +
                        "-300 💲денег\n " +
                        "-100 ⭐️опыта\n" +
                        "*Нельзя привести String к Integer... как и твои надежды!*\n" +
                        "_Иногда лучше остаться собой, чем пытаться быть кем-то другим!_");
    }


    public SendMessage ticket5(Long chatId) {
        log.debug("ticket5: Создание результата билета 1 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", 200,
                "currency", 500
        ));
        return new SendMessage(chatId.toString(),
                "\uD83C\uDF81 БИЛЕТ 5: ```IllegalArgumentException```\n" +
                        "+500 💲денег\n" +
                        "+200 ⭐️опыта \n" +
                        "*Аргумент был нелегальным, но награда — легальная!*\n" +
                        "_В программировании, как в жизни — всё относительно!_");
    }

    public SendMessage ticket6(Long chatId) {
        log.debug("ticket6: Создание результата билета 1 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", -50,
                "currency", -150
        ));

        return new SendMessage(chatId.toString(),
                "\uD83D\uDC80 БИЛЕТ 6:```ConcurrentModificationException```\n" +
                        "-150 💲денег\n" +
                        "-50 ⭐️опыта\n" +
                        "*Попытался изменить список во время итерации... классика!* \n" +
                        "_Это как пытаться перекрасить машину во время езды!_");
    }

    public SendMessage ticket7(Long chatId) {
        log.debug("ticket7: Создание результата билета 1 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", 150,
                "currency", 400
        ));

        return new SendMessage(chatId.toString(),
                "\uD83C\uDF81 БИЛЕТ 7: ```ArrayIndexOutOfBoundsException``` \n" +
                        "+400 💲денег\n" +
                        "+150 ⭐️опыта\n" +
                        "*Иногда выйти за границы — значит найти новые возможности!*\n" +
                        "_Главное, чтобы прод это не увидел!_");
    }

    public SendMessage ticket8(Long chatId) {
        log.debug("ticket8: Создание результата билета 1 для chatId={}", chatId);

        statService.applyStatChanges(chatId, Map.of(
                "achievement_points", 500,
                "currency", 1000
        ));

        return new SendMessage(chatId.toString(),
                "\uD83D\uDE80\uD83C\uDF89 БИЛЕТ 8: ДЖЕКПОТ: Редкое исключение!\n" +
                        "+1000 💲денег\n" +
                        "+500 ⭐️опыта \uD83D\uDD25\n" +
                        "ВАУ! Ты вытащил ДЖЕКПОТ! \n" +
                        "*Это как найти золотой баг в продакшене!*\n" +
                        "_Теперь ты можешь купить себе новый MacBook!\n" +
                        "Или хотя бы доширак на неделю!_");
    }



}
