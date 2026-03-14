package ru.mishlak.rksmp_pr1_8.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class UploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
	override suspend fun doWork(): Result {
		val watermarkedFile = inputData.getString("watermarked_file") ?: return Result.failure(
			Data.Builder().putString("error", "No watermarked file").build()
		)
		return try {
			for (i in 0..100 step 10) {
				if (!coroutineContext.isActive) return Result.failure()
				setProgress(Data.Builder().putInt("progress", i).build())
				delay(200)
			}
			val uploadedFile = "uploaded_$watermarkedFile"
			Result.success(Data.Builder().putString("uploaded_file", uploadedFile).build())
		} catch (e: Exception) {
			Result.failure(Data.Builder().putString("error", "Upload failed: ${e.message}").build())
		}
	}
}