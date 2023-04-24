package com.hnv.utils.exceptions

import retrofit2.HttpException

class ExceptionHandler(private val exceptionFactories: ArrayList<ExceptionFactory>) {
//    fun process(cause: Throwable): KvException {
//        cause.printStackTrace()
//        if (cause is KvException) {
//            return cause
//        } else {
//            var error: KvException? = null
//            try {
//                for (filter in exceptionFactories) {
//                    error = filter.buildError(cause)
//                    if (error != null) break
//                }
//            } catch (e: Exception) {
//                if (BuildConfig.DEBUG) {
//                    var errorCode = Int.MIN_VALUE
//                    if (cause is HttpException)
//                        errorCode = cause.code()
//                    error = ToastException(errorCode, e.message ?: "Chưa xác định", e)
//                }
//            }
//            if (error == null) {
//                error = KvException(ExceptionEnum.UNDEFINED.value, cause)
//            }
//            return error
//        }
//    }
    fun process(cause: Throwable): HvException {
        cause.printStackTrace()
        return if(cause is HvException){
            cause
        }else{
            HvException(0,"khong xac dinh",cause)
        }
    }

}