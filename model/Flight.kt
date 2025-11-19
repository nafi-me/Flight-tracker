package model

data class Flight(
    val id: Int,
    val icao: String,
    val callsign: String?,
    val origin: String?,
    val destination: String?
)
