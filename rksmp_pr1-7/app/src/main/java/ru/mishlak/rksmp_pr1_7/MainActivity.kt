package ru.mishlak.rksmp_pr1_7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

	private val viewModel: MainViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			MyApp(viewModel)
		}
	}

	override fun onStop() {
		super.onStop()
		viewModel.unbindService(this)
	}
}

@Composable
fun MyApp(viewModel: MainViewModel = viewModel()) {
	val number by viewModel.currentNumber.collectAsState()
	val isConnected by viewModel.isConnected.collectAsState()
	val context = LocalContext.current

	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = number.toString(),
			fontSize = 48.sp
		)
		Spacer(modifier = Modifier.height(16.dp))
		Button(
			onClick = {
				if (isConnected) {
					viewModel.unbindService(context)
				} else {
					viewModel.bindService(context)
				}
			}
		) {
			Text(if (isConnected) "Отключиться" else "Подключиться")
		}
	}
}