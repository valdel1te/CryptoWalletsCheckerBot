package bot

import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.types.message.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.message.ParseMode

/**
 * Безопасная отправка сообщения
 *
 * Может помочь если, например, бот попытается отправить сообщение пользователю, который его заблокировал
 * @param onError что делать в случае ошибки, по умолчанию - игнорировать
 */
suspend fun BehaviourContext.sendMessageSafe(
    chatId: ChatIdentifier,
    text: String,
    parseMode: ParseMode = MarkdownV2ParseMode,
    replyMarkup: KeyboardMarkup? = null,
    onError: (Exception) -> Unit = {}
) {
    try {
        sendMessage(
            chatId = chatId,
            text = text,
            parseMode = parseMode,
            replyMarkup = replyMarkup
        )
    } catch (e: Exception) {
        onError(e)
    }
}

/** Отправка нескольких сообщений вместо одного, если текст превышает символьный лимит */
suspend fun BehaviourContext.sendLongMessages(chatId: ChatIdentifier, text: String) {
    val messagesTexts = splitMessageText(text)
    messagesTexts.forEach { messageText ->
        sendMessage(
            chatId = chatId,
            text = messageText,
            parseMode = MarkdownV2ParseMode
        )
    }
}

/**
 * Разбиение текста на несколько сообщений, если оно превышает лимит по символам
 *
 * @param text исходный текст
 * @param maxLength символьный лимит на одно сообщение, по умолчанию равен 4096
 * @return список текста из n частей для отправки n сообщений
 */
private fun splitMessageText(text: String, maxLength: Int = Bot.MAX_MESSAGE_LENGTH): List<String> {
    if (text.length <= maxLength) return listOf(text)

    val parts = mutableListOf<String>()
    val lines = text.lines()
    var currentPart = StringBuilder()

    for (line in lines) {
        if (currentPart.isNotEmpty() && currentPart.length + line.length + 1 > maxLength) {
            parts.add(currentPart.toString())
            currentPart = StringBuilder()
        }

        if (currentPart.isNotEmpty()) {
            currentPart.append("\n")
        }
        currentPart.append(line)

        if (currentPart.length > maxLength) {
            val chunked = currentPart.toString().chunked(maxLength)
            parts.addAll(chunked.dropLast(1))
            currentPart = StringBuilder(chunked.last())
        }
    }

    if (currentPart.isNotEmpty()) {
        parts.add(currentPart.toString())
    }

    return parts
}