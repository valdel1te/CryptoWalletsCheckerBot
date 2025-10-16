package server.domain.services

import server.domain.repositories.RandomRepository


class RandomService(private val repository: RandomRepository) {
    fun randomMessage(): String = repository.random()
}