package com.hnv.utils.exceptions


interface ExceptionFactory {
    fun buildError(cause: Throwable): HvException?
}