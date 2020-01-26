package pl.marcin.kotlinflow.feature.main

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.marcin.kotlinflow.util.CoroutineContextProvider

val mainActivityModule = module {
    factory { CoroutineContextProvider() }
    factory { MainController(get(), get()) }
    viewModel { MainActivityViewModel(get(), get()) }
}
