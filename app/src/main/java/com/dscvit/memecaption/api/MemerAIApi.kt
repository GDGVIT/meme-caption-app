package com.dscvit.memecaption.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface MemerAIApi {

    //file
    @Multipart
    @POST("image/")
    suspend fun uploadFile(
        @Part data: MultipartBody.Part
    ):Response<trial>


}