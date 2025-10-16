package server.api.controllers

import kotlinx.serialization.json.Json
import server.domain.models.RandomResponse
import server.domain.services.RandomService
import org.slf4j.LoggerFactory


class RandomController(private val service: RandomService) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getRandomJson(): String {
        val msg = service.randomMessage()
        val dto = RandomResponse(message = msg)

        logger.info("sending random response: $msg");

        return Json.encodeToString(dto)
    }
}