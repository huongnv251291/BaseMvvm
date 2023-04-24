package com.example.basemvvm.network

import retrofit2.Retrofit

interface ApiSource {
  fun getApi(): Retrofit
}
