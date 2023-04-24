package com.example.basemvvm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.basemvvm.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.logger.Level
import timber.log.Timber

open class App : MultiDexApplication(), Application.ActivityLifecycleCallbacks {
    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        // just for debug
        Timber.plant(Timber.DebugTree())
        instance = this
        registerActivityLifecycleCallbacks(this)
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(
                networkModule,
                viewModelModule,
                interactModule,
                repositoryModule,
                otherModule,
            )
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onActivityStarted(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityResumed(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityPaused(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityStopped(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        TODO("Not yet implemented")
    }

    override fun onActivityDestroyed(activity: Activity) {
        TODO("Not yet implemented")
    }

    private fun restartApplication(activity: Activity) {
        unloadKoinModules(
            listOf(
                networkModule,
                viewModelModule,
                interactModule,
                repositoryModule,
                otherModule,
            )
        )
        loadKoinModules(
            listOf(
                networkModule,
                viewModelModule,
                interactModule,
                repositoryModule,
                otherModule,
            )
        )

        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }
}