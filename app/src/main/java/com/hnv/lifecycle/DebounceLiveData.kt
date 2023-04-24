package com.hnv.lifecycle

import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DebounceLiveData<T>(private val scope: CoroutineScope, private val delay: Long = 300L) : MediatorLiveData<T>() {

    private var job: Job? = null

    override fun postValue(value: T) {
        postValue(value, null)
    }

    fun postValue(value: T, delay: Long? = null) {
        job?.cancel()
        job = scope.launch(Dispatchers.IO) {
            kotlinx.coroutines.delay(delay ?: this@DebounceLiveData.delay)
            super.postValue(value)
        }
    }

    override fun onInactive() {
        super.onInactive()
        job?.cancel()
    }
}