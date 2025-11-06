package data.repositories

import data.User
import data.Users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.update

class UserRepository(private val database: Database) {
    private val users get() = database.sequenceOf(Users)

    fun create(user: User): Int {
        return users.add(user)
    }

    fun update(user: User): Int {
        return users.update(user)
    }

    fun getByTgId(tgId: Long): User? {
        return users.find { it.tgId eq tgId }
    }
}