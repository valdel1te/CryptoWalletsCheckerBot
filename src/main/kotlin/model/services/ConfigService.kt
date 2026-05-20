package model.services

import data.ChainWithTokens
import data.User
import data.UserConfig
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class ConfigService {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getDefaultConfigString(): String {
        return try {
            val inputStream = this::class.java.classLoader.getResourceAsStream("defaultConfig.json")
            inputStream?.bufferedReader()?.use { it.readText() } ?: throw Exception("file didn't found")
        } catch (e: Exception) {
            logger.error("getDefaultConfigString error -> ${e.message}")
            "{}"
        }
    }

    fun getDefaultConfig(): UserConfig {
        return Json.decodeFromString(getDefaultConfigString())
    }

    fun deleteEthChainByName(user: User, name: String, onSuccess: (User) -> Unit) {
        val newChainList = user.config.eth.filter { it.name != name }
        user.config.eth = newChainList
        onSuccess(user)
    }

    fun <T : ChainWithTokens> deleteTokenBySymbols(
        chains: List<T>,
        chainName: String,
        tokenSymbols: String,
        updateChains: (List<T>) -> Unit,
        onSuccess: () -> Unit,
    ) {
        val updatedChains = chains.map { chain ->
            if (chain.name == chainName) {
                chain.tokens = chain.tokens.filter { it.symbols != tokenSymbols }
            }
            chain
        }
        updateChains(updatedChains)
        onSuccess()
    }
}
