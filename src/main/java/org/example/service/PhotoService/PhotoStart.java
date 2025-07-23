package org.example.service.PhotoService;

import org.example.model.personage.Personage1;
import org.example.model.personage.Personage2;
import org.example.model.personage.Personage3;
import org.example.bot.KeyboardService.KeyboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * PhotoService — сервис для централизованного формирования всех фото-сообщений бота.
 * Здесь хранятся методы для создания SendPhoto для разных сценариев: старт, теория, карточки персонажей и т.д.
 * <p>
 * Почему это важно? Потому что если ты начнёшь лепить фотки в каждом сервисе — твой проект превратится в помойку быстрее, чем дедлайн подкрадётся к твоему коду.
 * <p>
 * Пример использования:
 * SendPhoto photo = photoService.getStartPhoto(chatId);
 * bot.execute(photo);
 * <p>
 * Добавляй новые методы для фоток только сюда, иначе тебя настигнет гнев баг.
 */
@Service
public class PhotoStart {
    private static final Logger log = LoggerFactory.getLogger(PhotoStart.class);

//    public SendPhoto getStartPhoto(Long chatId) {
//        System.out.println("[PhotoService] getStartPhoto() — вызываем стартовую фотку. Если ты это читаешь, значит бот ещё жив.");
//        return SendPhoto.builder()
//                .chatId(chatId)
//                .photo(new InputFile("https://ltdfoto.ru/image/soXapU"))
//                .caption("Вот и ты здесь, новичок. \n" +
//                        "Я — *Доктор БайтФордж* , архитектор программных миров и кузнец идей. \n"
//                        + "Ты в мультивселенной по Java...\n" +
//                        "Где каждая строка — это шаг,а баг — это урок.")
//                .parseMode("Markdown")
//                .build();
//    }

