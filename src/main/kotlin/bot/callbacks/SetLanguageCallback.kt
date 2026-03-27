package bot.callbacks

import bot.events.EventBus
import bot.messages.BotMessageService
import bot.messages.MessageData
import bot.messages.getWelcomeMessageData
import bot.updates.chatIdOrNull
import bot.updates.getCallbackData
import bot.updates.getCallbackDataAs
import bot.updates.messageId
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.Localizer
import model.services.UserService

@Serializable
@SerialName("set_language")
data class SetLanguageCallbackData(
    val language: String,
    val showWelcomeMessage: Boolean,
) : CallbackData

class SetLanguageCallback(
    private val eventBus: EventBus,
    private val messageService: BotMessageService,
    private val userService: UserService,
    private val localizer: Localizer,
) : Callback {
    override fun canHandle(update: Update): Boolean {
        if (!super.canHandle(update))
            return false
        val data = update.getCallbackData() ?: return false

        return data is SetLanguageCallbackData && data.language in Localizer.SUPPORTED_LANGUAGES
    }

    override suspend fun handle(callbackUpdate: CallbackQueryUpdate) {
        val user = userService.getByTgId(callbackUpdate.chatIdOrNull ?: return) ?: return
        val data = callbackUpdate.getCallbackDataAs<SetLanguageCallbackData>()
        val language = data.language

        if (user.config.language != language) {
            user.config.language = language
            userService.update(user)
        }

        val messageData = MessageData(
            chatId = user.tgId.toChatId(),
            text = localizer.getText("languageUpdated", user)
        )
        eventBus.publish(messageService.editTextMessage(messageData, callbackUpdate.messageId ?: return))

        if (data.showWelcomeMessage) {
            val messageData = getWelcomeMessageData(user)
            eventBus.publish(messageService.createTextMessage(messageData))
        }
    }
}