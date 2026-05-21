package bot.commands

import bot.BotEvent
import bot.updates.isMessage
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update

interface Command : BotEvent {
    override fun canHandle(update: Update): Boolean {
        return update.isMessage
    }

    override suspend fun handle(update: Update) {
        handle(update as MessageUpdate)
    }

    suspend fun handle(messageUpdate: MessageUpdate)
}