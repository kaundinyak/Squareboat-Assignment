package com.kaundinyakasibhatla.squareboat_assignment.di.module

import com.kaundinyakasibhatla.squareboat_assignment.BuildConfig
import com.kaundinyakasibhatla.squareboat_assignment.data.api.ApiHelper
import com.kaundinyakasibhatla.squareboat_assignment.data.api.ApiHelperImpl
import com.kaundinyakasibhatla.squareboat_assignment.data.api.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient.Builder
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {


    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_URL

    /*@Provides
    @Singleton
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request: Request =
                        chain.request().newBuilder().addHeader("Authorization", "Bearer ${BuildConfig.client_secret}").build()
                    return chain.proceed(request)
                }
            })
            .build()
    } else {
        OkHttpClient
            .Builder()
            .addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request: Request =
                        chain.request().newBuilder().addHeader("Authorization", "Bearer ${BuildConfig.client_secret}").build()
                    return chain.proceed(request)
                }
            })
            .build()
    }*/

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder = Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        okHttpClientBuilder.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val request: Request =
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${BuildConfig.client_secret}")
                        .build()
                return chain.proceed(request)
            }
        })

        return okHttpClientBuilder.build()

    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        BASE_URL: String
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

}