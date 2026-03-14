package ru.mishlak.rksmp_pr1_6

import android.content.Intent
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.mishlak.rksmp_pr1_6.ui.theme.*

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			TimerScreen()
		}
	}
}

@Composable
fun TimerScreen() {
	val context = LocalContext.current
	var inputText by remember { mutableStateOf("") }

	Column(
		modifier = Modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		OutlinedTextField(
			value = inputText,
			onValueChange = { inputText = it },
			label = { Text("Введите секунды") }
		)
		Button(
			onClick = {
				val seconds = inputText.toLongOrNull()
				if (seconds != null && seconds > 0) {
					val intent = Intent(context, TimerService::class.java).apply {
						putExtra("seconds", seconds)
					}
					context.startService(intent)
				}
			},
			modifier = Modifier.padding(top = 16.dp)
		) {
			Text("Запустить таймер")
		}
	}
}