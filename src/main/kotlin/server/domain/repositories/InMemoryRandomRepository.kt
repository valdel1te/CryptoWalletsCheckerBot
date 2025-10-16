package server.domain.repositories

class InMemoryRandomRepository(
    private val items: List<String> = listOf("кто там", "занято", "да")
) : RandomRepository {
    override fun random(): String = items.random()
}