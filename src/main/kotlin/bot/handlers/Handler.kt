package bot.handlers

import bot.states.BotState
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface Handler {
    /** Обработка ивентов
     *
     * @param context контекст ивента
     * */
    suspend fun register(context: BehaviourContextWithFSM<BotState>)

    /** Логгер */
    val logger: Logger get() = LoggerFactory.getLogger(this::class.java)
}