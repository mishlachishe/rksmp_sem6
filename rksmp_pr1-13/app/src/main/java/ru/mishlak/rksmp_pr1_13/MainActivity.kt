package ru.mishlak.rksmp_pr1_13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.mishlak.rksmp_pr1_13.ui.theme.Rksmp_pr112Theme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			Rksmp_pr112Theme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					CurrencyScreen()
				}
			}
		}
	}
}

@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel = viewModel()) {
	val rate by viewModel.rate.collectAsState()
	val direction by viewModel.direction.collectAsState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		// Заголовок
		Text(
			text = "USD / RUB",
			fontSize = 28.sp,
			fontWeight = FontWeight.Medium,
			color = MaterialTheme.colorScheme.primary
		)

		Spacer(modifier = Modifier.height(32.dp))
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center
		) {
			Text(
				text = "%.2f".format(rate),
				fontSize = 64.sp,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onBackground
			)

			Spacer(modifier = Modifier.width(16.dp))
			when (direction) {
				1 -> Icon(
					imageVector = Icons.Default.ArrowUpward,
					contentDescription = "Up",
					tint = Color.Green,
					modifier = Modifier.size(40.dp)
				)
				-1 -> Icon(
					imageVector = Icons.Default.ArrowDownward,
					contentDescription = "Down",
					tint = Color.Red,
					modifier = Modifier.size(40.dp)
				)
				else -> Spacer(modifier = Modifier.size(40.dp))
			}
		}

		Spacer(modifier = Modifier.height(48.dp))

		// Кнопка принудительного обновления
		Button(
			onClick = { viewModel.refreshRate() },
			modifier = Modifier.fillMaxWidth(0.6f)
		) {
			Text(text = "Обновить сейчас", fontSize = 18.sp)
		}
	}
}