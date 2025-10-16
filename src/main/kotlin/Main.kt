import bot.Bot
import bot.Configuration
import org.kodein.di.instance
import server.Server
import server.domain.repositories.InMemoryRandomRepository

suspend fun main() {
    val repo = InMemoryRandomRepository()

    val server = Server(port = 8081, repo = repo)
    server.start()

    val bot: Bot by di.instance()
    val botToken = Configuration.getData("token")

    bot.startWithFSMAndLongPolling(botToken)
}