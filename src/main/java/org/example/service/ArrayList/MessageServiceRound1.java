package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bot.KeyboardService.KeyboardReam;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

/**
 * MessageService — универсальный сервис для создания сообщений в Telegram боте.
 * <p>
 * Этот класс отвечает за:
 * - Создание боевых сообщений (try-catch, анализ, вставка)
 * - Создание сообщений с результатами и наградами
 * - Создание сообщений с изменениями статов персонажей
 * - Форматирование текста сообщений с Markdown
 * - Создание сообщений с клавиатурами
 * <p>
 * Связи с другими классами:
 * - Использует UserService для получения данных пользователей
 * - Использует StatService для работы со статами персонажей
 * - Интегрируется с KeyboardReam для создания клавиатур
 * - Используется в ArrayListStory для создания сообщений
 * <p>
 * Типы создаваемых сообщений:
 * - Боевые действия (защита, анализ, вставка)
 * - Результаты с наградами (опыт, деньги, индивидуальные статы)
 * - Информационные сообщения (атака, завершение раунда)
 * - Сообщения с клавиатурами для взаимодействия
 * <p>
 * Форматирование:
 * - Использует Markdown для жирного текста (*текст*)
 * - Использует курсив для описаний (_текст_)
 * - Использует код-блоки для примеров (```код```)
 * - Добавляет эмодзи для визуального оформления
 * <p>
 * Автор: Иларион (который знает, что сообщения без логики — это просто текст)
 * <p>
 * Пример использования:
 *
 * @Autowired private MessageService messageService;
 * <p>
 * // Создание боевого сообщения
 * SendMessage defense = messageService.createTryCatchDefenseMessage(chatId);
 * <p>
 * // Создание результата с наградами
 * SendMessage result = messageService.createBattleResultMessage(chatId, 50, 200);
 * <p>
 * // Создание сообщения с изменениями статов
 * Map<String, Integer> changes = Map.of("achievement_points", 30, "currency", 150);
 * SendMessage stats = messageService.createInsertBeginningResultMessage(chatId, changes);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceRound1 {

    private final UserService userService;
    private final StatService statService;

    /**
     * Создает сообщение о попытке защиты try-catch.
     *
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о защите
     */
    public SendMessage createTryCatchDefenseMessage(Long chatId) {
        log.debug("MessageService: Создание сообщения о защите try-catch для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🛡 **БЛОКИРОВАТЬ (TRY-CATCH)**\n" +
                "*Нанесен урон Aррейну 100(-50)*\n\n" +
                "Ты быстро строишь защитную стену:\n\n" +
                "```java\n" +
                "try {\n" +
                "    list.add(0, \"💀BUG\");\n" +
                "    // Пытаемся защититься от атаки\n" +
                "} catch (IndexOutOfBoundsException e) {\n" +
                "    System.out.println(\"Поймал баг!\");\n" +
                "}\n" +
                "```\n\n" +
                "_Но Аррейн не из тех, кто уважает чужие перехваты!_\n\n" +
                "💥 **Аррейн пробивает твою защиту:**\n" +
                "```\n" +
                "Exception in thread \"main\":\n" +
                "ArrayIndexOutOfBoundsException: Index 0 out of bounds\n" +
                "```\n\n" +
                "🐞 **ПОЛУЧЕН БАГ** - _Ошибка ушла в отпуск, но обещала вернуться к дедлайну._");

        log.debug("MessageService: Сообщение о защите try-catch создано");
        return sendMessage;
    }

    /**
     * Создает сообщение с результатом защиты try-catch.
     *
     * @param chatId     — ID чата пользователя
     * @param expReward  — награда за опыт
     * @param cashReward — награда за деньги
     * @return SendMessage — сообщение с результатом try-catch
     */
    public SendMessage createTryCatchResultMessage(Long chatId, int expReward, int cashReward) {
        log.debug("MessageService: Создание результата try-catch для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус:*\n\n" +
                "Молодец, конечно… Только try-catch не вечен.\n" +
                "Не всё в жизни ловится на костыли.\n" +
                "*Навык повышен:*\n" +
                "  +" + expReward + " ⭐️ к Очкам Достижения\n" +
                "  +" + cashReward + " 💲 к Деньгам.");

        log.debug("MessageService: Результат try-catch создан");
        return sendMessage;
    }

    /**
     * Создает сообщение с результатом анализа.
     *
     * @param chatId     — ID чата пользователя
     * @param expReward  — награда за опыт
     * @param cashReward — награда за деньги
     * @return SendMessage — сообщение с результатом анализа
     */
    public SendMessage createAnalysisResultMessage(Long chatId, int expReward, int cashReward) {
        log.debug("MessageService: Создание результата анализа для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText(
                "*Итераториус:*\n\n" +
                "Вот это подход! Учиться через боль — зато запомнишь на всю жизнь. " +
                "Только не забывай, что в пятницу прод лучше не трогать.\n\n" +
                "_Видно, что ты читал JavaDoc, а не только переписывал код с StackOverflow._\n\n" +
                "*Навык повышен:*\n" +
                "  +" + expReward + " ⭐️ к Очкам Достижения\n" +
                "  +" + cashReward + " 💲 к Деньгам.");

        log.debug("MessageService: Результат анализа создан");
        return sendMessage;
    }

    /**
     * Создает сообщение о попытке анализа.
     *
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение об анализе
     */
    public SendMessage createAnalysisMessage(Long chatId) {
        log.debug("MessageService: Создание сообщения об анализе для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("📘 Ты вспоминаешь строки древнего манускрипта JavaDocs…\n\n" +
                "Ты сканируешь память — мозг работает на пределе.\n" +
                "*ArrayList — это просто массив.*\n" +
                "*Вставка в начало? Сдвиг, тормоза, страдания. Ты этого хочешь?!*\n\n" +
                "_Нанесен урон Aррейну 100(-50)\uD83D\uDD25_");


        log.debug("MessageService: Сообщение об анализе создано");
        return sendMessage;
    }



    /**
     * Создает сообщение о попытке вставки в начало.
     *
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о вставке
     */
    public SendMessage createInsertBeginningMessage(Long chatId) {
        log.debug("MessageService: Создание сообщения о вставке в начало для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("🚨 *Вставить в начало — ва-банк!*\n" +
                "🔥 _Аррейн получает урон 100(-50) — начинается частичный resize()_\n\n" +
                "Ты швыряешь элемент в начало списка — как камень в стеклянную крышу офиса.\n\n" +
                "*АРРЕЙОН (звереет):*\n" +
                "«Ты что, совсем страх потерял?! Я ТАК не работаю! Сейчас будет больно — тебе, мне и твоему менеджеру.»\n\n" +
                "_Массив трещит, но урон наносишь ты_");

        log.debug("MessageService: Сообщение о вставке в начало создано");
        return sendMessage;
    }

    /**
     * Создает сообщение с результатом вставки в начало.
     *
     * @param chatId      — ID чата пользователя
     * @param statChanges — изменения статов
     * @return SendMessage — сообщение с результатом
     */
    public SendMessage createInsertBeginningResultMessage(Long chatId, Map<String, Integer> statChanges) {
        log.debug("MessageService: Создание результата вставки в начало для chatId={}", chatId);

        UserEntity user = userService.getUserByTgId(chatId);
        if (user == null || user.getPersonage() == null) {
            log.warn("MessageService: Пользователь или персонаж не найден для chatId={}", chatId);
            return new SendMessage(chatId.toString(), "Ошибка: персонаж не найден");
        }

        PersonageEntity entity = user.getPersonage();
        String characterType = entity.getCharacterType();
        String individualStat = statService.getIndividualStatForCharacter(characterType);

        int achievementChange = statChanges.getOrDefault("achievement_points", 0);
        int moneyChange = statChanges.getOrDefault("currency", 0); // Исправлено: "money" -> "currency" 
        int individualChange = statChanges.getOrDefault(individualStat, 0);

        String individualStatInfo = statService.getStatDisplayInfo(individualStat);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус:*\n\n" +
                "Смело, но немного безрассудно. Главное, чтобы прод это не увидел.\n" +
                "Иногда даже идиотизм — это стратегия.\n\n" +
                "*Навык повышен:*\n" +
                " +" + achievementChange + " ⭐️ к Очкам Достижения\n" +
                " -" + Math.abs(moneyChange) + " 💲 к Деньгам (за психотерапевта позже)\n" +
                " +" + individualChange + " " + individualStatInfo + "\n"
        );
        log.debug("MessageService: Результат вставки в начало создан для chatId={}, тип персонажа: {}, индивидуальный стат: {}",
                chatId, characterType, individualStat);
        return sendMessage;
    }

    /**
     * Создает сообщение с атакой.
     *
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение с атакой
     */
    public SendMessage createAttackMessage(Long chatId) {
        log.debug("MessageService: Создание сообщения с атакой для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("💥 _Аррейн бросает в тебя виртуальный элемент с индексом 0!_\n\n" +
                "```java\n" +
                "ArrayList<String> list = new ArrayList<>();\n" +
                "list.add(\"Bug\");  // <- ВОТ ЭТО ЛЕТИТ В ТЕБЯ!\n" +
                "list.add(\"Error\");\n" +
                "list.add(\"Exception\");\n\n" +
                "```" +
                "🧠 *Итераториус*_(шепчет_): Аррейн пытается добавить баг в начало списка!\n" +
                "У тебя есть доля секунды. Реагируй!");
        
        // Добавляем клавиатуру с кнопками боевых действий
        sendMessage.setReplyMarkup(KeyboardReam.BattlArreyn(chatId));

        log.debug("MessageService: Сообщение с атакой создано");
        return sendMessage;
    }


    /**
     * Создает сообщение о завершении раунда 1.
     *
     * @param chatId — ID чата пользователя
     * @return SendMessage — сообщение о завершении раунда
     */
    public SendMessage endOfRoundOne(Long chatId) {
        log.debug("MessageService: Создание сообщения с завершением раунда 1 для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("⏳ *Раунд 1 завершён*\n\n" +
                "*Итераториус* хмурится, а где-то в логе мелькает баг из его прошлого проекта... \n\n" +
                "_Аррейон всё ещё стоит.\n" +
                "Но его структура… дала трещину._");
        
        log.debug("MessageService: Сообщение о завершении раунда 1 создано для chatId={}", chatId);
        return sendMessage;
    }


}