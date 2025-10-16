package server

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

class OptionsHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val origin = exchange.requestHeaders.getFirst("Origin")
        val allowed = CorsUtil.allowedOrigin(origin)
        if (allowed != null) {
            exchange.responseHeaders.add("Access-Control-Allow-Origin", allowed)
            exchange.responseHeaders.add("Vary", "Origin")
        }
        exchange.responseHeaders.add("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
        exchange.responseHeaders.add("Access-Control-Allow-Headers", "Content-Type")
        exchange.sendResponseHeaders(204, -1)
        exchange.close()
    }
}
