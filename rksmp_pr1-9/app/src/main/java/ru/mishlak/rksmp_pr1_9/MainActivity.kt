package ru.mishlak.rksmp_pr1_9

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			MaterialTheme(colorScheme = darkColorScheme()) {
				MainScreen()
			}
		}
	}
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
	val context = LocalContext.current
	val isLoading by viewModel.isLoading.collectAsState()
	val averageTemp by viewModel.averageTemp.collectAsState()
	val cities by viewModel.cities.collectAsState()

	val permissionLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission()
	) { }

	LaunchedEffect(Unit) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
				permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
			}
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		Text(
			text = "🌤 Прогноз погоды",
			fontSize = 28.sp,
			modifier = Modifier.padding(bottom = 16.dp)
		)

		// Список городов
		LazyColumn(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			items(cities) { city ->
				CityWeatherCard(city)
			}
		}

		// Кнопка и результат
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.fillMaxWidth()
		) {
			Button(
				onClick = {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
						if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
							viewModel.startWork()
						} else {
							permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
						}
					} else {
						viewModel.startWork()
					}
				},
				enabled = !isLoading,
				modifier = Modifier.fillMaxWidth()
			) {
				Text(if (isLoading) "Сбор данных..." else "Собрать прогноз")
			}

			if (isLoading) {
				LinearProgressIndicator(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 16.dp)
				)
			}

			averageTemp?.let {
				Card(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 16.dp),
					colors = CardDefaults.cardColors(
						containerColor = MaterialTheme.colorScheme.primaryContainer
					)
				) {
					Text(
						text = "📊 Средняя температура: %.1f°C".format(it),
						fontSize = 18.sp,
						modifier = Modifier.padding(16.dp)
					)
				}
			}
		}
	}
}

@Composable
fun CityWeatherCard(city: CityWeather) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = when (city.status) {
				CityStatus.PENDING -> MaterialTheme.colorScheme.surfaceVariant
				CityStatus.LOADING -> MaterialTheme.colorScheme.secondaryContainer
				CityStatus.LOADED -> MaterialTheme.colorScheme.primaryContainer
				CityStatus.ERROR -> MaterialTheme.colorScheme.errorContainer
			}
		)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Column {
				Text(
					text = city.name,
					fontSize = 20.sp,
					fontWeight = FontWeight.Bold
				)
				Text(
					text = when (city.status) {
						CityStatus.PENDING -> "Ожидание"
						CityStatus.LOADING -> "Загружается..."
						CityStatus.LOADED -> "Загружено"
						CityStatus.ERROR -> "Ошибка"
					},
					fontSize = 14.sp,
					color = when (city.status) {
						CityStatus.PENDING -> Color.Gray
						CityStatus.LOADING -> MaterialTheme.colorScheme.primary
						CityStatus.LOADED -> MaterialTheme.colorScheme.primary
						CityStatus.ERROR -> MaterialTheme.colorScheme.error
					}
				)
			}

			city.temperature?.let {
				Surface(
					color = MaterialTheme.colorScheme.tertiaryContainer,
					shape = MaterialTheme.shapes.medium
				) {
					Text(
						text = "$it°C",
						fontSize = 24.sp,
						fontWeight = FontWeight.Bold,
						modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
					)
				}
			}
		}
	}
}