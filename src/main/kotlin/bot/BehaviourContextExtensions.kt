package bot

import bot.callbacks.ECallbackData
import bot.callbacks.generateCallbackDataWithArgs
import data.User
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import di
import model.Localizer
import org.kodein.di.instance
import kotlin.getValue

suspend fun BehaviourContext.sendWelcomeMessage(user: User) {
    val localizer: Localizer by di.instance()

    val keyboard = inlineKeyboard {
        row {
            dataButton(
                localizer.getText("settings", user),
                generateCallbackDataWithArgs(ECallbackData.SHOW_SETTINGS, listOf())
            )
        }
    }

    sendMessage(
        chatId = user.tgId.toChatId(),
        text = "WELCOME BROTHER", // TODO: welcome message text
        replyMarkup = keyboard
    )
}

suspend fun BehaviourContext.sendConfigSettingsMessage(user: User) {
    val localizer: Localizer by di.instance()

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

    val keyboard = inlineKeyboard { // TODO: keyboard text locales + style
        row {
            dataButton(localizer.getText("languageSetting", user), "l")
        }
        row {
            dataButton("eth settings", "e")
            dataButton("sol settings", "s")
            dataButton("ton settings", "t")
        }
    }

    sendMessage(
        chatId = user.tgId.toChatId(),
        text = configString,
        parseMode = MarkdownParseMode,
        replyMarkup = keyboard
    )
}

suspend fun BehaviourContext.sendLanguageSettingMessage(tgId: Long) {
    val localizer: Localizer by di.instance()

    sendMessage(
        chatId = tgId.toChatId(),
        text = "${localizer.getText("chooseLanguage", "ru")} / ${localizer.getText("chooseLanguage", "en")}",
        replyMarkup = inlineKeyboard {
            row {
                dataButton(
                    localizer.getText("language", "ru"),
                    generateCallbackDataWithArgs(ECallbackData.SET_LANGUAGE, listOf("ru", "true"))
                )
                dataButton(
                    localizer.getText("language", "en"),
                    generateCallbackDataWithArgs(ECallbackData.SET_LANGUAGE, listOf("en", "true"))
                )
            }
        }
    )
}