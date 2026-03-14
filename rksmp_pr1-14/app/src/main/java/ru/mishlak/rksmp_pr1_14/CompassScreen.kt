package ru.mishlak.rksmp_pr1_14

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.catch


@Composable
fun CompassScreen(viewModel: CompassViewModel = viewModel()) {
	val lifecycleOwner = LocalLifecycleOwner.current
	val context = LocalContext.current
	var azimuth by remember { mutableStateOf(0f) }
	var hasSensor by remember { mutableStateOf(true) }
	var errorMessage by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		hasSensor = viewModel.hasRotationVectorSensor()
		if (!hasSensor) {
			errorMessage = "Устройство не поддерживает датчик ориентации"
		}
	}
	LaunchedEffect(Unit) {
		if (!hasSensor) return@LaunchedEffect
		lifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
			viewModel.getAzimuthFlow()
				.catch {
					errorMessage = "Ошибка получения данных сенсора"
				}
				.collect { newAzimuth ->
					azimuth = newAzimuth
				}
		}
	}
	val animatedAzimuth = remember { Animatable(0f) }
	LaunchedEffect(azimuth) {
		animatedAzimuth.animateTo(azimuth, animationSpec = tween(durationMillis = 150))
	}
	Box(modifier = Modifier.fillMaxSize()) {
		if (errorMessage != null) {
			Text(
				text = errorMessage!!,
				color = Color.Red,
				fontSize = 24.sp,
				modifier = Modifier.align(Alignment.Center)
			)
		} else {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier.fillMaxSize()
			) {
				Text(
					text = "Компас",
					fontSize = 32.sp,
					modifier = Modifier.padding(top = 32.dp)
				)
				Spacer(modifier = Modifier.weight(1f))
				Compass(
					azimuth = animatedAzimuth.value,
					modifier = Modifier
						.size(300.dp)
						.padding(16.dp)
				)
				Spacer(modifier = Modifier.weight(0.5f))
				Text(
					text = "Азимут: ${azimuth.toInt()}°",
					fontSize = 28.sp,
					modifier = Modifier.padding(bottom = 32.dp)
				)
				Spacer(modifier = Modifier.weight(1f))
			}
		}
	}
}

@Composable
fun Compass(azimuth: Float, modifier: Modifier = Modifier) {
	Canvas(modifier = modifier) {
		val width = size.width
		val height = size.height
		val radius = minOf(width, height) / 2f
		val centerX = width / 2f
		val centerY = height / 2f

		// 1. Круглый диск
		drawCircle(
			color = Color.DarkGray,
			radius = radius,
			center = Offset(centerX, centerY)
		)
		drawCircle(
			color = Color.LightGray,
			radius = radius * 0.98f,
			center = Offset(centerX, centerY),
			style = Stroke(width = 2.dp.toPx())
		)

		rotate(azimuth, pivot = Offset(centerX, centerY)) {
			val arrowLength = radius * 0.8f   // 80% радиуса
			val arrowWidth = radius * 0.25f   // ширина стрелки

			val northTip = Offset(centerX, centerY - arrowLength)
			val southTip = Offset(centerX, centerY + arrowLength)
			val leftBase = Offset(centerX - arrowWidth, centerY)
			val rightBase = Offset(centerX + arrowWidth, centerY)

			drawPath(
				path = Path().apply {
					moveTo(northTip.x, northTip.y)
					lineTo(leftBase.x, leftBase.y)
					lineTo(rightBase.x, rightBase.y)
					close()
				},
				color = Color.Red
			)
			drawPath(
				path = Path().apply {
					moveTo(southTip.x, southTip.y)
					lineTo(leftBase.x, leftBase.y)
					lineTo(rightBase.x, rightBase.y)
					close()
				},
				color = Color.Gray
			)

			drawContext.canvas.nativeCanvas.apply {
				val paint = Paint().apply {
					color = android.graphics.Color.WHITE
					textSize = 40f
					textAlign = Paint.Align.CENTER
					isFakeBoldText = true
				}
				drawText("N", centerX, centerY - arrowLength * 1.1f, paint)
			}
		}
	}
}