package pl.marcin.kotlinflow.api

import pl.marcin.kotlinflow.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var req = chain.request()
        val url = req.url.newBuilder().addQueryParameter(
            WEATHER_API_KEY_QUERY_HEADER,
            "API_KEY")
        .build()
        req = req.newBuilder().url(url).build()
        return chain.proceed(req)
    }

    companion object {
        private const val WEATHER_API_KEY_QUERY_HEADER = "APPID"
    }
}