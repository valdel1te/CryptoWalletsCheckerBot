import bot.Bot
import bot.Configuration
import org.kodein.di.instance

suspend fun main() {
    val bot: Bot by di.instance()
    val botToken = Configuration.getData("token")

    bot.startWithFSMAndLongPolling(botToken)
}