package com.hnv.lifecycle

import androidx.lifecycle.MutableLiveData

class NonNullableLiveData<T>: MutableLiveData<T> {
    constructor(): super()
    constructor(value: T) : super(value)

    override fun postValue(value: T) {
        super.postValue(value)
    }

    override fun getValue(): T {
        return super.getValue() as T
    }
}