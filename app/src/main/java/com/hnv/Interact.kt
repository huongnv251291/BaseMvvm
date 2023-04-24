package com.hnv

abstract class Interact<in Param, out Result> where Param : Interact.Param, Result : Any? {

    abstract suspend fun execute(param: Param): Result

    open class Param
}