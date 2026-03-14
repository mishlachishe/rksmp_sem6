package ru.mishlak.rksmp_pr1_8.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class WatermarkWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
	override suspend fun doWork(): Result {
		val compressedFile = inputData.getString("compressed_file") ?: return Result.failure(
			Data.Builder().putString("error", "No compressed file").build()
		)
		return try {
			for (i in 0..100 step 10) {
				if (!coroutineContext.isActive) return Result.failure()
				setProgress(Data.Builder().putInt("progress", i).build())
				delay(200)
			}
			val outputFile = "watermarked_$compressedFile"
			Result.success(Data.Builder().putString("watermarked_file", outputFile).build())
		} catch (e: Exception) {
			Result.failure(Data.Builder().putString("error", "Watermark failed: ${e.message}").build())
		}
	}
}