package ru.mishlak.rksmp_pr1_8

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import ru.mishlak.rksmp_pr1_8.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
	val workInfos by viewModel.workInfos.collectAsState(initial = emptyList())

	val isProcessing = workInfos.any {
		it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED
	}

	val currentWorkInfo = workInfos.firstOrNull { it.state == WorkInfo.State.RUNNING }
	val stepText = when (currentWorkInfo?.tags?.firstOrNull { it.startsWith("step_") }) {
		"step_compress" -> "Сжимаем фото..."
		"step_watermark" -> "Добавляем водяной знак..."
		"step_upload"   -> "Загружаем..."
		else -> if (workInfos.all { it.state == WorkInfo.State.SUCCEEDED }) "Готово!" else "Ожидание..."
	}

	val progress = currentWorkInfo?.progress?.getInt("progress", 0) ?: 0
	val result = workInfos
		.find { it.state == WorkInfo.State.SUCCEEDED && it.tags.contains("step_upload") }
		?.outputData
		?.getString("uploaded_file")

	val error = workInfos
		.find { it.state == WorkInfo.State.FAILED }
		?.outputData
		?.getString("error")
		?: workInfos.find { it.state == WorkInfo.State.FAILED }?.tags?.firstOrNull()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Button(
			onClick = { viewModel.startProcessing() },
			enabled = !isProcessing
		) {
			Text("Начать обработку и загрузку")
		}

		Spacer(modifier = Modifier.height(32.dp))

		if (isProcessing) {
			Text(
				text = stepText,
				style = MaterialTheme.typography.headlineSmall
			)
			Spacer(modifier = Modifier.height(16.dp))
			LinearProgressIndicator(
				progress = { progress / 100f },
				modifier = Modifier.fillMaxWidth()
			)
		} else if (result != null) {
			Text(
				text = "Готово! Фото загружено: $result",
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.primary
			)
		} else if (error != null) {
			Text(
				text = "Ошибка: $error",
				style = MaterialTheme.typography.bodyLarge,
				color = Color.Red
			)
		}
	}
}