package ru.mishlak.rksmp_pr1_8.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class CompressWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
	override suspend fun doWork(): Result {
		val inputFile = inputData.getString("input_file") ?: "default.jpg"
		return try {
			for (i in 0..100 step 10) {
				if (!coroutineContext.isActive) return Result.failure()
				setProgress(Data.Builder().putInt("progress", i).build())
				delay(200)
			}
			val outputFile = "compressed_$inputFile"
			Result.success(Data.Builder().putString("compressed_file", outputFile).build())
		} catch (e: Exception) {
			Result.failure(Data.Builder().putString("error", "Compress failed: ${e.message}").build())
		}
	}
}