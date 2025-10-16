package bot.handlers

import bot.commands.ECommandNames
import bot.commands.startCommand
import bot.states.BotState
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand

class CommandHandler : Handler {
    override suspend fun register(context: BehaviourContextWithFSM<BotState>): Unit =
        with(context) {
            /** StartCommand example */
            onCommand(ECommandNames.START.data) {
                startCommand(it)
            }
        }
}