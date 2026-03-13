package ru.mishlak.rksmp_pr1_4

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.mishlak.rksmp_pr1_4.ui.theme.*

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			Rksmp_pr14Theme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					FeedScreen(viewModel = viewModel ());
				}
			}
		}
	}
}