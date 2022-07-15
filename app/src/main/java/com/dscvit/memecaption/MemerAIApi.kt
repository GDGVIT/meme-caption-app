package com.dscvit.memecaption

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MemerAIApi {
    @GET("/get")
    suspend fun getCaption() : Response<DataClass>

    @Multipart
    @POST("/upload")
    suspend fun postImage(@Part body: MultipartBody.Part) : Response<DataClass>

    //TODO: post the image to the api
}