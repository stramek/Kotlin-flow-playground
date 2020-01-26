package pl.marcin.kotlinflow.api

class WeatherRepository(private val weatherApi: WeatherApi) {
    suspend fun getWeather(place: String) = weatherApi.getForecast(place)
}
