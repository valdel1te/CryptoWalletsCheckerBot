package bot.commands

import bot.events.EventBus
import bot.messages.BotMessageService
import bot.messages.MessageData
import bot.messages.getErrorMessageData
import bot.updates.chatIdOrNull
import bot.updates.hasCommand
import bot.updates.textContent
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import model.services.ProfileService
import model.services.UserService

/** костыльная команда для mvp */
class AddAddressToProfile1Command(
    private val eventBus: EventBus,
    private val messageService: BotMessageService,
    private val userService: UserService,
    private val profileService: ProfileService,
) : Command {
    override fun canHandle(update: Update): Boolean {
        return super.canHandle(update) && (update as MessageUpdate).hasCommand("/addAddress")
    }

    override suspend fun handle(messageUpdate: MessageUpdate) {
        val tgId = messageUpdate.chatIdOrNull ?: return
        val user = userService.getByTgId(tgId) ?: return
        val splitContent = messageUpdate.textContent?.split(" ") ?: return

        val messageData: MessageData
        if (splitContent.size != 3) {
            messageData = getErrorMessageData(user, "need 2 args: 1 -> sol/eth/ton; 2 -> address")
        } else {
            val profile1 = profileService.getByUserAndName(user, "Profile 1")
            val chain = splitContent[1]
            val address = splitContent[2]

            when (chain) {
                "ETH", "eth", "SOL", "sol", "TON", "ton" -> {
                    profileService.addNewAddress(profile1, "${chain.lowercase()}:$address")
                    messageData = MessageData(chatId = tgId.toChatId(), text = "success")
                }

                else -> messageData = getErrorMessageData(user, "wrong chain name")
            }
        }

        val request = messageService.createTextMessage(messageData)
        eventBus.publish(request)
    }
}