package model.checkers

import model.PriceCache
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.math.BigDecimal

class JettonChecker(private val priceCache: PriceCache) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private val tonApi = "https://tonapi.io/v2"

    suspend fun getTonBalanceInUsd(
        client: HttpClient, addressString: String, tokenList: List<String>,
    ): BigDecimal {
        var balance = BigDecimal.ZERO

        runCatching {
            val json = Json { ignoreUnknownKeys = true }
            val walletInfoUrl = "${tonApi}/accounts/$addressString"

            val walletInfoResponse: String = client.get(walletInfoUrl).body()
            val walletInfo = json.decodeFromString<WalletInfo>(walletInfoResponse)
            val tonPrice = priceCache.getTokenPrice("ton")
            balance += tonPrice * walletInfo.balance.toBigDecimal().movePointLeft(9)

            tokenList.forEach { token ->
                delay(500)

                val jettonUserInfoUrl = "$walletInfoUrl/jettons/$token"
                val jettonUserInfoResponse: String = client.get(jettonUserInfoUrl).body()
                runCatching {
                    val jettonUserInfo = json.decodeFromString<JettonUserInfo>(jettonUserInfoResponse)
                    val tokenPrice = priceCache.getTokenPrice(jettonUserInfo.jetton.symbol.lowercase())
                    val tokenBalance =
                        jettonUserInfo.balance.toBigDecimal().movePointLeft(jettonUserInfo.jetton.decimals)
                    balance += tokenPrice * tokenBalance
                }.onFailure {
                    logger.error(it.message)
                    return@forEach
                }
            }
        }.onFailure {
            logger.error(it.message)
        }

        return balance
    }

    @Serializable
    data class WalletInfo(
        val address: String,
        val balance: Long,
    )

    @Serializable
    data class JettonUserInfo(
        val balance: Long,
        val jetton: JettonInfo,
    )

    @Serializable
    data class JettonInfo(
        val address: String,
        val name: String,
        val symbol: String,
        val decimals: Int,
    )
}