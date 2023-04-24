package com.hnv.utils.exceptions


data class ToastException(
    override val code: Int = 0,
    override val message: String = "",
    override val cause: Throwable? = null
) : HvException(code, "$message", cause) {


}
