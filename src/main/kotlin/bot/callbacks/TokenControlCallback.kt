package bot.callbacks

import bot.Bot.Companion.availableCategories
import bot.events.EventBus
import bot.messages.BotMessageService
import bot.messages.getErrorMessageData
import bot.messages.getEthChainSettingsMessageData
import bot.messages.getEthSettingsMessageData
import bot.updates.chatIdOrNull
import bot.updates.getCallbackData
import bot.updates.getCallbackDataAs
import bot.updates.messageId
import data.EthChain
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.services.ConfigService
import model.services.UserService

@Serializable
@SerialName("token_control")
data class TokenControlCallbackData(
    @SerialName("c")
    val category: String,

    @SerialName("t")
    val tokenName: String,

    @SerialName("cn")
    val chainName: String,

    @SerialName("a_c")
    val addToken: Boolean = false,

    @SerialName("r_c")
    val removeToken: Boolean = false,
) : CallbackData

class TokenControlCallback(
    private val eventBus: EventBus,
    private val messageService: BotMessageService,
    private val userService: UserService,
    private val configService: ConfigService,
) : Callback {

    override fun canHandle(update: Update): Boolean {
        if (!super.canHandle(update))
            return false

        val data = update.getCallbackData() ?: return false

        return data is TokenControlCallbackData && availableCategories.contains(data.category.lowercase())
    }

    override suspend fun handle(callbackUpdate: CallbackQueryUpdate) {
        val user = userService.getByTgId(callbackUpdate.chatIdOrNull ?: return) ?: return

        val data = callbackUpdate.getCallbackDataAs<TokenControlCallbackData>()
        val messageData = when (data.category) {
            "eth" -> {
                if (data.addToken) {
                    user.config.eth =
                        user.config.eth
                    userService.update(user)
                    getEthSettingsMessageData(user)
                } else if (data.removeToken) {
                    configService.deleteEthChainByName(user, data.tokenName) { user -> userService.update(user) }
                    getEthSettingsMessageData(user)
                } else
                    getEthChainSettingsMessageData(user, data.tokenName)
            }

            else -> getErrorMessageData(user, "category_not_found")
        }

        val request = messageService.editTextMessage(messageData, callbackUpdate.messageId ?: return)
        eventBus.publish(request)
    }
}