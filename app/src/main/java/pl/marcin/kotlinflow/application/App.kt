package pl.marcin.kotlinflow.application

import android.app.Application
import pl.marcin.kotlinflow.feature.main.mainActivityModule
import pl.marcin.kotlinflow.api.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                listOf(
                    mainActivityModule,
                    networkModule,
                    forecastModule,
                    fakeLibModule
                )
            )
        }
    }
}

