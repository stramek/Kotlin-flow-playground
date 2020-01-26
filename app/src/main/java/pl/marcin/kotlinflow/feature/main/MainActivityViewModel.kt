package pl.marcin.kotlinflow.feature.main

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.marcin.kotlinflow.util.CoroutineContextProvider

class MainActivityViewModel(
    private val coroutineContextProvider: CoroutineContextProvider,
    private val mainController: MainController
) : ViewModel() {

    /**
     * More advanced requesting with debounce using ConflatedBroadcastChannel
     */
    val cityChannel = ConflatedBroadcastChannel<String>()
    fun getCityLiveData() = mainController.getCityFlow(cityChannel)
        .flowOn(coroutineContextProvider.IO)
        .asLiveData()

    fun onInputChanged(city: String) {
        viewModelScope.launch {
            cityChannel.send(city)
        }
    }

    fun getZippedLiveDataFunc() = mainController
        .getZippedFlow()
        .flowOn(coroutineContextProvider.IO)
        .asLiveData()

    val setupLibLiveData = MutableLiveData<Boolean>(true)
    fun getLibLiveDataFunc() = mainController
        .getLibFlow()
        .onCompletion { setupLibLiveData.value = true }
        .onStart { setupLibLiveData.value = false }
        .asLiveData()
}
