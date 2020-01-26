package pl.marcin.kotlinflow.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    @SerialName("main") val temp: TempData,
    val name: String
)

@Serializable
data class TempData(
    val temp: Double,
    val humidity: Int
)