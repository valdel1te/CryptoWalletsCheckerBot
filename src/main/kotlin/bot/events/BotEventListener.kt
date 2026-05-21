package bot.events

import dev.inmo.tgbotapi.bot.TelegramBot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BotEventListener(
    private val bot: TelegramBot,
    private val eventBus: EventBus
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun start() {
        scope.launch {
            eventBus.events.collect { request ->
                try {
                    bot.execute(request)
                } catch (e: Exception) {
                    println("Ошибка при отправке через EventBus: ${e.message}")
                }
            }
        }
    }
}