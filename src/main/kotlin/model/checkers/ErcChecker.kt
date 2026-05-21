package model.checkers

import io.ethers.abigen.generated.ERC20
import io.ethers.core.types.Address
import io.ethers.core.types.BlockId
import io.ethers.providers.Provider
import io.ethers.providers.types.sendAwait
import io.ethers.providers.types.unwrap
import kotlinx.coroutines.delay
import model.PriceCache
import org.slf4j.LoggerFactory
import java.math.BigDecimal

class ErcChecker(private val priceCache: PriceCache) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    suspend fun getChainBalanceInUsd(
        addressString: String,
        rpcUrl: String,
        tokenList: List<String>,
    ): BigDecimal {
        runCatching {
            val address = Address(addressString)
            val provider = Provider.fromUrl(rpcUrl).unwrap()
            val balanceEth = provider.getBalance(address, BlockId.LATEST).sendAwait().unwrap()

            val tokens = tokenList.map { ERC20(provider, Address(it)) }
            val symbols = tokens.map { it.symbol().call(BlockId.LATEST) }.sendAwait().unwrap()
            val decimals = tokens.map { it.decimals().call(BlockId.LATEST) }.sendAwait().unwrap()
            val tokensBalances = tokens.map { it.balanceOf(address).call(BlockId.LATEST) }.sendAwait().unwrap()

            var totalBalance = priceCache.getTokenPrice("eth") * balanceEth.toBigDecimal(18)
            tokensBalances.forEachIndexed { i, balance ->
                val symbol = symbols[i]
                val scaled = balance.toBigDecimal(decimals[i].toInt())

                delay(500)
                totalBalance += priceCache.getTokenPrice(symbol.lowercase()) * scaled
            }

            return totalBalance
        }.onFailure {
            logger.error(it.message)
            return BigDecimal.ZERO
        }

        return BigDecimal.ZERO
    }
}