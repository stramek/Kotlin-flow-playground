package pl.marcin.kotlinflow

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.netguru.weatherguru.common.TestCoroutineRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import pl.marcin.kotlinflow.api.WeatherRepository
import pl.marcin.kotlinflow.common.TestCollector
import pl.marcin.kotlinflow.common.TestContextProvider
import pl.marcin.kotlinflow.fakelib.FakeLibImpl
import pl.marcin.kotlinflow.fakelib.SomeFakeCallback
import pl.marcin.kotlinflow.feature.main.MainController
import pl.marcin.kotlinflow.model.Weather
import pl.marcin.kotlinflow.util.Result

class MainControllerTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    private val weatherRepository = mock<WeatherRepository>()
    private val fakeLibImpl = mock<FakeLibImpl>()
    private val weatherMock = mock<Weather>()
    private val listener = mock<SomeFakeCallback>()

    private val controller = MainController(weatherRepository, fakeLibImpl)

    @Test
    fun `Should download weather after 1000 ms`() = runBlockingTest {
        whenever(weatherRepository.getWeather(any())).thenReturn(weatherMock)
        val broadcastChannel = ConflatedBroadcastChannel<String>()

        val flow = controller.getCityFlow(broadcastChannel)
        val collector = TestCollector<Result<Weather>>()
        val job = collector.test(this, flow)

        broadcastChannel.offer("item")
        advanceTimeBy(1000)

        assertThat(collector.getCollectedValues()).containsExactly(
            Result.Loading,
            Result.Success(weatherMock)
        )
        job.cancel()
    }

    @Test
    fun `Should emit value from lib`() = runBlockingTest {
        whenever(fakeLibImpl.setListener(any())).thenCallRealMethod()

        val flow = controller.getLibFlow()
        val collector = TestCollector<Result<Int>>()
        val job = collector.test(this, flow)

        fakeLibImpl.setListener(listener)

        assertThat(collector.getCollectedValues()).containsExactly(
            Result.Success(1000)
        )
        verify(fakeLibImpl).unsubscribe()
        job.cancel()
    }

    @Test
    fun `Should zip two flows`() = runBlockingTest {
        whenever(weatherRepository.getWeather(any())).thenReturn(weatherMock)

        val flow = controller.getZippedFlow()
        val collector = TestCollector<Result<Pair<Weather, Weather>>>()
        val job = collector.test(this, flow)

        advanceTimeBy(1000)

        assertThat(collector.getCollectedValues()).containsExactly(
            Result.Loading,
            Result.Success(weatherMock to weatherMock)
        )
        job.cancel()
    }
}