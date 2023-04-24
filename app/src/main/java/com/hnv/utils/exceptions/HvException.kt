package com.hnv.utils.exceptions

open class HvException(
    open val code: Int = 0,
    override val message: String = "",
    cause: Throwable? = null
) : Throwable(message, cause)