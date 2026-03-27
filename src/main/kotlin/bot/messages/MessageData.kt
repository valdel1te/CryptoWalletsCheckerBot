package bot.messages

import bot.callbacks.SetLanguageCallbackData
import bot.callbacks.ShowLanguageSettingsCallbackData
import bot.callbacks.ShowSettingsCallbackData
import bot.callbacks.generateCallbackDataWithArgs
import data.User
import dev.inmo.tgbotapi.extensions.utils.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.ParseMode
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import di
import model.Localizer
import org.kodein.di.instance

data class MessageData(
    val chatId: ChatId,
    val text: String,
    val keyboard: KeyboardMarkup = InlineKeyboardMarkup(),
    val parseMode: ParseMode = MarkdownParseMode,
)

private val localizer: Localizer by di.instance()

fun getWelcomeMessageData(user: User): MessageData {
    val showSettingsCallbackData = ShowSettingsCallbackData()
    val keyboard = inlineKeyboard {
        row {
            dataButton(
                localizer.getText("settings", user),
                generateCallbackDataWithArgs(showSettingsCallbackData)
            )
        }
    }

    return MessageData(
        chatId = user.tgId.toChatId(),
        text = "WELCOME BROTHER", // TODO: welcome message text
        keyboard = keyboard
    )
}

fun getConfigSettingsMessageData(user: User): MessageData {
    val config = user.config
    val ethConfig = config.eth
    val solConfig = config.sol[0]
    val tonConfig = config.ton[0]
    val solTokensCount = solConfig.tokens.size
    val tonTokensCount = tonConfig.tokens.size

    val configString = """
        *language*: ${config.language.uppercase()}
        
        *eth chains*:
    ${
        ethConfig.joinToString("") { ethChain ->
            val tokenCount = ethChain.tokens.size
            """
            ▪︎ ${ethChain.name} ($tokenCount token${if (tokenCount > 1) "s" else ""}) → RPC ${ethChain.rpcUrl}
            """
        }
    }
        *sol*:
            ▪︎ $solTokensCount token${if (solTokensCount > 1) "s" else ""} → RPC ${solConfig.rpcUrl}
            
        *ton*:
            ▪︎ $tonTokensCount token${if (tonTokensCount > 1) "s" else ""}

    """.trimIndent()

    val showLanguageSettingsCallbackData = ShowLanguageSettingsCallbackData()
    val keyboard = inlineKeyboard {
        row {
            dataButton(
                localizer.getText("languageSetting", user),
                generateCallbackDataWithArgs(showLanguageSettingsCallbackData)
            )
        }
        row {
            dataButton("eth settings", "e")
            dataButton("sol settings", "s")
            dataButton("ton settings", "t")
        }
    }

    return MessageData(
        chatId = user.tgId.toChatId(),
        text = configString,
        keyboard = keyboard,
        parseMode = MarkdownParseMode
    )
}

fun getLanguageSettingMessageData(tgId: Long): MessageData {
    val setRuLanguageCallbackData = SetLanguageCallbackData(language = "ru", showWelcomeMessage = true)
    val setEnLanguageCallbackData = SetLanguageCallbackData(language = "en", showWelcomeMessage = true)

    return MessageData(
        chatId = tgId.toChatId(),
        text = "${localizer.getText("chooseLanguage", "ru")} / ${localizer.getText("chooseLanguage", "en")}",
        keyboard = inlineKeyboard {
            row {
                dataButton(
                    localizer.getText("language", "ru"),
                    generateCallbackDataWithArgs(setRuLanguageCallbackData)
                )
                dataButton(
                    localizer.getText("language", "en"),
                    generateCallbackDataWithArgs(setEnLanguageCallbackData)
                )
            }
        }
    )
}