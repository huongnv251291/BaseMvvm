package com.hnv.base

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.hnv.lifecycle.DebounceLiveData
import kotlinx.coroutines.*
import com.hnv.utils.exceptions.ExceptionHandler
import com.hnv.utils.exceptions.HvException
import com.hnv.lifecycle.SingleLiveEvent
import kotlin.experimental.ExperimentalTypeInference

abstract class BaseViewModel(
    private val exceptionHandler: ExceptionHandler
) : ViewModel() {
    private val rawException = MediatorLiveData<Throwable>()

    var loading = MediatorLiveData<Boolean>()
    var isShowKeyboard = MediatorLiveData<Boolean>()
    val submitted = SingleLiveEvent<Boolean>()

    fun toggleShowKeyboard(show: Boolean) {
        isShowKeyboard.postValue(show)
    }

    val handler = CoroutineExceptionHandler { _, throwable ->
        rawException.postValue(throwable)
        loading.postValue(false)
        submitted.postValue(false)
    }
    private val handlerException: LiveData<HvException> = rawException.switchMapLiveDataEmit {
        exceptionHandler.process(it)
    }

    val exception = SingleLiveEvent<HvException>().apply {
        addSource(handlerException) {
            postValue(it)
        }
    }

    @OptIn(ExperimentalTypeInference::class)
    open fun <Y> liveDataEmit(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        @BuilderInference block: suspend () -> Y
    ): LiveData<Y> = liveData(dispatcher) {
        emit(block())
    }

    @OptIn(ExperimentalTypeInference::class)
    fun <Y> liveData(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        @BuilderInference block: suspend LiveDataScope<Y>.() -> Unit
    ): LiveData<Y> = liveData(handler + dispatcher) {
        block()
    }

    fun <X, Y> LiveData<X>.switchMapLiveDataEmit(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend CoroutineScope.(X) -> Y
    ) = switchMap {
        liveData(handler + dispatcher) {
            emit(block(viewModelScope, it))
        }
    }

    @MainThread
    fun <T> LiveData<T>.checkDiffAndPostValue(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend CoroutineScope.(T?) -> T?
    ) {
        val currentValue = value
        viewModelScope.launch(handler + dispatcher) {
            val newValue = block.invoke(this, currentValue)
            if (currentValue == null && newValue == null) return@launch
            if (currentValue?.equals(newValue) != true) (this@checkDiffAndPostValue as? MutableLiveData<T>)?.postValue(
                newValue
            )
        }
    }

    fun <T> LiveData<T>.postIfChanged(newValue: T) {
        val currentValue = value
        if (currentValue?.equals(newValue) != true)
            postValue(newValue)
    }

    @AnyThread
    fun <T> LiveData<T>?.postValue(t: T) {
        when (this) {
            is SingleLiveEvent<T> -> this.postValue(t)
            is MediatorLiveData<T> -> this.postValue(t)
            is MutableLiveData<T> -> this.postValue(t)
            is DebounceLiveData<T> -> this.postValue(t)
        }
    }

    @MainThread
    fun <X> LiveData<X>.getOrDefault(default: X): X {
        return value ?: default
    }
}
