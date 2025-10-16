package bot.handlers

import bot.states.BotState
import bot.states.WelcomeState
import bot.states.welcomeState
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.strictlyOn

class StatesHandler : Handler {
    override suspend fun register(context: BehaviourContextWithFSM<BotState>): Unit =
        with(context) {
            /** WelcomeState example */
            strictlyOn { state: WelcomeState ->
                welcomeState(state)
                logger.info("new user here -> ${state.context.chatId}")
                null // выход из состояния
            }
        }
}