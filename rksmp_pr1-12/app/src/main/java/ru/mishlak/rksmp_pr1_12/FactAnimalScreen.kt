package ru.mishlak.rksmp_pr1_12

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactAnimalScreen(
	viewModel: FactAnimalViewModel = viewModel()
) {
	val fact by viewModel.fact.collectAsState()
	val isLoading by viewModel.isLoading.collectAsState()

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Случайные факты о животных") }
			)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(24.dp)
		) {
			AnimatedVisibility(
				visible = fact != null && !isLoading,
				enter = fadeIn(animationSpec = tween(500)) +
						scaleIn(initialScale = 0.8f, animationSpec = tween(500))
			) {
				Card(
					modifier = Modifier.fillMaxWidth(),
					elevation = CardDefaults.cardElevation(6.dp)
				) {
					Text(
						text = fact ?: "",
						modifier = Modifier.padding(20.dp),
						fontSize = 18.sp,
						style = MaterialTheme.typography.bodyLarge
					)
				}
			}
			if (isLoading) {
				CircularProgressIndicator(modifier = Modifier.size(48.dp))
			}
			Spacer(modifier = Modifier.weight(1f))
			Button(
				onClick = { viewModel.loadNewFact() },
				enabled = !isLoading,
				modifier = Modifier.fillMaxWidth()
			) {
				Text("Новый факт!", fontSize = 18.sp)
			}
		}
	}
}