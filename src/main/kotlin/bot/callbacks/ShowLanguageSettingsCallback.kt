package bot.callbacks

import bot.events.EventBus
import bot.messages.BotMessageService
import bot.messages.getLanguageSettingMessageData
import bot.updates.chatIdOrNull
import bot.updates.getCallbackData
import bot.updates.messageId
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("show_lang_settings")
object ShowLanguageSettingsCallbackData : CallbackData

class ShowLanguageSettingsCallback(
    private val eventBus: EventBus,
    private val messageService: BotMessageService,
) : Callback {
    override fun canHandle(update: Update): Boolean {
        if (!super.canHandle(update))
            return false

        return update.getCallbackData() is ShowLanguageSettingsCallbackData
    }

    override suspend fun handle(callbackUpdate: CallbackQueryUpdate) {
        val tgId = callbackUpdate.chatIdOrNull ?: return
        val messageData = getLanguageSettingMessageData(tgId)
        eventBus.publish(messageService.editTextMessage(messageData, callbackUpdate.messageId ?: return))
    }
}
