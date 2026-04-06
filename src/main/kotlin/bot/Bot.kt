package bot

import dev.inmo.micro_utils.coroutines.subscribe
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import io.ktor.utils.io.*
import org.slf4j.LoggerFactory

class Bot(
    private val telegramBot: TelegramBot,
    private val handler: EventHandler,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    suspend fun startWithFSMAndLongPolling() {
        telegramBot.buildBehaviourWithLongPolling(
            defaultExceptionsHandler = { exception ->
                if (exception !is CancellationException)
                    logger.error(exception.message)
            }
        ) {
            logger.info("Bot started")
            allUpdatesFlow.subscribe(this) { update ->
                handler.handleUpdate(update)
            }
        }.join()
    }

    companion object {
        const val MAX_MESSAGE_LENGTH = 4096
        const val MAX_CALLBACK_DATA_LENGTH_BYTES = 64
        const val MAX_CHAINS_COUNT = 30

        val availableCategories = listOf("eth", "sol", "ton")
    }
}