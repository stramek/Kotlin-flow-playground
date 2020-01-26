package pl.marcin.kotlinflow.application

import pl.marcin.kotlinflow.fakelib.FakeLibImpl
import org.koin.dsl.module

val fakeLibModule = module {
    factory { FakeLibImpl() }
}
