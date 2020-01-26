package pl.marcin.kotlinflow.common

import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TestCollector<T> {
    private val values = mutableListOf<T>()

    fun test(scope: CoroutineScope, flow: Flow<T>): Job {
        return scope.launch { flow.collect { values.add(it) } }
    }

    fun getCollectedValues() = values
}
