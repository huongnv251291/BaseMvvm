package com.example.basemvvm.di

import retrofit2.http.POST
//demo
internal interface Api {
    @POST("auth")
    suspend fun login(): String
}