package server

/**
 * Простая логика CORS:
 * - читаем ALLOWED_ORIGINS из env (comma separated)
 * - если пусто -> dev-mode: возвращаем "*" (разрешаем все)
 * - если указаны домены -> возвращаем requestOrigin только если он в списке
 */
object CorsUtil {
    private val raw = System.getenv("ALLOWED_ORIGINS") ?: ""
    private val allowedList: List<String> = raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    fun allowedOrigin(requestOrigin: String?): String? {
        if (allowedList.isEmpty()) return "*" // dev-friendly
        if (requestOrigin == null) return null
        return if (allowedList.contains(requestOrigin)) requestOrigin else null
    }
}