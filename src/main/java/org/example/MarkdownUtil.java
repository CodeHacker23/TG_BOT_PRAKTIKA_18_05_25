package org.example;

/**
 * MarkdownUtil — твой спаситель от Telegram-ошибок форматирования.
 * Здесь живёт метод для экранирования всех спецсимволов MarkdownV2,
 * чтобы бот не ругался на точки, скобки и прочие пакости.
 *
 * Как использовать:
 *   String safe = MarkdownUtil.escapeMarkdownV2("Текст с *жирным* и _подчёркнутым_!");
 *   sendMessage.setText(safe);
 *
 * Юмор: если забудешь экранировать точку в спойлере — Telegram тебя накажет.
 */
public class MarkdownUtil {
    /**
     * Экранирует все спецсимволы MarkdownV2 согласно документации Telegram.
     * @param text — исходный текст
     * @return экранированный текст, готовый к отправке в Telegram
     *
     * Список символов:
     * _ * [ ] ( ) ~ ` > # + - = | { } . !
     *
     * Пример:
     *   String safe = MarkdownUtil.escapeMarkdownV2("*Hello* _world_.");
     *   // safe: \\*Hello\\* \\_world\\_.
     * Юмор: если забудешь экранировать точку — Telegram тебя накажет.
     */
    public static String escapeMarkdownV2(String text) {
        System.out.println("[MarkdownUtil] escapeMarkdownV2() — старт, входной текст: " + text);
        if (text == null) {
            System.out.println("[MarkdownUtil] escapeMarkdownV2() — входной текст null, возвращаем null");
            return null;
        }
        // Экранируем все спецсимволы по очереди
        String result = text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
        System.out.println("[MarkdownUtil] escapeMarkdownV2() — результат: " + result);
        return result;
    }

    /**
     * =========================
     * ПОЛНЫЙ МАНУАЛ ПО MARKDOWNV2 ДЛЯ ТЕЛЕГРАМ-БОТОВ (читай, кайфуй, не матерись)
     * =========================
     *
     * 0. TL;DR для самых нетерпеливых:
     *    — Управляющие символы (*, _, ~, ||, `, [ ], (, ), и т.д.) — руками!
     *    — Всё, что между ними — через escapeMarkdownV2()!
     *    — Если не понял — смотри примеры ниже и не ной.
     *
     * 1. Жирный (bold):
     *    String bold = "*" + escapeMarkdownV2("ТЕКСТ") + "*";
     *    // *ТЕКСТ*
     *
     * 2. Курсив (italic):
     *    String italic = "_" + escapeMarkdownV2("ТЕКСТ") + "_";
     *    // _ТЕКСТ_
     *
     * 3. Подчёркнутый (underline):
     *    String underline = "__" + escapeMarkdownV2("ТЕКСТ") + "__";
     *    // __ТЕКСТ__
     *
     * 4. Зачёркнутый (strikethrough):
     *    String strike = "~" + escapeMarkdownV2("ТЕКСТ") + "~";
     *    // ~ТЕКСТ~
     *
     * 5. Спойлер:
     *    String spoiler = "||" + escapeMarkdownV2("ТЕКСТ") + "||";
     *    // ||ТЕКСТ||
     *
     * 6. Моноспейс (inline code):
     *    String mono = "`" + escapeMarkdownV2("ТЕКСТ") + "`";
     *    // `ТЕКСТ`
     *
     * 7. Многострочный код (code block):
     *    String codeBlock = "```" + "\n" + escapeMarkdownV2("ТЕКСТ\nещё строка") + "\n```";
     *    // ```
     *    // ТЕКСТ
     *    // ещё строка
     *    // ```
     *
     * 8. Ссылка:
     *    String link = "[" + escapeMarkdownV2("текст ссылки") + "](https://example.com)";
     *    // [текст ссылки](https://example.com)
     *    // ВАЖНО: url не экранируется!
     *
     * 9. Списки:
     *    String list = "- " + escapeMarkdownV2("пункт 1") + "\n- " + escapeMarkdownV2("пункт 2");
     *    // - пункт 1
     *    // - пункт 2
     *
     * 10. Вложенные форматы (например, жирный+спойлер):
     *     String combo = "*||" + escapeMarkdownV2("секретный жирный текст") + "||*";
     *     // *||секретный жирный текст||*
     *
     * 11. Универсальный шаблон:
     *     String bold = "*" + escapeMarkdownV2("БайтФордж:") + "*";
     *     String spoiler = "||" + escapeMarkdownV2("Римскую проекцию Java-машины.") + "||";
     *     String body = escapeMarkdownV2("Это древний мир, где алгоритмы маскируются под воинов,\nа коллекции — под боевые порядки.\n\nДобро пожаловать в... ");
     *     String text = bold + "\n\n" + body + spoiler;
     *     sendMessage.setText(text);
     *
     * 12. Анти-примеры (как делать НЕ НАДО):
     *     // escapeMarkdownV2("*жирный*") — будет просто звёздочка, а не жирный!
     *     // escapeMarkdownV2("||спойлер||") — будет просто две палки, а не спойлер!
     *     // escapeMarkdownV2("[ссылка](url)") — будет просто текст, а не ссылка!
     *
     * 13. Если хочешь добавить новый формат — управляющие символы руками, содержимое через escapeMarkdownV2().
     *
     * 14. Если не понял — перечитай ещё раз, потом выпей кофе, потом спроси у Архитектора.
     *
     * 15. Если забыл экранировать точку в спойлере — Telegram тебя накажет (и не только точкой).
     *
     * 16. Если напишешь всё в одну строку без экранирования — баги вылезут из всех коллекций.
     *
     * 17. Если забудешь экранировать спецсимволы — Архитектор лично напишет тебе в Telegram (и не только).
     *
     * =========================
     *
     * Визуальный пример (копируй, вставляй, радуйся):
     *
     *   String wow = "*" + escapeMarkdownV2("Жирный") + "* " +
     *                "_" + escapeMarkdownV2("Курсив") + "_ " +
     *                "__" + escapeMarkdownV2("Подчёркнутый") + "__ " +
     *                "~" + escapeMarkdownV2("Зачёркнутый") + "~ " +
     *                "||" + escapeMarkdownV2("Спойлер") + "|| " +
     *                "`" + escapeMarkdownV2("Моноспейс") + "` " +
     *                "[" + escapeMarkdownV2("Ссылка") + "](https://example.com)\n" +
     *                "- " + escapeMarkdownV2("Список 1") + "\n- " + escapeMarkdownV2("Список 2") + "\n" +
     *                "*||" + escapeMarkdownV2("Жирный спойлер") + "||*";
     *
     * =========================
     *
     * Если после этого мануала ты всё равно словил ошибку Telegram — поздравляю, ты нашёл новый баг в Telegram!
     *
     * =========================
     */
} 