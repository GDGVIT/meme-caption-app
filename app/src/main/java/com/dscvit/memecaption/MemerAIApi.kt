package com.dscvit.memecaption

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface MemerAIApi {

    //gets the caption
    @GET("/get")
    suspend fun getCaption(): Response<DataClass>


    //base 64
    //post image to api
    @FormUrlEncoded
    @POST("/upload")
    fun uploadImage(
        @Field("image") image: String
    ): Call<String>


}