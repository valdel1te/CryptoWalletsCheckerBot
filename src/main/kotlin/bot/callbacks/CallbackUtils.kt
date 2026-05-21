package bot.callbacks

import bot.Bot.Companion.MAX_CALLBACK_DATA_LENGTH_BYTES
import data.User
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.InlineKeyboardButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import model.Localizer

@Serializable
sealed interface CallbackData

fun generateCallbackDataWithArgs(data: CallbackData): String {
    val data = callbackJson.encodeToString(data)
    val size = data.toByteArray().size

    if (MAX_CALLBACK_DATA_LENGTH_BYTES <= size)
        return "error:too_big_data_size"

    return data
}

val callbackJson = Json {
    classDiscriminator = "n"
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        polymorphic(CallbackData::class) {
            subclass(SetLanguageCallbackData::class)
            subclass(ShowSettingsCallbackData::class)
            subclass(ShowLanguageSettingsCallbackData::class)
            subclass(ShowEthSettingsCallbackData::class)
            subclass(ChainControlCallbackData::class)
        }
    }
}

fun getBackInlineButtonRow(localizer: Localizer, user: User, callbackData: CallbackData): List<InlineKeyboardButton> =
    row {
        dataButton(
            localizer.getText("back", user.config.language),
            generateCallbackDataWithArgs(callbackData)
        )
    }
