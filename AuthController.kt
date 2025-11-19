package controller

import com.google.gson.JsonObject
import okhttp3.*
import tornadofx.*
import java.util.*

class AuthController : Controller() {

    private val client = OkHttpClient()
    var token: String? = null

    fun login(user: String, pass: String): Boolean {
        val json = """
            {"username":"$user", "password":"$pass"}
        """.trimIndent()

        val body = RequestBody.create(
            MediaType.parse("application/json"), json
        )

        val request = Request.Builder()
            .url("http://localhost:8080/api/auth/login")
            .post(body)
            .build()

        val response = client.newCall(request).execute()

        return if (response.isSuccessful) {
            val jsonResp = response.body()?.string()
            val obj = gson.fromJson(jsonResp, JsonObject::class.java)
            token = obj["token"].asString
            true
        } else false
    }

    companion object {
        val gson = com.google.gson.Gson()
    }
}

