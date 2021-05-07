package com.clint.workmanagersample.network

import android.content.Context
import com.clint.workmanagersample.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class ServiceGenerator {

    companion object {
        private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        private val builder: Retrofit.Builder =
            Retrofit.Builder() //                    .baseUrl(UriList.BASE_URL + UriList.PACKAGE)
                .baseUrl(Constants.BASE_URL)
                .client(requestHeader)
                .addConverterFactory(GsonConverterFactory.create())

        private fun setLogger(): HttpLoggingInterceptor {
            val logging = HttpLoggingInterceptor()
            // set your desired log level
            logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
            return logging
        }

        fun <S> createService(serviceClass: Class<S>?): S {
            if (httpClient.interceptors().isNotEmpty()) {
                httpClient.interceptors().clear()
            }
            httpClient.addInterceptor(setLogger())
            httpClient.connectTimeout(1, TimeUnit.MINUTES)
            httpClient.readTimeout(1, TimeUnit.MINUTES)
            httpClient.writeTimeout(1, TimeUnit.MINUTES)
            httpClient.addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original: Request = chain.request()
                    val requestBuilder: Request.Builder = original.newBuilder()
                        .method(original.method, original.body)
                    val request: Request = requestBuilder.build()
                    return chain.proceed(request)
                }
            })
            val client: OkHttpClient = httpClient.build()
            val retrofit = builder.client(client).build()
            return retrofit.create(serviceClass)
        }

        private val requestHeader: OkHttpClient
            get() = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
    }
}