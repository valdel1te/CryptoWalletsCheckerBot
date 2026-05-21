package data

import bot.fsm.UserState
import org.ktorm.entity.Entity
import kotlinx.serialization.Serializable


interface User : Entity<User> {
    companion object : Entity.Factory<User>()

    val id: Int
    var tgId: Long
    var config: UserConfig
    var state: UserState
}

interface Profile : Entity<Profile> {
    companion object : Entity.Factory<Profile>()

    val id: Int
    var user: User
    var name: String
    var addresses: String
}

@Serializable
data class UserConfig(
    var language: String,
    var eth: List<EthChain>,
    var sol: List<SolChain>,
    var ton: List<TonChain>,
)

interface ChainWithTokens {
    var name: String
    var tokens: List<Token>
}

@Serializable
data class EthChain(
    override var name: String,
    var rpcUrl: String,
    override var tokens: List<Token>,
) : ChainWithTokens

@Serializable
data class SolChain(
    override var name: String,
    var rpcUrl: String,
    override var tokens: List<Token>,
) : ChainWithTokens

@Serializable
data class TonChain(
    override var name: String,
    override var tokens: List<Token>,
) : ChainWithTokens

@Serializable
data class Token(
    var address: String,
    var symbols: String,
)