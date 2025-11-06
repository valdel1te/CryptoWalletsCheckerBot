package data

import org.ktorm.entity.Entity
import kotlinx.serialization.Serializable


interface User : Entity<User> {
    companion object : Entity.Factory<User>()

    val id: Int
    var tgId: Long
    var config: UserConfig
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

@Serializable
data class EthChain(
    var name: String,
    var rpcUrl: String,
    var tokens: List<Token>,
)

@Serializable
data class SolChain(
    var name: String,
    var rpcUrl: String,
    var tokens: List<Token>,
)

@Serializable
data class TonChain(
    var name: String,
    var tokens: List<Token>,
)

@Serializable
data class Token(var address: String)