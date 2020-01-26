package pl.marcin.kotlinflow.feature.main

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import pl.marcin.kotlinflow.api.WeatherRepository
import pl.marcin.kotlinflow.fakelib.FakeLibImpl
import pl.marcin.kotlinflow.fakelib.SomeFakeCallback
import pl.marcin.kotlinflow.model.Weather
import pl.marcin.kotlinflow.util.CoroutineContextProvider
import pl.marcin.kotlinflow.util.Result
import timber.log.Timber

class MainController constructor(
    private val weatherRepository: WeatherRepository,
    private val someLibInstance: FakeLibImpl
) {

    /**
     * More advanced requesting with debounce using ConflatedBroadcastChannel
     */
    fun getCityFlow(broadcastChannel: BroadcastChannel<String>) = broadcastChannel
        .asFlow()
        .debounce(1000)
        .map { input -> input.trim().toUpperCase() }
        .distinctUntilChanged()
        .flatMapConcat { cityName ->
            flow {
                emit(Result.Loading)
                try {
                    val weather = getWeather(cityName)
                    emit(Result.Success(weather))
                } catch (exception: Exception) {
                    emit(Result.Error(exception, exception.message))
                }
            }
        }

    /**
     * Combined two flows -> errors propagated to combinedLiveData
     */
    private val flow1: Flow<Weather> = flow { emit(getWeather("Warsaw")) }
    private val flow2: Flow<Weather> = flow { emit(getWeather("Helsinki")) }
    /*val combinedLiveData = flow1.combineTransform<Weather, Weather, Result<Pair<Weather, Weather>>>(flow2) { weather1, weather2 ->
        emit(Result.Success(weather1 to weather2))
    }
        .catch { error -> emit(Result.Error(error, error.message)) }
        .asLiveData()*/
    fun getZippedFlow() = flow1.zip<Weather, Weather, Result<Pair<Weather, Weather>>>(flow2) { weather1, weather2 ->
        return@zip Result.Success(weather1 to weather2)
    }
        .catch { error -> emit(Result.Error(error, error.message)) }
        .onEach { delay(1000) } // delays data and error, but not Loading which is below
        .onStart { emit(Result.Loading) }

    private suspend fun getWeather(cityName: String) = withContext(Dispatchers.IO) {
        weatherRepository.getWeather(cityName)
    }

    /**
     * Wrapping libraries callbacks
     */
    fun getLibFlow(): Flow<Result<Int>> = callbackFlow { // Same as channelFlow
        someLibInstance.setListener(object : SomeFakeCallback {
            override fun onValue(someData: Int) {
                Timber.i(">>> Got new value from lib! $someData")
                offer(Result.Success(someData))
            }
            override fun onError(error: Throwable) {
                Timber.e(error)
                offer(Result.Error(error, error.message))
            }
            override fun onComplete() {
                Timber.i(">>>> Lib flow completed")
                close()
            }
        })

        awaitClose {
            Timber.i(">>>>> Lib awaitClose called -> unsubscribing...")
            someLibInstance.unsubscribe()
        }
    }
}