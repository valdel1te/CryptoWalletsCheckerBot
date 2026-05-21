package model.services

import data.Profile
import data.Token
import data.User
import data.repositories.ProfileRepository
import io.ktor.client.HttpClient
import model.checkers.ErcChecker
import model.checkers.JettonChecker
import model.checkers.SplChecker
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import kotlin.plus

class ProfileService(
    private val repository: ProfileRepository,
    private val ercChecker: ErcChecker,
    private val jettonChecker: JettonChecker,
    private val splChecker: SplChecker,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    fun addNew(user: User, name: String, addresses: String = "") {
        if (getUserProfiles(user).count() == 10) return

        val newProfile = Profile {
            this.user = user
            this.name = name
            this.addresses = addresses
        }

        repository.create(newProfile)
    }

    fun update(profile: Profile) {
        repository.update(profile)
    }

    fun getAddresses(profile: Profile): List<String> {
        return profile.addresses.split(";")
    }

    fun saveNewAddresses(profile: Profile, addresses: List<String>) {
        if (addresses.count() > 100) return

        val updatedProfile = profile.apply { this.addresses = addresses.joinToString(";") }
        update(updatedProfile)
    }

    fun addNewAddress(profile: Profile, address: String) {
        val addresses = profile.addresses
        profile.addresses =
            if (addresses == "")
                address
            else
                "$addresses;$address"

        update(profile)
    }

    fun getByUserAndName(user: User, name: String): Profile {
        return repository.getByUserAndName(user, name)
            ?: throw NoSuchElementException("didn't found the profile $name for user ${user.id}")
    }

    fun delete(profile: Profile) {
        repository.delete(profile)
    }

    fun getUserProfiles(user: User): List<Profile> {
        return repository.getUserProfiles(user)
    }

    suspend fun getProfilesBalances(user: User, profiles: List<Profile>): Map<Profile, BigDecimal> {
        return profiles.associateWith { profile ->
            var balance = BigDecimal.ZERO
            val addresses = profile.addresses.split(";")

            addresses.forEach { addressWithChain ->
                if (addressWithChain == "") return@forEach

                val parts = addressWithChain.split(":")
                val chain = parts[0]
                val address = parts[1]

                when (chain) {
                    "eth" -> {
                        user.config.eth.forEach { ethChain ->
                            val tokenList = getTokenAddresses(ethChain.tokens)
                            balance += ercChecker.getChainBalanceInUsd(address, ethChain.rpcUrl, tokenList)
                        }
                    }

                    "sol" -> {
                        val solConfig = user.config.sol.first()
                        val tokenList = getTokenAddresses(solConfig.tokens)
                        balance += splChecker.getSolBalanceInUsd(HttpClient(), address, solConfig.rpcUrl, tokenList)
                    }

                    "ton" -> {
                        val tonConfig = user.config.ton.first()
                        val tokenList = getTokenAddresses(tonConfig.tokens)
                        balance += jettonChecker.getTonBalanceInUsd(HttpClient(), address, tokenList)
                    }

                    else -> profile to BigDecimal.ZERO
                }

            }

            balance
        }
    }

    fun getTokenAddresses(tokens: List<Token>): List<String> {
        return tokens.map { token -> token.address }
    }
}