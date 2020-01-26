package pl.marcin.kotlinflow.fakelib

import android.os.Handler
import timber.log.Timber

class FakeLibImpl {

    private lateinit var listener: SomeFakeCallback

    fun setListener(listener: SomeFakeCallback) {
        this.listener = listener
        listener.onValue(1000)
        listener.onComplete()
    }

    fun unsubscribe() {
        Timber.i(">>>> Unsubscribed fake library!")
    }
}