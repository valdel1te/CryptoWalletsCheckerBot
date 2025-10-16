package server

import com.sun.net.httpserver.HttpServer
import server.api.controllers.RandomController
import server.api.routes.RandomHandler
import server.domain.repositories.RandomRepository
import server.domain.services.RandomService
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory

class Server(
    private val host: String = "0.0.0.0",
    private val port: Int = 8081,
    private val repo: RandomRepository
) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private var httpServer: HttpServer? = null
    private val executor = Executors.newFixedThreadPool(4)

    fun start() {
        if (httpServer != null) return
        val service = RandomService(repo)
        val controller = RandomController(service)
        httpServer = HttpServer.create(InetSocketAddress(host, port), 0).apply {
            executor = this@Server.executor
            // context for GET /api/random
            createContext("/api/random", RandomHandler(controller))
            // generic context to reply to OPTIONS for /api/* paths (simple approach)
            createContext("/api", OptionsHandler())
            start()
        }
        logger.info("Server started at http://$host:$port")
    }

    fun stop() {
        httpServer?.stop(0)
        executor.shutdown()
        httpServer = null
        logger.info("Server stopped")
    }
}