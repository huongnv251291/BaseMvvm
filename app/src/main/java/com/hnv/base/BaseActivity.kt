package com.hnv.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.hnv.helpers.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import java.util.*

open class BaseActivity(layoutRes: Int) : AppCompatActivity(layoutRes) {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}