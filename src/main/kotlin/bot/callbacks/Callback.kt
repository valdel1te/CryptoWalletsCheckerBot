package bot.callbacks

import bot.BotEvent
import bot.updates.isCallback
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update

interface Callback : BotEvent {
    override fun canHandle(update: Update): Boolean {
        return update.isCallback
    }

    override suspend fun handle(update: Update) {
        handle(update as CallbackQueryUpdate)
    }

    suspend fun handle(callbackUpdate: CallbackQueryUpdate)
}