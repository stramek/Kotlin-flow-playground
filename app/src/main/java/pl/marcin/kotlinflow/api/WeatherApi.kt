package pl.marcin.kotlinflow.api

import pl.marcin.kotlinflow.model.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather?units=metric")
    suspend fun getForecast(@Query("q") place: String): Weather
}
