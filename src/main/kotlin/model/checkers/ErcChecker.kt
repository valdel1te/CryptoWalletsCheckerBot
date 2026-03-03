package model.checkers

import model.PriceCache
import io.ethers.abigen.generated.ERC20
import io.ethers.core.types.Address
import io.ethers.core.types.BlockId
import io.ethers.providers.Provider
import io.ethers.providers.types.sendAwait
import io.ethers.providers.types.unwrap
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import java.math.BigDecimal

class ErcChecker(private val priceCache: PriceCache) {
    suspend fun getChainBalanceInUsd(
        client: HttpClient,
        addressString: String,
        rpcUrl: String,
        tokenList: List<String>
    ): BigDecimal {
        val address = Address(addressString)
        val provider = Provider.fromUrl(rpcUrl).unwrap()
        val balanceEth = provider.getBalance(address, BlockId.LATEST).sendAwait().unwrap()

        val tokens = tokenList.map { ERC20(provider, Address(it)) }
        val symbols = tokens.map { it.symbol().call(BlockId.LATEST) }.sendAwait().unwrap()
        val decimals = tokens.map { it.decimals().call(BlockId.LATEST) }.sendAwait().unwrap()
        val tokensBalances = tokens.map { it.balanceOf(address).call(BlockId.LATEST) }.sendAwait().unwrap()

        var totalBalance = priceCache.getTokenPrice(client, "eth") * balanceEth.toBigDecimal(18)
        tokensBalances.forEachIndexed { i, balance ->
            val symbol = symbols[i]
            val scaled = balance.toBigDecimal(decimals[i].toInt())

            delay(500)
            totalBalance += priceCache.getTokenPrice(client, symbol.lowercase()) * scaled
        }

        return totalBalance
    }
}