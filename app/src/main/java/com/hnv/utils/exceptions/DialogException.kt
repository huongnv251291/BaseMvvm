package com.hnv.utils.exceptions

data class DialogException(
    override val code: Int,
    var title: String,
    override var message: String,
    override val cause: Throwable? = null,
) : HvException(code, message, cause)