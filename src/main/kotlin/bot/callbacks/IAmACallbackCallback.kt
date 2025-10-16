package bot.callbacks

import bot.states.BotState
import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery

suspend fun BehaviourContextWithFSM<BotState>.iAmACallbackCallback(messageDataCallbackQuery: MessageDataCallbackQuery) {
    val message = messageDataCallbackQuery.message
    editMessageText(
        chatId = message.chat.id,
        messageId = message.messageId,
        text = "i am a callback",
    )
}