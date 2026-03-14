package ru.mishlak.rksmp_pr1_12

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			val darkTheme = isSystemInDarkTheme()
			val colorScheme = if (darkTheme) {
				darkColorScheme()
			} else {
				darkColorScheme()
			}
			val systemUiController = rememberSystemUiController()
			SideEffect {
				systemUiController.setStatusBarColor(
					color = Color.Transparent,
					darkIcons = false
				)
			}
			MaterialTheme(
				colorScheme = colorScheme,
				content = { FactAnimalScreen() }
			)
		}
	}
}