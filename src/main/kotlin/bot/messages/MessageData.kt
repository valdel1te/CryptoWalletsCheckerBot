package bot.messages

import bot.Bot.Companion.MAX_CHAINS_COUNT
import bot.callbacks.*
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

fun getErrorMessageData(user: User, error: String): MessageData {
    return MessageData(
        chatId = user.tgId.toChatId(),
        text = "error: $error"
    )
}

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
            ▪︎ ${ethChain.name} ($tokenCount token${if (tokenCount != 1) "s" else ""}) → RPC ${ethChain.rpcUrl}
            """
        }
    }
        *sol*:
            ▪︎ $solTokensCount token${if (solTokensCount > 1) "s" else ""} → RPC ${solConfig.rpcUrl}
            
        *ton*:
            ▪︎ $tonTokensCount token${if (tonTokensCount > 1) "s" else ""}

    """.trimIndent()

    val showLanguageSettingsCallbackData = ShowLanguageSettingsCallbackData
    val showEthSettingsCallbackData = ShowEthSettingsCallbackData
    val showSettingsCallbackData = ShowSettingsCallbackData(resetSettings = true)
    val keyboard = inlineKeyboard {
        row {
            dataButton(
                localizer.getText("languageSetting", user),
                generateCallbackDataWithArgs(showLanguageSettingsCallbackData)
            )
        }
        row {
            dataButton("ETH", generateCallbackDataWithArgs(showEthSettingsCallbackData))
            dataButton("(not available) sol settings", "s")
            dataButton("(not available) ton settings", "t")
        }
        row {
            dataButton(
                localizer.getText("resetSettings", user),
                generateCallbackDataWithArgs(showSettingsCallbackData)
            )
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

fun getEthSettingsMessageData(user: User): MessageData {
    val ethConfig = user.config.eth
    val rpcRows = ethConfig.mapIndexed { index, ethChain ->
        val tokenCount = ethChain.tokens.size
        val text =
            "${index + 1}. ${ethChain.name} ($tokenCount token${if (tokenCount != 1) "s" else ""}) → RPC ${ethChain.rpcUrl}"
        val chainControlCallbackData = ChainControlCallbackData(category = "eth", chainName = ethChain.name)
        val deleteChainControlCallbackData =
            ChainControlCallbackData(category = "eth", chainName = ethChain.name, removeChain = true)

        row {
            dataButton(text, generateCallbackDataWithArgs(chainControlCallbackData))
            dataButton("❌", generateCallbackDataWithArgs(deleteChainControlCallbackData))
        }
    }

    val showSettingsCallbackData = ShowSettingsCallbackData()
    val addChainControlCallbackData =
        ChainControlCallbackData(category = "eth", chainName = "New chain", addChain = true)
    val keyboard = inlineKeyboard {
        rpcRows.forEach { add(it) }

        if (rpcRows.size < MAX_CHAINS_COUNT)
            row {
                dataButton(
                    localizer.getText("addNewChain", user.config.language),
                    generateCallbackDataWithArgs(addChainControlCallbackData)
                )
            }

        add(getBackInlineButtonRow(localizer, user, showSettingsCallbackData))
    }

    return MessageData(
        chatId = user.tgId.toChatId(),
        text = localizer.getText("ethSettingsChoice", user.config.language),
        keyboard = keyboard
    )
}

fun getAddTokenMessageData(user: User, chainName: String): MessageData {
    val backCallbackData = ChainControlCallbackData(category = "eth", chainName = chainName)
    val keyboard = inlineKeyboard {
        add(getBackInlineButtonRow(localizer, user, backCallbackData))
    }

    return MessageData(
        chatId = user.tgId.toChatId(),
        text = localizer.getText("addTokenPrompt", user.config.language),
        keyboard = keyboard
    )
}

fun getSuccessfullyChangedTokenMessageData(user: User): MessageData {
    return MessageData(
        chatId = user.tgId.toChatId(),
        text = localizer.getText("successfullyChangedTokenMessageData", user.config.language)
    )
}

fun getEthChainSettingsMessageData(user: User, chainName: String): MessageData {
    val chain = user.config.eth.firstOrNull { it.name == chainName }
        ?: return getErrorMessageData(user, "error_chain_not_found")

    val text = """
        ▪︎ ${chain.name} → RPC ${chain.rpcUrl}
    """.trimIndent()

    val tokenRows = chain.tokens.mapIndexed { index, token ->
        val tokenText = "${index + 1}. ${token.symbols} ${token.address}"
        val editTokenCallbackData = TokenControlCallbackData(
            category = "eth",
            tokenName = token.symbols,
            chainName = chainName
        )
        val removeTokenCallbackData = TokenControlCallbackData(
            category = "eth",
            tokenName = token.symbols,
            chainName = chainName,
            removeToken = true
        )

        row {
            dataButton(tokenText, generateCallbackDataWithArgs(editTokenCallbackData))
            dataButton("❌", generateCallbackDataWithArgs(removeTokenCallbackData))
        }
    }

    val addTokenCallbackData = TokenControlCallbackData(
        category = "eth",
        tokenName = "",
        chainName = chainName,
        addToken = true
    )
    val showEthSettingsCallbackData = ShowEthSettingsCallbackData
    val keyboard = inlineKeyboard {
        tokenRows.forEach { add(it) }
        row {
            dataButton(
                localizer.getText("addNewToken", user.config.language),
                generateCallbackDataWithArgs(addTokenCallbackData)
            )
        }
        add(getBackInlineButtonRow(localizer, user, showEthSettingsCallbackData))
    }

    return MessageData(
        chatId = user.tgId.toChatId(),
        text = text,
        keyboard = keyboard
    )
}