package com.zpw.net.core

import com.zpw.net.core.api.ApiService
import com.zpw.net.core.interceptor.HandleDataInterceptor
import com.zpw.net.core.interceptor.HttpHeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitManager {
    private const val BASE_URL = "https://remo-test.obsbot.com/"

    val apiService: ApiService by lazy {
        RetrofitCreator.createApiService(
            ApiService::class.java, BASE_URL
        )
    }
}

object Config {
    const val DEFAULT_TIMEOUT = 3000L
}

object RetrofitCreator {

    private fun getRetrofitBuilder(baseUrl: String): Retrofit.Builder {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val mOkClient = OkHttpClient.Builder()
            .callTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .followRedirects(false)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(HttpHeaderInterceptor())
            .addInterceptor(HandleDataInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(mOkClient)
            .addConverterFactory(GsonConverterFactory.create())
    }

    /**
     * Create Api service
     *  @param  cls Api Service
     *  @param  baseUrl Base Url
     */
    fun <T> createApiService(cls: Class<T>, baseUrl: String): T {
        val retrofit = getRetrofitBuilder(
            baseUrl
        ).build()
        return retrofit.create(cls)
    }
}