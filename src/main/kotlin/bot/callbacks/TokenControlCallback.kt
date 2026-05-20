package bot.callbacks

import bot.Bot.Companion.availableCategories
import bot.events.EventBus
import bot.fsm.UserState
import bot.messages.BotMessageService
import bot.messages.getAddTokenMessageData
import bot.messages.getErrorMessageData
import bot.messages.getEthChainSettingsMessageData
import data.Token
import bot.updates.chatIdOrNull
import bot.updates.getCallbackData
import bot.updates.getCallbackDataAs
import bot.updates.messageId

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

private const val DEFAULT_TOKEN_SYMBOLS = "TOKEN"
private const val DEFAULT_TOKEN_ADDRESS = "0x0000000000000000000000000000000000000000"

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
                if (data.removeToken) {
                    configService.deleteTokenBySymbols(
                        chains = user.config.eth,
                        chainName = data.chainName,
                        tokenSymbols = data.tokenName,
                        updateChains = { user.config.eth = it },
                        onSuccess = { userService.update(user) }
                    )
                    getEthChainSettingsMessageData(user, data.chainName)
                } else if (data.addToken) {
                    val newToken = Token(symbols = DEFAULT_TOKEN_SYMBOLS, address = DEFAULT_TOKEN_ADDRESS)
                    user.config.eth = user.config.eth.map { chain ->
                        if (chain.name == data.chainName) chain.copy(tokens = chain.tokens + newToken)
                        else chain
                    }
                    userService.update(user)
                    getEthChainSettingsMessageData(user, data.chainName)
                } else {
                    userService.changeUserState(user, UserState.EditTokenData)
                    getAddTokenMessageData(user, data.chainName)
                }
            }

            else -> getErrorMessageData(user, "category_not_found")
        }

        val request = messageService.editTextMessage(messageData, callbackUpdate.messageId ?: return)
        eventBus.publish(request)
    }
}