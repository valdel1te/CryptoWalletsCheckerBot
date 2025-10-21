package checker.chains

import io.ethers.core.types.Address
import io.ethers.core.types.BlockId
import io.ethers.providers.Provider

fun getEthBalance(addressString: String, rpcUrl: String, tokens: List<String>): String {
    val address = Address(addressString)
    val provider = Provider.fromUrl(rpcUrl).unwrap()

    val balanceEth = provider.getBalance(address, BlockId.LATEST).sendAwait().unwrap()
    return balanceEth.toBigDecimal(18).toPlainString()
}

private const val PUBLIC_RPC_URL: String = "wss://0xrpc.io/eth"