import bot.Bot
import bot.handlers.CallbackHandler
import bot.handlers.CommandHandler
import bot.handlers.Handler
import bot.handlers.StatesHandler
import checker.PriceCache
import checker.chains.ErcChecker
import checker.chains.JettonChecker
import checker.chains.SplChecker
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
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
    bindSingletonOf(::SplChecker)
    bindSingletonOf(::JettonChecker)
}