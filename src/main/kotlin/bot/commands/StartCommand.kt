package bot.commands

import bot.events.EventBus
import bot.fsm.UserState
import bot.messages.BotMessageService
import bot.messages.MessageData
import bot.messages.getLanguageSettingMessageData
import bot.messages.getWelcomeMessageData
import bot.updates.chatIdOrNull
import bot.updates.hasCommand
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import model.services.ProfileService
import model.services.UserService

class StartCommand(
    private val eventBus: EventBus,
    private val messageService: BotMessageService,
    private val userService: UserService,
    private val profileService: ProfileService,
) : Command {
    override fun canHandle(update: Update): Boolean {
        return super.canHandle(update) && (update as MessageUpdate).hasCommand("/start")
    }

    override suspend fun handle(messageUpdate: MessageUpdate) {
        val tgId = messageUpdate.chatIdOrNull ?: return
        val user = userService.getByTgId(tgId)

        val messageData: MessageData
        if (user != null) {
            userService.changeUserState(user, UserState.Default)
            val profiles = profileService.getUserProfiles(user)
            val balances = profileService.getProfilesBalances(user, profiles)
            messageData = getWelcomeMessageData(user, balances)
        } else {
            userService.create(tgId)
            profileService.addNew(userService.getByTgId(tgId)!!, "Profile 1")
            messageData = getLanguageSettingMessageData(tgId)
        }

        val request = messageService.createTextMessage(messageData)
        eventBus.publish(request)
    }
}