package bot.commands

import bot.sendLanguageSettingMessage
import bot.sendWelcomeMessage
import bot.states.BotState
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.types.message.content.TextMessage
import di
import model.Localizer
import model.services.UserService
import org.kodein.di.instance

suspend fun BehaviourContextWithFSM<BotState>.startCommand(textMessage: TextMessage) {
    val userService: UserService by di.instance()
    val localizer: Localizer by di.instance()

    val tgId = textMessage.chat.id.chatId.long
    val user = userService.getByTgId(tgId)
    if (user != null) {
        sendWelcomeMessage(user)
        return
    }

    userService.create(tgId)
    sendLanguageSettingMessage(tgId)
}