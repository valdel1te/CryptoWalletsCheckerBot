package bot.commands

import bot.sendConfigSettingsMessage
import bot.states.BotState
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.types.message.content.TextMessage
import di
import model.services.UserService
import org.kodein.di.instance
import kotlin.getValue

suspend fun BehaviourContextWithFSM<BotState>.settingsCommand(textMessage: TextMessage) {
    val userService: UserService by di.instance()

    val tgId = textMessage.chat.id.chatId.long
    val user = userService.getByTgId(tgId) ?: return

    sendConfigSettingsMessage(user)
}
