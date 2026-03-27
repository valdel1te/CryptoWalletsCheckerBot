package bot.callbacks

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
sealed interface CallbackData

fun generateCallbackDataWithArgs(data: CallbackData): String = callbackJson.encodeToString(data)

val callbackJson = Json {
    classDiscriminator = "n"
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        polymorphic(CallbackData::class) {
            subclass(SetLanguageCallbackData::class)
            subclass(ShowSettingsCallbackData::class)
            subclass(ShowLanguageSettingsCallbackData::class)
        }
    }
}
