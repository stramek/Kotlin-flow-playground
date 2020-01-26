package pl.marcin.kotlinflow.fakelib

interface SomeFakeCallback {
    fun onValue(someData: Int)
    fun onError(error: Throwable)
    fun onComplete()
}