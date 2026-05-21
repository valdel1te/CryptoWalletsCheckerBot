package bot

import dev.inmo.tgbotapi.types.update.abstracts.Update
import org.slf4j.LoggerFactory

class EventHandler(
    private val events: List<BotEvent>,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    suspend fun handleUpdate(update: Update) {
        runCatching {
            val event = events.firstOrNull { it.canHandle(update) } ?: return
            event.handle(update)
        }.onFailure {
            logger.error(it.message)
        }
    }
}