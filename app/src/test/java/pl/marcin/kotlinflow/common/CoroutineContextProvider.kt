package pl.marcin.kotlinflow.common

import kotlinx.coroutines.Dispatchers
import pl.marcin.kotlinflow.util.CoroutineContextProvider
import kotlin.coroutines.CoroutineContext

open class TestContextProvider : CoroutineContextProvider() {
    override val Main: CoroutineContext = Dispatchers.Unconfined
    override val IO: CoroutineContext = Dispatchers.Unconfined
}
