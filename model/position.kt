package model

data class Position(
    val id: Int,
    val flight_id: Int,
    val lat: Double,
    val lon: Double,
    val altitude: Int,
    val speed: Int,
    val timestamp: String
)
