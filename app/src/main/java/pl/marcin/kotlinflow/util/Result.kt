package pl.marcin.kotlinflow.util

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val error: Throwable, val message: String?) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
