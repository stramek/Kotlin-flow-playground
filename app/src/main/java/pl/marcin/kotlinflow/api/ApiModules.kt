package pl.marcin.kotlinflow.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    factory { provideAuthInterceptor() }
    factory { provideOkHttpClient(get()) }
    factory { provideWeatherApi(get()) }
    single { provideRetrofit(get()) }
}

fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient
{
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    return OkHttpClient().newBuilder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    val jsonConfiguration = JsonConfiguration(strictMode = false)
    return Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .client(okHttpClient)
        .addConverterFactory(Json(jsonConfiguration).asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
        .build()
}

fun provideWeatherApi(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)

private const val JSON_MEDIA_TYPE = "application/json"