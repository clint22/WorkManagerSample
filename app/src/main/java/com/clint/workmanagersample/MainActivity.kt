package com.clint.workmanagersample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.work.*
import com.bumptech.glide.Glide
import com.clint.workmanagersample.Constants.KEY_IMAGE_ID
import com.clint.workmanagersample.Constants.UNIQUE_TAG
import com.clint.workmanagersample.databinding.ActivityMainBinding
import com.google.gson.Gson
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val workManager by lazy {
        WorkManager.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.button.setOnClickListener {
//            createOneTimeRequest()
            createPeriodicWorkRequest()
        }
    }

    private fun createOneTimeRequest() {

        val builder = Data.Builder()
        builder.putString(KEY_IMAGE_ID, "1")
        val data = builder.build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val imageDownloadWorker = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .addTag(UNIQUE_TAG)
            .build()

        workManager.enqueueUniqueWork(
            "oneTimeImageDownloader",
            ExistingWorkPolicy.KEEP,
            imageDownloadWorker
        )

        observeWork(imageDownloadWorker.id)
        queryWorkInfo()


    }

    private fun createPeriodicWorkRequest() {

        val builder = Data.Builder()
        builder.putString(KEY_IMAGE_ID, "1")
        val data = builder.build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val imageDownloadWorker = PeriodicWorkRequestBuilder<ImageDownloadWorker>(
            15, TimeUnit.MINUTES
        ).setInputData(data)
            .setConstraints(constraints)
            .addTag(UNIQUE_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "periodicImageDownloader",
            ExistingPeriodicWorkPolicy.KEEP,
            imageDownloadWorker
        )
        observeWork(imageDownloadWorker.id)
        queryWorkInfo()
    }

    private fun observeWork(id: UUID) {
        workManager.getWorkInfoByIdLiveData(id).observe(this, { info ->
            if (info != null && info.state.isFinished) {
                val imageUrl = info.outputData.getString(KEY_IMAGE_ID)
                Log.e("imageUrlSuccess", imageUrl.toString())
                if (imageUrl != null) {
                    binding.button.visibility = View.GONE
                    binding.imageView.visibility = View.VISIBLE
                    setImage(imageUrl)
                }
            }
        })
    }

    private fun queryWorkInfo() {
        // 1
        val workQuery = WorkQuery.Builder
            .fromTags(listOf(UNIQUE_TAG))
            .addStates(listOf(WorkInfo.State.SUCCEEDED))
            .addUniqueWorkNames(
                listOf("oneTimeImageDownloader", "periodicImageDownloader")
            )
            .build()
        // 2
        workManager.getWorkInfosLiveData(workQuery).observe(this) { workInfos ->
            if (!workInfos.isNullOrEmpty()) {
                val imageUrl = workInfos[0].outputData.keyValueMap["image_id"].toString()
                Log.e("workInfos", Gson().toJson(workInfos))
                Log.e("image", imageUrl)
                binding.button.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
                setImage(imageUrl)
            }

        }
    }

    private fun setImage(imageUrl: String?) {
        Glide
            .with(this)
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_loading)
            .into(binding.imageView)
    }


}
