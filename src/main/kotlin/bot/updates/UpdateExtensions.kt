package bot.updates

import bot.callbacks.CallbackData
import bot.callbacks.callbackJson
import dev.inmo.tgbotapi.extensions.utils.asCallbackQueryUpdate
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.data
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.message
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.RiskFeature

val Update.chatIdOrNull: Long?
    get() = when (this) {
        is MessageUpdate -> data.chat.id.chatId.long
        is CallbackQueryUpdate -> data.from.id.chatId.long
        else -> null
    }

@OptIn(PreviewFeature::class)
val Update.isCallback: Boolean
    get() = asCallbackQueryUpdate() != null

@OptIn(PreviewFeature::class)
val Update.isMessage: Boolean
    get() = asMessageUpdate() != null

@OptIn(PreviewFeature::class, RiskFeature::class)
val Update.callbackData: String?
    get() = asCallbackQueryUpdate()?.data?.data

@OptIn(PreviewFeature::class)
val MessageUpdate.textContent: String?
    get() = data.asContentMessage()?.content?.asTextContent()?.text

@OptIn(RiskFeature::class)
val CallbackQueryUpdate.messageId: MessageId?
    get() = data.message?.messageId

fun MessageUpdate.hasCommand(command: String): Boolean {
    return textContent?.startsWith(command) == true
}

@OptIn(RiskFeature::class)
fun Update.getCallbackData(): CallbackData? {
    val raw = (this as? CallbackQueryUpdate)?.data?.data ?: return null
    return runCatching { callbackJson.decodeFromString<CallbackData>(raw) }.getOrNull()
}

@OptIn(RiskFeature::class)
fun CallbackQueryUpdate.getCallbackData(): CallbackData? {
    val raw = data.data ?: return null
    return runCatching { callbackJson.decodeFromString<CallbackData>(raw) }.getOrNull()
}

@OptIn(RiskFeature::class)
inline fun <reified T : CallbackData> CallbackQueryUpdate.getCallbackDataAs(): T {
    return callbackJson.decodeFromString<T>(data.data ?: "")
}
