import bot.Bot
import bot.Configuration
import checker.PriceCache
import io.ktor.client.HttpClient
import liquibase.Liquibase
import org.kodein.di.instance
import java.sql.Connection

const val TOKENS =
    "eth,usdt,usdc,usdt0,bnb,weth,arb,sol,op,wld,pengu,abster,big,trx,jup,ssol,bonk,ton,not,blum,dogs,major,px"

suspend fun main() {
    val connection: Connection by di.instance()
    val migration: Liquibase by di.instance()
    migration.update()
    connection.close()

    val priceCache: PriceCache by di.instance()
    priceCache.fillTokensPrice(HttpClient(), TOKENS) // initial base price list

    val bot: Bot by di.instance()
    val botToken = Configuration.getData("token")
    bot.startWithFSMAndLongPolling(botToken)
}