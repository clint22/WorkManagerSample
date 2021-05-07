package com.clint.workmanagersample

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.clint.workmanagersample.Constants.KEY_IMAGE_ID
import com.clint.workmanagersample.network.Service
import com.clint.workmanagersample.network.ServiceGenerator
import com.google.gson.Gson
import java.lang.Exception

class ImageDownloadWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val imageId = inputData.getString(KEY_IMAGE_ID)
            val picture = downloadImage(imageId)
            val outputData = workDataOf(KEY_IMAGE_ID to picture)
            Result.success(outputData)
        }catch (e: Exception) {
            Log.e("doWorkException", e.printStackTrace().toString())
            Result.failure()
        }

    }

    private suspend fun downloadImage(imageId: String?): String? {

        val service = ServiceGenerator.createService(Service::class.java)
        var imageUrl: String? = null
        if (imageId != null) {
            val response = service.getImage(imageId)
            if (response.isSuccessful) {
                response.body().let {
                    Log.e("imageResponse", Gson().toJson(response.body()))
                    imageUrl = response.body()?.download_url
                }
            }
        }
        return imageUrl
    }
}

