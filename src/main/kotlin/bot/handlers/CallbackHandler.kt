package bot.handlers

import bot.callbacks.ECallbackData
import bot.callbacks.iAmACallbackCallback
import bot.states.BotState
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMessageDataCallbackQuery

class CallbackHandler : Handler {
    override suspend fun register(context: BehaviourContextWithFSM<BotState>): Unit =
        with(context) {
            /** IAmACallback example */
            onMessageDataCallbackQuery(ECallbackData.I_AM_A_CALLBACK.data) {
                iAmACallbackCallback(it)
            }
        }
}