package ru.mishlak.rksmp_pr1_8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.mishlak.rksmp_pr1_8.ui.theme.Rksmp_pr18Theme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			Rksmp_pr18Theme {
				MainScreen()
			}
		}
	}
}