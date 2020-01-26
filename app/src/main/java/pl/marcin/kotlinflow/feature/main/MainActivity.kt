package pl.marcin.kotlinflow.feature.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import pl.marcin.kotlinflow.R
import pl.marcin.kotlinflow.model.Weather
import pl.marcin.kotlinflow.util.Result
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindObservables()
        setupListeners()
    }

    private fun setupListeners() {
        setupLibBt.setOnClickListener { observeLib() }
        zippedLiveData.setOnClickListener { observeZippedLiveData() }
        cityEt.doAfterTextChanged { text -> viewModel.onInputChanged(text.toString()) }
    }

    private fun bindObservables() {
        viewModel.getCityLiveData().observe(
            this,
            Observer { result -> handleResult(result, temperatureThirdTv) })
        viewModel.setupLibLiveData.observe(
            this,
            Observer { isEnabled -> setupLibBt.isEnabled = isEnabled }
        )
    }

    private fun observeZippedLiveData() {
        viewModel.getZippedLiveDataFunc().observe(
            this,
            Observer { result ->
                when (result) {
                    is Result.Loading -> temperature4Tv.text = getString(R.string.loading_message)
                    is Result.Success<Pair<Weather, Weather>> -> temperature4Tv.text = "${formatWeather(result.data.first)}\n${formatWeather(result.data.second)}"
                    is Result.Error -> temperature4Tv.text = result.message
                }
            }
        )
    }

    private fun observeLib() {
        viewModel.getLibLiveDataFunc().observe(
            this,
            Observer { result ->
                when (result) {
                    is Result.Success -> libraryCallbackResult.text = result.data.toString()
                    is Result.Error -> libraryCallbackResult.text = result.message
                    is Result.Loading -> libraryCallbackResult.text = getString(R.string.loading_message)
                }
            }
        )
    }

    private fun handleResult(result: Result<Weather>, targetView: TextView) {
        when (result) {
            is Result.Loading -> targetView.text = getString(R.string.loading_message)
            is Result.Success<Weather> -> targetView.text = formatWeather(result.data)
            is Result.Error -> targetView.text = result.message
        }
    }

    private fun formatWeather(weather: Weather) = getString(
        R.string.weather_template,
        weather.name,
        weather.temp.temp
    )
}
