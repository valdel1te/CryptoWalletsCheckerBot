package bot.commands

import bot.events.EventBus
import bot.fsm.UserState
import bot.messages.BotMessageService
import bot.messages.getConfigSettingsMessageData
import bot.updates.chatIdOrNull
import bot.updates.hasCommand
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import model.services.UserService

class SettingsCommand(
    private val eventBus: EventBus,
    private val messageService: BotMessageService,
    private val userService: UserService,
) : Command {
    override fun canHandle(update: Update): Boolean {
        return super.canHandle(update) && (update as MessageUpdate).hasCommand("/settings")
    }

    override suspend fun handle(messageUpdate: MessageUpdate) {
        val tgId = messageUpdate.chatIdOrNull ?: return
        val user = userService.getByTgId(tgId) ?: return

        userService.changeUserState(user, UserState.Default)

        val messageData = getConfigSettingsMessageData(user)
        eventBus.publish(messageService.createTextMessage(messageData))
    }
}
