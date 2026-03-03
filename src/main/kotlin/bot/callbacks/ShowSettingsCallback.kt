package bot.callbacks

import bot.sendConfigSettingsMessage
import bot.states.BotState
import dev.inmo.tgbotapi.extensions.api.deleteMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import di
import model.services.UserService
import org.kodein.di.instance

/**
 * set language
 *
 * no args
 *  */
suspend fun BehaviourContextWithFSM<BotState>.showSettingsCallback(messageDataCallbackQuery: MessageDataCallbackQuery) {
    val userService: UserService by di.instance()

    val message = messageDataCallbackQuery.message
    val user = userService.getByTgId(message.chat.id.chatId.long) ?: return

    deleteMessage(message)
    sendConfigSettingsMessage(user)
}