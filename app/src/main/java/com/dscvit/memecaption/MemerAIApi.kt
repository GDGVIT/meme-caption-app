package com.dscvit.memecaption

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface MemerAIApi {
    @GET("/get")
    suspend fun getCaption() : Response<DataClass>

    @POST("/upload")
    suspend fun postImage() : Response<DataClass>

    //TODO: post the image to the api
}