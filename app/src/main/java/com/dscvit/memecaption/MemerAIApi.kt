package com.dscvit.memecaption

import okhttp3.MultipartBody
import okhttp3.RequestBody

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.Response
import java.io.File


interface MemerAIApi {

    //base 64
    @Headers(
        "Accept: application/json"
    )
    @FormUrlEncoded
    @POST("encoded")
    fun uploadImageBase64(
        @Field("data") data: String
        // @Query("data") data: String
    ): Call<String>

    //file
    @Multipart
    @POST("image/")
    suspend fun uploadFile(
        @Part data: MultipartBody.Part
    )

//    @Multipart
//    @POST("image")
//    suspend fun upload(
//        @Part file: MultipartBody.Part
//    ): Response<DataClass>


}