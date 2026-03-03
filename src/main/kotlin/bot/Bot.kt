package bot

import bot.handlers.Handler
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithFSMAndStartLongPolling
import io.ktor.utils.io.*
import org.slf4j.LoggerFactory

class Bot(
    private val handlers: List<Handler>,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    suspend fun startWithFSMAndLongPolling(token: String) {
        val bot = telegramBot(token)

        bot.buildBehaviourWithFSMAndStartLongPolling(
            defaultExceptionsHandler = { exception ->
                if (exception !is CancellationException)
                    logger.error(exception.message)
            }
        ) {
            logger.info("Bot started")

            handlers.forEach { handler ->
                handler.register(this)
            }
        }.join()
    }

    companion object {
        const val MAX_MESSAGE_LENGTH = 4096
    }
}