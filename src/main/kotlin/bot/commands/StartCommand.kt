package bot.commands

import bot.callbacks.ECallbackData
import bot.states.BotState
import bot.states.WelcomeState
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.webAppButton
import dev.inmo.tgbotapi.types.message.content.TextMessage
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import dev.inmo.tgbotapi.utils.row

suspend fun BehaviourContextWithFSM<BotState>.startCommand(textMessage: TextMessage) {
    sendMessage(
        chatId = textMessage.chat.id,
        text = "welcome!",
        replyMarkup = inlineKeyboard {
            row {
                webAppButton("webapp", WebAppInfo("https://maliciously-pipy-lezlie.ngrok-free.dev"))
            }
            row {
                dataButton("i am a callback", ECallbackData.I_AM_A_CALLBACK.data)
            }
        }
    )
    startChain(WelcomeState(textMessage.chat.id))
}