package controller

import tornadofx.*
import okhttp3.*
import okio.ByteString

class WebSocketController : Controller() {

    private val client = OkHttpClient()

    lateinit var listener: (String) -> Unit

    fun connect() {
        val request = Request.Builder()
            .url("ws://localhost:8080/ws/flights")
            .build()

        client.newWebSocket(request, object : WebSocketListener() {

            override fun onMessage(webSocket: WebSocket, text: String) {
                if (::listener.isInitialized)
                    listener(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                onMessage(webSocket, bytes.utf8())
            }
        })
    }
}
