package bot.fsm

import bot.events.EventBus
import bot.messages.BotMessageService
import bot.messages.getErrorMessageData
import bot.messages.getSuccessfullyChangedTokenMessageData
import bot.updates.chatIdOrNull
import bot.updates.textContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import model.services.UserService

class EditTokenDataStateEvent(
    private val eventBus: EventBus,
    private val messageService: BotMessageService,
    private val userService: UserService,
) : State {
    override fun canHandle(update: Update): Boolean {
        val tgId = (update as MessageUpdate).chatIdOrNull ?: return false
        val user = userService.getByTgId(tgId) ?: return false

        return super.canHandle(update) && user.state == UserState.EditTokenData
    }

    override suspend fun handle(messageUpdate: MessageUpdate) {
        val tgId = messageUpdate.chatIdOrNull ?: return
        val user = userService.getByTgId(tgId) ?: return
        val textContent = messageUpdate.textContent ?: return

        val tokenData = textContent.split("-")
        if (tokenData.size != 2) {
            val messageData = getErrorMessageData(user, "invalid args")
            eventBus.publish(messageService.createTextMessage(messageData))
            return
        }

        val messageData = getSuccessfullyChangedTokenMessageData(user)
        //TODO: нужно развить стейты до json чтобы корректно отредачить нужный токен

        userService.changeUserState(user, UserState.Default)
    }
}