package com.dscvit.memecaption.api

import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException


class FileRepository {
    companion object {
        var cap = MutableLiveData<String>()
    }

    suspend fun uploadFile(file: File): Boolean {
        cap.postValue("Loading Caption...")
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
        return try {
            val response = Instance.api.uploadFile(
                data = MultipartBody.Part.createFormData(
                    "data",
                    file.name,
                    file.asRequestBody("image/$mime".toMediaTypeOrNull())
                )
            )

            cap.postValue(
                response.body().toString().replace("trial(got=Got(top=", "").replace("<sep>", "")
                    .replace("))", "").replace("<emp>", "").trim()
            )

            Log.d("resp message", response.message())
            Log.d("resp code", response.code().toString())
            Log.d("resp body", cap.value.toString())
            Log.d("resp error body", response.errorBody().toString())

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