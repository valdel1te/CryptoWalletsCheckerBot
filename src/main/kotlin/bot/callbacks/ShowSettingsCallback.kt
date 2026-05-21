package bot.callbacks

import bot.events.EventBus
import bot.messages.BotMessageService
import bot.messages.getConfigSettingsMessageData
import bot.updates.chatIdOrNull
import bot.updates.getCallbackData
import bot.updates.getCallbackDataAs
import bot.updates.messageId
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.services.ConfigService
import model.services.UserService

@Serializable
@SerialName("show_settings")
data class ShowSettingsCallbackData(
    @SerialName("r")
    val resetSettings: Boolean = false
) : CallbackData

class ShowSettingsCallback(
    private val eventBus: EventBus,
    private val messageService: BotMessageService,
    private val userService: UserService,
    private val configService: ConfigService,
) : Callback {
    override fun canHandle(update: Update): Boolean {
        if (!super.canHandle(update))
            return false

        return update.getCallbackData() is ShowSettingsCallbackData
    }

    override suspend fun handle(callbackUpdate: CallbackQueryUpdate) {
        val user = userService.getByTgId(callbackUpdate.chatIdOrNull ?: return) ?: return

        val data = callbackUpdate.getCallbackDataAs<ShowSettingsCallbackData>()
        if (data.resetSettings) {
            user.config = configService.getDefaultConfig()
            userService.update(user)
        }

        val messageData = getConfigSettingsMessageData(user)
        val request = messageService.editTextMessage(messageData, callbackUpdate.messageId ?: return)
        eventBus.publish(request)
    }
}