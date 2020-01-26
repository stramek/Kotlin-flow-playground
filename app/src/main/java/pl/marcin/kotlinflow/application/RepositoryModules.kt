package pl.marcin.kotlinflow.application

import pl.marcin.kotlinflow.api.WeatherRepository
import org.koin.dsl.module

val forecastModule = module {
    factory { WeatherRepository(get()) }
}