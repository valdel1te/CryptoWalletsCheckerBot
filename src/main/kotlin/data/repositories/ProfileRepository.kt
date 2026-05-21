package data.repositories

import data.Profile
import data.Profiles
import data.User
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.update

class ProfileRepository(private val database: Database) {
    private val profiles get() = database.sequenceOf(Profiles)

    fun create(profile: Profile): Int {
        return profiles.add(profile)
    }

    fun update(profile: Profile): Int {
        return profiles.update(profile)
    }

    fun delete(profile: Profile): Int {
        return database.delete(Profiles) { it.id eq profile.id }
    }

    fun getByUserAndName(user: User, name: String): Profile? {
        return profiles.find { (it.userId eq user.id) and (it.name eq name) }
    }

    fun getUserProfiles(user: User): List<Profile> {
        return database.from(Profiles)
            .select()
            .where { Profiles.userId eq user.id }
            .map { Profiles.createEntity(it) }
    }
}