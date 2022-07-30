package com.dscvit.memecaption

import android.util.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.lang.Exception

class FileRepository {

    suspend fun uploadFile(file: File): Boolean {
        return try {
            Instance.api.uploadFile(
                data = MultipartBody.Part.createFormData(
                    "data",
                    file.name,
                    file.asRequestBody("image/png".toMediaTypeOrNull())
                )
            )
            Log.d("status", "Successful")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: HttpException) {
            e.printStackTrace()
            false
        }
    }
}