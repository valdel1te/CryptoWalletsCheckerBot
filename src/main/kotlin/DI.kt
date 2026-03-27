import bot.Bot
import bot.BotEvent
import bot.EventHandler
import bot.callbacks.SetLanguageCallback
import bot.callbacks.ShowSettingsCallback
import bot.callbacks.ShowLanguageSettingsCallback
import bot.commands.SettingsCommand
import bot.commands.StartCommand
import bot.events.BotEventListener
import bot.events.EventBus
import bot.messages.BotMessageService
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import data.repositories.ProfileRepository
import data.repositories.UserRepository
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.telegramBot
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import model.Localizer
import model.PriceCache
import model.checkers.ErcChecker
import model.checkers.JettonChecker
import model.checkers.SplChecker
import model.services.ConfigService
import model.services.ProfileService
import model.services.UserService
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.bindSingletonOf
import org.kodein.di.instance
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect
import java.sql.Connection
import java.sql.DriverManager

val di = DI {
    // config - resources/application.conf
    bindSingleton<Config> {
        ConfigFactory.load()
            .getConfig(System.getProperty("profile"))
    }

    // db
    bindSingleton<Database> {
        Database.connect(
            url = instance<Config>().getString("db.url"),
            driver = "org.postgresql.Driver",
            user = instance<Config>().getString("db.user"),
            password = instance<Config>().getString("db.password"),
            dialect = PostgreSqlDialect()
        )
    }
    bindSingleton<Connection> {
        DriverManager.getConnection(
            instance<Config>().getString("db.url"),
            instance<Config>().getString("db.user"),
            instance<Config>().getString("db.password")
        )
    }
    // migrations
    bindSingleton<liquibase.database.Database> {
        DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(
                JdbcConnection(instance())
            )
    }
    bindSingleton<Liquibase> {
        Liquibase(
            instance<Config>().getString("liquibase.path"),
            ClassLoaderResourceAccessor(),
            instance<liquibase.database.Database>()
        )
    }

    // data
    bindSingletonOf(::UserRepository)
    bindSingletonOf(::ProfileRepository)

    // server services
    bindSingletonOf(::ConfigService)
    bindSingletonOf(::ProfileService)
    bindSingletonOf(::UserService)

    // handler
    bindSingletonOf(::EventHandler)

    // bot
    bindSingleton<TelegramBot> { telegramBot(instance<Config>().getString("token")) }
    bindSingletonOf(::Bot)

    // bot services
    bindSingletonOf(::BotMessageService)

    // events
    bindSingletonOf(::EventBus)
    bindSingletonOf(::BotEventListener)
    bindSingletonOf(::StartCommand)
    bindSingletonOf(::SettingsCommand)
    bindSingletonOf(::ShowSettingsCallback)
    bindSingletonOf(::ShowLanguageSettingsCallback)
    bindSingletonOf(::SetLanguageCallback)
    bindSingleton<List<BotEvent>> {
        listOf(
            instance<StartCommand>(),
            instance<SettingsCommand>(),
            instance<ShowSettingsCallback>(),
            instance<ShowLanguageSettingsCallback>(),
            instance<SetLanguageCallback>(),
        )
    }

    // helpers
    bindSingletonOf(::PriceCache)
    bindSingletonOf(::Localizer)

    // crypto checkers
    bindSingletonOf(::ErcChecker)
    bindSingletonOf(::SplChecker)
    bindSingletonOf(::JettonChecker)
}