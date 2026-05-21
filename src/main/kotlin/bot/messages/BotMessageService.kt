package bot.messages

import dev.inmo.tgbotapi.requests.abstracts.Request
import dev.inmo.tgbotapi.requests.edit.text.EditChatMessageText
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup


class BotMessageService {
    fun createTextMessage(messageData: MessageData): Request<*> {
        return SendTextMessage(
            chatId = messageData.chatId,
            text = messageData.text,
            parseMode = messageData.parseMode,
            replyMarkup = messageData.keyboard
        )
    }

    fun editTextMessage(messageData: MessageData, messageId: MessageId): Request<*> {
        return EditChatMessageText(
            messageId = messageId,
            chatId = messageData.chatId,
            text = messageData.text,
            parseMode = messageData.parseMode,
            replyMarkup = messageData.keyboard as InlineKeyboardMarkup
        )
    }
}