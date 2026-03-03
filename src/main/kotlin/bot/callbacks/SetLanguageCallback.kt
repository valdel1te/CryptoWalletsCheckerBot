package bot.callbacks

import bot.sendWelcomeMessage
import bot.states.BotState
import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import di
import model.Localizer
import model.services.UserService
import org.kodein.di.instance
import kotlin.getValue

/**
 * set language
 *
 * arg 0 - language
 * arg 1 - show welcome message flag
 *  */
suspend fun BehaviourContextWithFSM<BotState>.setLanguageCallback(messageDataCallbackQuery: MessageDataCallbackQuery, args: List<String>) {
    val userService: UserService by di.instance()
    val localizer: Localizer by di.instance()

    val message = messageDataCallbackQuery.message
    val language = args[0]
    val showWelcomeMessage = if (args.size == 2) args[1].toBoolean() else false
    val user = userService.getByTgId(message.chat.id.chatId.long) ?: return
    if (user.config.language != language) {
        user.config.language = language
        userService.update(user)
    }

    editMessageText(
        chatId = message.chat.id,
        messageId = message.messageId,
        text = localizer.getText("languageUpdated", user),
    )

    if (showWelcomeMessage)
        sendWelcomeMessage(user)
}