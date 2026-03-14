package ru.mishlak.rksmp_pr1_11

// MainActivity.kt
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

	private val requestPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted ->
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContent {
			PillReminderTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					val viewModel: MainViewModel = viewModel()
					val isEnabled by viewModel.isEnabled.collectAsState()
					val nextReminder by viewModel.nextReminderText.collectAsState()

					var hasPermission by remember {
						mutableStateOf(
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
								checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
							} else true
						)
					}
					LaunchedEffect(Unit) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission) {
							requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
						}
					}

					ReminderScreen(
						isEnabled = isEnabled,
						nextReminderText = nextReminder,
						onToggle = {
							if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
								requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
							} else {
								viewModel.toggleReminder(hasPermission)
							}
						}
					)
				}
			}
		}
	}
}

@Composable
fun ReminderScreen(
	isEnabled: Boolean,
	nextReminderText: String,
	onToggle: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.padding(bottom = 32.dp)
		) {
			Box(
				modifier = Modifier
					.size(16.dp)
					.clip(CircleShape)
					.background(if (isEnabled) Color.Green else Color.Gray)
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				text = if (isEnabled) "Включено" else "Выключено",
				fontSize = 18.sp
			)
		}
		Text(
			text = "Напоминание о таблетке",
			fontSize = 28.sp,
			modifier = Modifier.padding(bottom = 16.dp)
		)
		if (isEnabled && nextReminderText.isNotEmpty()) {
			Text(
				text = nextReminderText,
				fontSize = 20.sp,
				modifier = Modifier.padding(vertical = 24.dp)
			)
		}
		Spacer(modifier = Modifier.height(32.dp))

		Button(
			onClick = onToggle,
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp)
		) {
			Text(
				text = if (isEnabled) "Выключить напоминание" else "Включить напоминание",
				fontSize = 18.sp
			)
		}
	}
}

@Composable
fun PillReminderTheme(content: @Composable () -> Unit) {
	MaterialTheme(colorScheme = darkColorScheme(), content = content)
}