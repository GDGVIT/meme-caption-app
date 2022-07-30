package com.dscvit.memecaption

import okhttp3.MultipartBody
import okhttp3.RequestBody

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.Response
import java.io.File


interface MemerAIApi {

    //file
    @Multipart
    @POST("image/")
    suspend fun uploadFile(
        @Part data: MultipartBody.Part
    )



}