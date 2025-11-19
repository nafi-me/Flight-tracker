package controller

import model.Flight
import okhttp3.*
import tornadofx.*

class FlightController : Controller() {

    private val client = OkHttpClient()
    private val auth: AuthController by inject()

    fun fetchFlights(): List<Flight> {
        val request = Request.Builder()
            .url("http://localhost:8080/api/flights/all")
            .header("Authorization", "Bearer ${auth.token}")
            .build()

        val response = client.newCall(request).execute()
        val json = response.body()?.string() ?: "[]"

        return AuthController.gson.fromJson(json, Array<Flight>::class.java).toList()
    }
}
