package bot.handlers

import bot.callbacks.ECallbackData
import bot.callbacks.setLanguageCallback
import bot.callbacks.showSettingsCallback
import bot.states.BotState
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMessageDataCallbackQuery

class CallbackHandler : Handler {
    override suspend fun register(context: BehaviourContextWithFSM<BotState>): Unit =
        with(context) {
            onMessageDataCallbackQuery { callbackDataWithArgs ->
                val tokens = callbackDataWithArgs.data.split(":")
                val callbackName = tokens[0]
                val args = tokens.drop(1)

                when (callbackName) {
                    ECallbackData.SET_LANGUAGE.data -> setLanguageCallback(callbackDataWithArgs, args)
                    ECallbackData.SHOW_SETTINGS.data -> showSettingsCallback(callbackDataWithArgs)
                    else -> return@onMessageDataCallbackQuery
                }
            }
        }
}