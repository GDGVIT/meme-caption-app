package com.dscvit.memecaption.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Instance {

    val api: MemerAIApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://memer-ai.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MemerAIApi::class.java)
    }
}