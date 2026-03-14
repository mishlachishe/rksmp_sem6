package ru.mishlak.rksmp_pr1_8.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.mishlak.rksmp_pr1_8.workers.CompressWorker
import ru.mishlak.rksmp_pr1_8.workers.WatermarkWorker
import ru.mishlak.rksmp_pr1_8.workers.UploadWorker

class MainViewModel(application: Application) : AndroidViewModel(application) {

	private val workManager = WorkManager.getInstance(application)
	private val uniqueWorkName = "photo_processing"

	val workInfos: StateFlow<List<WorkInfo>> = workManager
		.getWorkInfosForUniqueWorkLiveData(uniqueWorkName)
		.asFlow()
		.map { it.orEmpty() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = emptyList()
		)

	fun startProcessing() {
		val compressRequest = OneTimeWorkRequestBuilder<CompressWorker>()
			.addTag("step_compress")
			.setInputData(workDataOf("input_file" to "sample_photo.jpg"))
			.build()

		val watermarkRequest = OneTimeWorkRequestBuilder<WatermarkWorker>()
			.addTag("step_watermark")
			.build()

		val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
			.addTag("step_upload")
			.build()

		workManager.beginUniqueWork(
			uniqueWorkName,
			ExistingWorkPolicy.REPLACE,
			compressRequest
		)
			.then(watermarkRequest)
			.then(uploadRequest)
			.enqueue()
	}

	fun cancelProcessing() {
		workManager.cancelUniqueWork(uniqueWorkName)
	}
}