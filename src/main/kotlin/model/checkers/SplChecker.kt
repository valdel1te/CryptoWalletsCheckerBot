package model.checkers

import model.PriceCache
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.sol4k.Connection
import org.sol4k.PublicKey
import org.sol4k.exception.RpcException
import java.math.BigDecimal

class SplChecker(private val priceCache: PriceCache) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private val jupiterApi = "https://lite-api.jup.ag/tokens/v2"

    suspend fun getSolBalanceInUsd(
        client: HttpClient, addressString: String, rpcUrl: String, tokenList: List<String>
    ): BigDecimal {
        val connection = Connection(rpcUrl)
        val wallet = PublicKey(addressString)
        val balanceLamports = connection.getBalance(wallet)

        var balance = priceCache.getTokenPrice(client, "sol") * BigDecimal(balanceLamports).movePointLeft(9)
        tokenList.forEach { token ->
            val tokenInfo = searchTokenInfo(client, token)
            val symbol = tokenInfo.symbol.lowercase()
            val price = if (tokenInfo.usdPrice == 0.0) {
                delay(500)

                priceCache.getTokenPrice(client, symbol)
            } else {
                val tokenPriceBigDecimal = tokenInfo.usdPrice.toBigDecimal()
                priceCache.addNewPrice(symbol, tokenPriceBigDecimal)

                tokenPriceBigDecimal
            }

            val tokenMintAddress = PublicKey(token)
            val balanceTokens = try {
                val programDerivedAddress = PublicKey.findProgramDerivedAddress(wallet, tokenMintAddress)

                connection.getTokenAccountBalance(programDerivedAddress.publicKey).uiAmount.toBigDecimal()
            } catch (_: RpcException) {
                BigDecimal.ZERO // wallet doesn't have current token
            }

            balance += price * balanceTokens
            delay(500)
        }

        return balance
    }

    private suspend fun searchTokenInfo(client: HttpClient, mintAddress: String): SplData {
        val url = "${jupiterApi}/search"
        try {
            val json = Json { ignoreUnknownKeys = true }
            val response: String = client.get(url) {
                parameter("query", mintAddress)
            }.body()
            val splData = json.decodeFromString<List<SplData>>(response)

            return splData[0]
        } catch (e: Exception) {
            logger.error(e.message)
            return SplData("0", "error", "error", 0.0)
        }
    }

    @Serializable
    data class SplData(
        val id: String,
        val name: String,
        val symbol: String,
        val usdPrice: Double,
    )
}