    /**
     * Отправка стартового фото с приветствием от БайтФорджа
     *
     * @param chatId — ID чата Telegram
     * @return SendPhoto — стартовая фотка
     * <p>
     * Пример:
     * SendPhoto photo = storyStartService.photoStart(chatId);
     * bot.execute(photo);
     *
     *   /**
     *      * Стартовое фото с приветствием от БайтФордж
     *      *
     *      * @param chatId — ID чата Telegram, куда отправлять фото
     *      * @return SendPhoto — готовый объект для отправки
     *      * <p>
     *      * Пример:
     *      * SendPhoto photo = photoService.getStartPhoto(123456789L);
     *      * bot.execute(photo);
     *
     */
    public static SendPhoto photoStart(Long chatId) {
        log.info("photoStart() — отправляем стартовую фотку. Пользователь только что зашёл в мультивселенную.");
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile("https://ltdfoto.ru/image/soXapU"))
                .caption("Вот и ты здесь, новичок. \n" +
                        "Я — *Доктор БайтФордж* , архитектор программных миров и кузнец идей. \n"
                        + "Ты в мультивселенной по Java...\n" +
                        "Где каждая строка — это шаг,а баг — это урок.")
                .parseMode("Markdown")
                .build();
    }



    /**
     * Карточка персонажа 1 (Кодыч)
     *
     * @param chatId     — ID чата Telegram
     * @param personage1 — экземпляр персонажа 1
     * @return SendPhoto — карточка персонажа
     * <p>
     * Пример:
     * Personage1 p1 = new Personage1();
     * p1.setName("Вася");
     * SendPhoto card = photoService.getPersonage1Card(chatId, p1);
     * bot.execute(card);
     * <p>
     * Если ты попытаешься передать сюда null — персонаж обидится и уйдёт в отпуск.
     */
    public SendPhoto getPersonage1Card(Long chatId, Personage1 personage1) {
        System.out.println("[PhotoService] getPersonage1Card() — выдаём карточку персонажа 1. Имя: " + personage1.getName());
        return personage1.getSendPhotoTheory(chatId);
    }

    /**
     * Карточка персонажа 2 (Флой)
     *
     * @param chatId     — ID чата Telegram
     * @param personage2 — экземпляр персонажа 2
     * @return SendPhoto — карточка персонажа
     * <p>
     * Пример:
     * Personage2 p2 = new Personage2();
     * p2.setName("Петя");
     * SendPhoto card = photoService.getPersonage2Card(chatId, p2);
     * bot.execute(card);
     * <p>
     * Если ты не передашь персонажа — баги вылезут из-под кровати.
     */
    public SendPhoto getPersonage2Card(Long chatId, Personage2 personage2) {
        System.out.println("[PhotoService] getPersonage2Card() — выдаём карточку персонажа 2. Имя: " + personage2.getName());
        return personage2.PhotoTheoryFloy(chatId);
    }

    /**
     * Карточка персонажа 3 (Гекс)
     *
     * @param chatId     — ID чата Telegram
     * @param personage3 — экземпляр персонажа 3
     * @return SendPhoto — карточка персонажа
     * <p>
     * Пример:
     * Personage3 p3 = new Personage3();
     * p3.setName("Лёха");
     * SendPhoto card = photoService.getPersonage3Card(chatId, p3);
     * bot.execute(card);
     * <p>
     * Если передашь не того персонажа — Гекс тебя закодит.
     */
    public SendPhoto getPersonage3Card(Long chatId, Personage3 personage3) {
        System.out.println("[PhotoService] getPersonage3Card() — выдаём карточку персонажа 3. Имя: " + personage3.getName());
        return personage3.PhotoTheoryGeks(chatId);
    }

    //метод фото которы отправляет Байта с вертолетом.
    /**
     * Фото с БайтФорджем на вертолёте (эпичный момент)
     * @param chatId — ID чата Telegram
     * @return SendPhoto — фото для эпичного прощания
     * Пример:
     *   SendPhoto photo = photoService.getByteFordj(chatId);
     *   bot.execute(photo);
     * Юмор: если не добавишь подпись — БайтФордж улетит без тебя.
     */
    public SendPhoto getByteFordj(Long chatId) {
        System.out.println("[PhotoService] getByteFordj() — вызываем фото с БайтФорджем на вертолёте.");
        SendPhoto ByteFordjGoodbay = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile("https://ltdfoto.ru/image/s5AGts"))
                .caption("*БайтФордж*\n" +
                        "Внимание, герой.\n" +
                        "_Меня срочно вызывают — один сервер начал воспринимать баги как фичи. Последствия могут быть… катастрофическими._\n" +
                        "\n" +
                        "☕\uFE0F Мне предстоит срочная отладка ядра одной из реальностей...\n" +
                        "Ты стоишь на пороге великой мульти вселенной.\n" +
                        "Там, где код — это стратегия, а компилятор — твой лучший помощник")
                .parseMode("Markdown")
                .build();
        return ByteFordjGoodbay;
    }



    /**
     * Фото Java 6 (симуляция Sys.exit(0))
     * @param chatId — ID чата Telegram
     * @return SendPhoto — мемная фотка про Java 6
     * Пример:
     *   SendPhoto photo = PhotoService.photoJava6(chatId);
     *   bot.execute(photo);
     * Юмор: если не знаешь, что такое try/catch — баги вылезут из всех коллекций.
     */
    public static SendPhoto photoJava6(Long chatId){
        System.out.println("[PhotoService] photoJava6() — отправляем мемную фотку про Java 6.");
        SendPhoto sendPhoto = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile("https://ltdfoto.ru/image/sCbgoi"))
                .caption("Это называется...\n" +
                        "Симуляция ``` Sys.exit(0) ```\n" +
                        "Место, где нет структуры. Где каждый день — try, но никогда — catch.")
                .parseMode("Markdown")
                .replyMarkup(KeyboardService.comeBack(chatId))
                .build();
        return  sendPhoto;
    }


    // Добавляйте сюда любые другие методы для фото, если потребуется
    // Если добавишь метод без комментария — тебя найдёт Доктор БайтФордж и заставит писать документацию до пенсии.
} 