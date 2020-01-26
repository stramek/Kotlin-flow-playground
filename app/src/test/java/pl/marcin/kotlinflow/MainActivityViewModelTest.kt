package pl.marcin.kotlinflow

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.netguru.weatherguru.common.TestCoroutineRule
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.marcin.kotlinflow.common.TestContextProvider
import pl.marcin.kotlinflow.feature.main.MainActivityViewModel
import pl.marcin.kotlinflow.feature.main.MainController
import pl.marcin.kotlinflow.model.Weather
import pl.marcin.kotlinflow.util.Result

class MainActivityViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    private val mainController = mock<MainController>()
    lateinit var viewModel: MainActivityViewModel
    private val weatherMock = mock<Weather>()

    @Before
    fun setup() {
//        whenever(mainController.getCityFlow(any())).thenReturn(flowOf())
//        whenever(mainController.getLibFlow()).thenReturn(flowOf())
//        whenever(mainController.getZippedFlow()).thenReturn(flowOf())
        viewModel = MainActivityViewModel(TestContextProvider(), mainController)
    }

    @Test
    fun `Should get zipped weathers`() = coroutineRule.runBlockingTest {
        val result = Result.Success(weatherMock to weatherMock)
        whenever(mainController.getZippedFlow()).thenReturn(flowOf(result))
        val resultObserver: Observer<Result<Pair<Weather, Weather>>> = spy()

        viewModel.getZippedLiveDataFunc().observeForever(resultObserver)

        verify(mainController).getZippedFlow()
        verify(resultObserver).onChanged(result)
    }

    @Test
    fun `Should get lib live result`() = coroutineRule.runBlockingTest {
        val result = Result.Success(12345)
        whenever(mainController.getLibFlow()).thenReturn(flowOf(result))
        val resultObserver: Observer<Result<Int>> = spy()
        val setupLibObserver: Observer<Boolean> = spy()

        viewModel.setupLibLiveData.observeForever(setupLibObserver)
        viewModel.getLibLiveDataFunc().observeForever(resultObserver)

        verify(mainController).getLibFlow()
        verify(resultObserver).onChanged(result)
        setupLibObserver.inOrder {
            verify().onChanged(false)
            verify().onChanged(true)
        }
    }

    @Test
    fun `Should get city on input`() {
        val result = Result.Success(weatherMock)
        whenever(mainController.getCityFlow(any())).thenReturn(flowOf(result))
        val resultObserver: Observer<Result<Weather>> = spy()

        viewModel.getCityLiveData().observeForever(resultObserver)

        verify(mainController).getCityFlow(any())
        verify(resultObserver).onChanged(result)
    }
}
