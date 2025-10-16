package bot.states

import dev.inmo.micro_utils.fsm.common.State
import dev.inmo.tgbotapi.types.IdChatIdentifier

/**
 * Состояния пользователей в боте
 *
 * Обязательное наличие контекста, в нашем случае это чат айди
 *
 * В data классы помимо контекста можно добавлять еще поля, например, json параметры
 * */
sealed interface BotState : State {
    override val context: IdChatIdentifier
    val name: EUserStates
}

enum class EUserStates {
    /* Пример */
    WELCOME
}

data class WelcomeState(
    override val context: IdChatIdentifier,
    override val name: EUserStates = EUserStates.WELCOME,
) : BotState