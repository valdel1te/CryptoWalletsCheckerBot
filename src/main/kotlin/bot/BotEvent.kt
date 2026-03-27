package bot

import dev.inmo.tgbotapi.types.update.abstracts.Update

interface BotEvent {
    fun canHandle(update: Update): Boolean
    suspend fun handle(update: Update)
}