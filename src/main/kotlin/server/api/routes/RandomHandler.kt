package server.api.routes

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import server.CorsUtil
import server.api.controllers.RandomController
import java.nio.charset.StandardCharsets

class RandomHandler(private val controller: RandomController) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        try {
            if (exchange.requestMethod == "OPTIONS") {
                // preflight handled separately by OptionsHandler in Server.kt (we will add generic handler)
                exchange.sendResponseHeaders(204, -1)
                return
            }

            if (exchange.requestMethod != "GET") {
                exchange.sendResponseHeaders(405, -1)
                return
            }

            val body = controller.getRandomJson().toByteArray(StandardCharsets.UTF_8)

            // headers
            exchange.responseHeaders.add("Content-Type", "application/json; charset=utf-8")
            // add CORS header if allowed
            val originHeader = exchange.requestHeaders.getFirst("Origin")
            val allowed = CorsUtil.allowedOrigin(originHeader)
            if (allowed != null) {
                exchange.responseHeaders.add("Access-Control-Allow-Origin", allowed)
                exchange.responseHeaders.add("Vary", "Origin")
            }

            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { os -> os.write(body) }
        } catch (t: Throwable) {
            t.printStackTrace()
            exchange.sendResponseHeaders(500, -1)
        } finally {
            exchange.close()
        }
    }
}