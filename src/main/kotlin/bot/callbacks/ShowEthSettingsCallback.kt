package bot.callbacks

import bot.events.EventBus
import bot.messages.BotMessageService
import bot.messages.getEthSettingsMessageData
import bot.updates.chatIdOrNull
import bot.updates.getCallbackData
import bot.updates.messageId
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.services.UserService

@Serializable
@SerialName("show_eth_settings")
object ShowEthSettingsCallbackData : CallbackData

class ShowEthSettingsCallback(
    private val eventBus: EventBus,
    private val messageService: BotMessageService,
    private val userService: UserService,
) : Callback {
    override fun canHandle(update: Update): Boolean {
        return super.canHandle(update) && update.getCallbackData() is ShowEthSettingsCallbackData
    }

    override suspend fun handle(callbackUpdate: CallbackQueryUpdate) {
        val user = userService.getByTgId(callbackUpdate.chatIdOrNull ?: return) ?: return

        val messageData = getEthSettingsMessageData(user)
        val request = messageService.editTextMessage(messageData, callbackUpdate.messageId ?: return)
        eventBus.publish(request)
    }
}