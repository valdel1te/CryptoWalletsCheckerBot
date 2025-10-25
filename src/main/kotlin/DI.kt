import bot.Bot
import bot.handlers.CallbackHandler
import bot.handlers.CommandHandler
import bot.handlers.Handler
import bot.handlers.StatesHandler
import checker.PriceCache
import checker.chains.ErcChecker
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.bindSingletonOf
import org.kodein.di.instance

val di = DI {
    // config - resources/application.conf
    bindSingleton<Config> {
        ConfigFactory.load()
            .getConfig(System.getProperty("profile"))
    }

    // handlers
    bindSingletonOf(::CommandHandler)
    bindSingletonOf(::CallbackHandler)
    bindSingletonOf(::StatesHandler)
    bindSingleton<List<Handler>> {
        listOf(
            instance<CommandHandler>(),
            instance<CallbackHandler>(),
            instance<StatesHandler>()
        )
    }

    // bot
    bindSingletonOf(::Bot)

    // checker folder
    bindSingletonOf(::PriceCache)
    bindSingletonOf(::ErcChecker)
}