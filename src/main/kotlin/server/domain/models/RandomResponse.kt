package server.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class RandomResponse(val message: String)