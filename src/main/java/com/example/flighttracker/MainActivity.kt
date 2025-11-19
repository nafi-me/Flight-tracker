package com.example.flighttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import okhttp3.*
import okio.ByteString

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /* Compose UI with MapView */
        }

        // Connect to STOMP endpoint via SockJS is more complex in Android;
        // As a minimal approach, connect to WS endpoint and parse JSON.
        val request = Request.Builder().url("ws://your-server:8080/ws").build()
        client.newWebSocket(request, object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) { /* handle */ }
            override fun onMessage(webSocket: WebSocket, text: String) {
                // parse payload and update Compose state for markers
            }
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) { onMessage(webSocket, bytes.utf8()) }
        })
    }
}
