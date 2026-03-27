package bot.events

import dev.inmo.tgbotapi.requests.abstracts.Request
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class EventBus {
    private val _events = MutableSharedFlow<Request<*>>(extraBufferCapacity = 64)
    val events = _events.asSharedFlow()

    suspend fun publish(request: Request<*>) {
        _events.emit(request)
    }
}