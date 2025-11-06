package model.services

import data.User
import data.UserConfig
import data.repositories.UserRepository
import org.slf4j.LoggerFactory

class UserService(
    private val repository: UserRepository,
    private val configService: ConfigService,
) {
    private val defaultConfig = configService.getDefaultConfig()

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    fun create(tgId: Long, config: UserConfig?) {
        if (getByTgId(tgId) != null) return

        val newUser = User {
            this.tgId = tgId
            this.config = config ?: defaultConfig
        }

        repository.create(newUser)
    }

    fun update(user: User) {
        repository.update(user)
    }

    fun getByTgId(tgId: Long): User? {
        return repository.getByTgId(tgId)
    }
}