package org.example.service.PhotoService;

import org.example.bot.KeyboardService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Service
public class PhotoReam {
    /**
     * Фото с теорией по ArrayList
     *
     * @param chatId — ID чата Telegram
     * @return SendPhoto — объект для отправки теории
     * <p>
     * Пример:
     * SendPhoto photo = photoService.getArrayListTheoryPhoto(chatId);
     * bot.execute(photo);
     */
    public static SendPhoto getArrayListTheoryPhoto(Long chatId) {
        System.out.println("[PhotoService] getArrayListTheoryPhoto() — кто-то решил поумнеть и узнать про ArrayList. Держи картинку!");
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile("https://thepresentation.ru/img/tmb/4/355544/cfe8189200c24b3ef3cec4bbae5c92b1-800x.jpg"))
                .parseMode("Markdown")
                .build();


    }

    /**
     * Мем: встреча в Риме (фото воина)
     * @param chatId — ID чата Telegram
     * @return SendPhoto — мемная фотка
     * Пример:
     *   SendPhoto photo = photoService.photoWarrior(chatId);
     *   bot.execute(photo);
     * Юмор: если не добавишь мем — Архитектор пришлёт тебе мем про SpaghettiCode.
     */
    public SendPhoto photoWarrior(Long chatId){
        System.out.println("[PhotoService] photoWarrior() — отправляем мемную фотку воина.");
        SendPhoto sendPhotoStart = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile("https://imgfoto.host/i/IMG-6301.cWiDQ5"))
                .caption("Приветствую, тебя воин! \n" +
                        "Я потомок JVM! \n" +
                        "Moй Stack переполнен, но я все равно стою, как Римская империя!!")
                .build();
        return sendPhotoStart;
    }

    /**
     * Фото генерала Итераториуса (легенда коллекций)
     * @param chatId — ID чата Telegram
     * @return SendPhoto — фото генерала
     * Пример:
     *   SendPhoto photo = photoService.photoIteratorius(chatId);
     *   bot.execute(photo);
     * Юмор: если не знаешь, что такое .next() — Итераториус тебя не пощадит.
     */
    public SendPhoto photoIteratorius(Long chatId){
        System.out.println("[PhotoService] photoIteratorius() — отправляем фото генерала Итераториуса.");
        SendPhoto sendPhoto = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile("https://ltdfoto.ru/image/sC91sk"))
                .caption("*Я — Генерал Итераториус.* \n"+
                        "_На этой арене не выживают те, кто не знает, что такое .next()..._\n" +
                        "\n" +
                        "Ты прибыл из будущего...\n" +
                        "Но, чтобы выжить здесь, тебе нужен новый облик.\n" +
                        "Не худи, не наушники, не кофеин — А броня \uD83D\uDEE1.\n" +
                        "\n" +
                        "Держи доспехи — под размер твоего кода!")
                .parseMode("Markdown")
                .replyMarkup(KeyboardService.gladiatorPlot(chatId))
                .build();
        return sendPhoto;
    }

}
