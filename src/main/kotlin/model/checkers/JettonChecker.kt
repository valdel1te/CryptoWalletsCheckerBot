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

        try {
            val json = Json { ignoreUnknownKeys = true }
            val walletInfoUrl = "${tonApi}/accounts/$addressString"

            val walletInfoResponse: String = client.get(walletInfoUrl).body()
            val walletInfo = json.decodeFromString<WalletInfo>(walletInfoResponse)
            val tonPrice = priceCache.getTokenPrice(client, "ton")
            balance += tonPrice * walletInfo.balance.toBigDecimal().movePointLeft(9)

            tokenList.forEach { token ->
                delay(500)

                val jettonUserInfoUrl = "$walletInfoUrl/jettons/$token"
                val jettonUserInfoResponse: String = client.get(jettonUserInfoUrl).body()
                val jettonUserInfo = json.decodeFromString<JettonUserInfo>(jettonUserInfoResponse)
                val tokenPrice = priceCache.getTokenPrice(client, jettonUserInfo.jetton.symbol.lowercase())
                val tokenBalance = jettonUserInfo.balance.toBigDecimal().movePointLeft(jettonUserInfo.jetton.decimals)
                balance += tokenPrice * tokenBalance
            }
        } catch (e: Exception) {
            logger.error(e.message)
            balance = BigDecimal.ZERO
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