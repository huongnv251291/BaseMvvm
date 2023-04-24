package com.example.basemvvm.di

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.example.basemvvm.network.ApiSource
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

@JvmField
val networkModule: Module = module {
    single<Converter.Factory> { GsonConverterFactory.create(get()) }
    single {
        val chuckerCollector = ChuckerCollector(
            context = get(),
            // Toggles visibility of the notification
            showNotification = true,
            // Allows to customize the retention period of collected data
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        ChuckerInterceptor.Builder(get())
            .collector(chuckerCollector)
            .maxContentLength(250_000L)
            .alwaysReadResponseBody(true)
            .build()
    }
    single<ApiSource> {
        object : ApiSource {
            override fun getApi(): Retrofit {
                val networkFlipperPlugin = NetworkFlipperPlugin()
                SoLoader.init(androidContext(), false)
                val client = AndroidFlipperClient.getInstance(androidContext())
                client.addPlugin(networkFlipperPlugin)
                client.addPlugin(
                    InspectorFlipperPlugin(
                        androidContext(),
                        DescriptorMapping.withDefaults()
                    )
                )
                client.start()
                val builder = OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .addInterceptor(get() as ChuckerInterceptor)
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
                return Retrofit.Builder().baseUrl("")
                    .addConverterFactory(GsonConverterFactory.create()).client(builder.build())
                    .build()

            }
        }
    }
}