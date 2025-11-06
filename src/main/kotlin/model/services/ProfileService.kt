package model.services

import data.Profile
import data.User
import data.repositories.ProfileRepository
import org.slf4j.LoggerFactory

class ProfileService(private val repository: ProfileRepository) {
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

    fun getByUserAndName(user: User, name: String): Profile {
        return repository.getByUserAndName(user, name) ?: Profile()
    }

    fun delete(profile: Profile) {
        repository.delete(profile)
    }

    fun getUserProfiles(user: User): List<Profile> {
        return repository.getUserProfiles(user)
    }
}