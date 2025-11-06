package model

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

class PriceCache {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private val coinGeckoApiSimple = "https://api.coingecko.com/api/v3/simple/"

    private var lastUpdate: Instant? = null
    private var cachedPrices: MutableMap<String, BigDecimal> = mutableMapOf()

    suspend fun getTokenPrice(client: HttpClient, token: String): BigDecimal {
        val now = Instant.now()

        if (lastUpdate == null || Duration.between(lastUpdate, now).toMinutes() > 10 || cachedPrices[token] == null) {
            val postfix =
                if (cachedPrices[token] == null)
                    ",$token"
                else
                    ""
            val symbols = cachedPrices.keys.joinToString(separator = ",", postfix = postfix)
            cachedPrices = fetchPricesUsd(client, symbols) ?: cachedPrices
            lastUpdate = now

            logger.info("Updating prices, arg token: ${token.lowercase()} -> ${cachedPrices[token] ?: BigDecimal(0)}")
        }

        return cachedPrices[token] ?: BigDecimal(0)
    }

    fun addNewPrice(token: String, price: BigDecimal) {
        if (cachedPrices[token] == null && cachedPrices[token] == price)
            return

        cachedPrices[token] = price
        logger.info("Updating prices, arg token: ${token.lowercase()} -> ${cachedPrices[token] ?: BigDecimal(0)}")
    }

    suspend fun fillTokensPrice(client: HttpClient, tokens: String) {
        cachedPrices = fetchPricesUsd(client, tokens) ?: cachedPrices
        lastUpdate = Instant.now()

        logger.info("Prices: \n${getAllPricesString()}")
    }

    private fun getAllPricesString(): String {
        return cachedPrices.entries.joinToString("\n") { "${it.key}: ${it.value}" }
    }

    private suspend fun fetchPricesUsd(client: HttpClient, symbols: String): MutableMap<String, BigDecimal>? {
        try {
            val url = "${coinGeckoApiSimple}price?symbols=${symbols}&vs_currencies=usd"

            val responseText = client.get(url).body<String>()
            if (Json.parseToJsonElement(responseText) !is JsonObject)
                return null

            val pricesJson = Json.parseToJsonElement(responseText).jsonObject
            val pricesMap = mutableMapOf<String, BigDecimal>()

            pricesJson.forEach { (key, value) ->
                if (value.jsonObject["error_code"] != null)
                    return null

                val price = value.jsonObject["usd"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                pricesMap.put(key, BigDecimal(price))
            }

            return pricesMap
        } catch (e: Exception) {
            logger.error(e.message)
            return null
        }
    }
}

// TODO: удалить позже
suspend fun main() {
    val client = HttpClient()
    val priceCache = PriceCache()
    val eth = priceCache.getTokenPrice(client, "eth")
    val btc = priceCache.getTokenPrice(client, "btc")

    println("eth is $eth")
    println("btc is $btc")

    println("eth is ${priceCache.getTokenPrice(client, "eth")}") // посмотреть будет в логах третья запись или нет

    // задача окончательно доебать коингеко
    val sol = priceCache.getTokenPrice(client, "sol")
    println("sol is $sol")
    val ton = priceCache.getTokenPrice(client, "ton")
    println("ton is $ton")
    val notcoin = priceCache.getTokenPrice(client, "not")
    println("notcoin is $notcoin")
    val matic = priceCache.getTokenPrice(client, "matic")
    println("matic is $matic")

    println("eth is still ${priceCache.getTokenPrice(client, "eth")}")
}