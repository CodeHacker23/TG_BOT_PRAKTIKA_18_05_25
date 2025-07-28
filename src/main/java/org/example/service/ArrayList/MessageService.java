package org.example.service.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

/**
 * MessageService — сервис для создания сообщений в ArrayList истории.
 * <p>
 * Этот класс отвечает за:
 * - Создание боевых сообщений
 * - Создание сообщений с результатами
 * - Создание сообщений с наградами
 * - Форматирование текста сообщений
 * <p>
 * Зачем нужен:
 * - Вынес логику создания сообщений из огромного ArrayListBattleService
 * - Централизованное создание всех типов сообщений
 * - Единообразное форматирование
 * <p>
 * Автор: Архитектор (который знает, что сообщения без логики — это просто текст)
 * <p>
 * Пример использования:
 * 
 * @Autowired private MessageService messageService;
 * 
 * SendMessage result = messageService.createBattleResult(chatId, expReward, cashReward);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

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
        sendMessage.setText("🛡 Блокировать \n" +
                "(try-catch) - у меня была уже простроена.\n" +
                "Нанесен урон Айрену 100(-50)\n\n" +
                "Ты строишь стену из\n" +
                "``` try { ... } catch (...) { ... } ```\n" +
                "но Аррейон не из тех, кто уважает чужие перехваты.\n\n" +
                "Аррейон выносит с ноги твою защиту, как баги выносят прод после пятничного рефактора.\n\n" +
                "Ошибка: \n" +
                "ArrayIndexOutOfBoundsException\n" +
                "прорывает блок, словно нож сквозь масло.");
        
        log.debug("MessageService: Сообщение о защите try-catch создано");
        return sendMessage;
    }

    /**
     * Создает сообщение с результатом боя.
     * 
     * @param chatId — ID чата пользователя
     * @param expReward — награда за опыт
     * @param cashReward — награда за деньги
     * @return SendMessage — сообщение с результатом
     */
    public SendMessage createBattleResultMessage(Long chatId, int expReward, int cashReward) {
        log.debug("MessageService: Создание результата боя для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус:*\n\n" +
                "Молодец, конечно… Только try-catch не вечен.\n" +
                "Не всё в жизни ловится на костыли.\n" +
                "Следующий ход — только защита или анализ. Атаковать нельзя.\n\n" +
                "*Навык повышен:*\n" +
                "  +" + expReward + " ⭐️ к Очкам Достижения\n" +
                "  +" + cashReward + " 💲 к Деньгам.");
        
        log.debug("MessageService: Результат боя создан");
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
                "*Вставка в начало? Сдвиг, тормоза, страдания. Ты этого хочешь?!*");
        
        log.debug("MessageService: Сообщение об анализе создано");
        return sendMessage;
    }

    /**
     * Создает сообщение с результатом анализа.
     * 
     * @param chatId — ID чата пользователя
     * @param expReward — награда за опыт
     * @param cashReward — награда за деньги
     * @return SendMessage — сообщение с результатом анализа
     */
    public SendMessage createAnalysisResultMessage(Long chatId, int expReward, int cashReward) {
        log.debug("MessageService: Создание результата анализа для chatId={}", chatId);
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Итераториус:*\n\n" +
                "Вот это подход! Учиться через боль — зато запомнишь на всю жизнь.\n" +
                "Только не забывай, что в пятницу прод лучше не трогать.\n\n" +
                "Видно, что ты читал JavaDoc, а не только переписывал код с StackOverflow.\n\n" +
                "*Навык повышен:*\n" +
                "  +" + expReward + " ⭐️ к Очкам Достижения\n" +
                "  +" + cashReward + " 💲 к Деньгам.");
        
        log.debug("MessageService: Результат анализа создан");
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
        sendMessage.setText("🚨 Вставить в начало — ва-банк!\n" +
                "🔥 Аррейн получает урон 100(-50) — начинается частичный resize()\n\n" +
                "Ты швыряешь элемент в начало списка — как камень в стеклянную крышу офиса.\n\n" +
                "АРРЕЙОН (звереет):\n" +
                "«Ты что, совсем страх потерял?! Я ТАК не работаю! Сейчас будет больно — тебе, мне и твоему менеджеру.»\n\n" +
                "Массив трещит, но урон наносишь ты");
        
        log.debug("MessageService: Сообщение о вставке в начало создано");
        return sendMessage;
    }

    /**
     * Создает сообщение с результатом вставки в начало.
     * 
     * @param chatId — ID чата пользователя
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
        
        int achievementChange = statChanges.get("achievement_points");
        int moneyChange = statChanges.get("money");
        int individualChange = statChanges.get(individualStat);
        
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
                " +" + individualChange + " " + individualStatInfo);
        
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
        sendMessage.setText("💥 Аррейн бросает в тебя виртуальный элемент с индексом 0!\n\n" +
                "🧠 Итераториус (шепчет):\n" +
                "У тебя есть доля секунды. Реагируй!");
        
        log.debug("MessageService: Сообщение с атакой создано");
        return sendMessage;
    }
} 