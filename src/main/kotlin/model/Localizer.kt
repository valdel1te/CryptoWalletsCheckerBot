package model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import data.User

class Localizer {
    private val russianDictionary = loadLocale("ru")
    private val englishDictionary = loadLocale("en")

    fun getText(key: String, user: User): String {
        return when (user.config.language) {
            "ru" -> russianDictionary[key] ?: "ошибка перевода"
            "en" -> englishDictionary[key] ?: "translate error"
            else -> "user config incorrect language"
        }
    }

    fun getText(key: String, locale: String): String {
        return when (locale) {
            "ru" -> russianDictionary[key] ?: "ошибка перевода"
            "en" -> englishDictionary[key] ?: "translate error"
            else -> "user config incorrect language"
        }
    }

    private fun loadLocale(language: String): Map<String, String> {
        val inputStream = this::class.java.classLoader.getResourceAsStream("locales/${language}.json") ?: return mapOf()
        return jacksonObjectMapper().readValue(inputStream)
    }
}