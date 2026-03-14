package ru.mishlak.rksmp_pr1_10

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.IOException
import java.util.Locale

class MainActivity : ComponentActivity() {

	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private var onPermissionGranted: (() -> Unit)? = null

	private val requestPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { permissions ->
		val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
		val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
		if (fineGranted || coarseGranted) {
			onPermissionGranted?.invoke()
		} else {
			Toast.makeText(this, "Разрешения на геолокацию не предоставлены", Toast.LENGTH_SHORT).show()
		}
		onPermissionGranted = null
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

		setContent {
			MaterialTheme {
				LocationApp(
					fusedLocationClient = fusedLocationClient,
					requestPermissions = { permissions, callback ->
						onPermissionGranted = callback
						requestPermissionLauncher.launch(permissions)
					}
				)
			}
		}
	}
}

@Composable
fun LocationApp(
	fusedLocationClient: FusedLocationProviderClient,
	requestPermissions: (Array<String>, () -> Unit) -> Unit
) {
	val context = LocalContext.current
	var isLoading by remember { mutableStateOf(false) }
	var addressText by remember { mutableStateOf("") }
	var coordinatesText by remember { mutableStateOf("") }
	var errorMessage by remember { mutableStateOf<String?>(null) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Button(
			onClick = {
				val hasFine = ContextCompat.checkSelfPermission(
					context, android.Manifest.permission.ACCESS_FINE_LOCATION
				) == PackageManager.PERMISSION_GRANTED
				val hasCoarse = ContextCompat.checkSelfPermission(
					context, android.Manifest.permission.ACCESS_COARSE_LOCATION
				) == PackageManager.PERMISSION_GRANTED

				if (hasFine || hasCoarse) {
					fetchLocation(
						fusedLocationClient = fusedLocationClient,
						context = context,
						onStart = { isLoading = true; errorMessage = null },
						onSuccess = { address, coords ->
							isLoading = false
							addressText = address
							coordinatesText = coords
						},
						onError = { message ->
							isLoading = false
							errorMessage = message
						}
					)
				} else {
					requestPermissions(
						arrayOf(
							android.Manifest.permission.ACCESS_FINE_LOCATION,
							android.Manifest.permission.ACCESS_COARSE_LOCATION
						)
					) {
						fetchLocation(
							fusedLocationClient = fusedLocationClient,
							context = context,
							onStart = { isLoading = true; errorMessage = null },
							onSuccess = { address, coords ->
								isLoading = false
								addressText = address
								coordinatesText = coords
							},
							onError = { message ->
								isLoading = false
								errorMessage = message
							}
						)
					}
				}
			},
			enabled = !isLoading
		) {
			Text("Получить мой адрес")
		}

		Spacer(modifier = Modifier.height(24.dp))

		if (isLoading) {
			CircularProgressIndicator()
		}

		errorMessage?.let {
			Text(
				text = it,
				color = MaterialTheme.colorScheme.error,
				modifier = Modifier.padding(8.dp)
			)
		}

		if (addressText.isNotEmpty()) {
			Text(
				text = addressText,
				fontSize = 20.sp,
				modifier = Modifier.padding(8.dp)
			)
		}

		if (coordinatesText.isNotEmpty()) {
			Text(
				text = coordinatesText,
				fontSize = 16.sp,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(8.dp)
			)
		}
	}
}

fun fetchLocation(
	fusedLocationClient: FusedLocationProviderClient,
	context: android.content.Context,
	onStart: () -> Unit,
	onSuccess: (String, String) -> Unit,
	onError: (String) -> Unit
) {
	onStart()

	val hasFine = ContextCompat.checkSelfPermission(
		context, android.Manifest.permission.ACCESS_FINE_LOCATION
	) == PackageManager.PERMISSION_GRANTED
	val hasCoarse = ContextCompat.checkSelfPermission(
		context, android.Manifest.permission.ACCESS_COARSE_LOCATION
	) == PackageManager.PERMISSION_GRANTED

	if (!hasFine && !hasCoarse) {
		onError("Нет разрешений на геолокацию. Пожалуйста, предоставьте доступ.")
		return
	}

	val currentLocationRequest = CurrentLocationRequest.Builder()
		.setPriority(Priority.PRIORITY_HIGH_ACCURACY)
		.setMaxUpdateAgeMillis(10000)  // принимаем кэш не старше 10 секунд
		.setDurationMillis(5000)        // таймаут запроса
		.build()

	val cancellationTokenSource = CancellationTokenSource()

	fusedLocationClient.getCurrentLocation(currentLocationRequest, cancellationTokenSource.token)
		.addOnSuccessListener { location ->
			if (location == null) {
				onError("Не удалось получить местоположение. Попробуйте включить GPS или Wi-Fi.")
				return@addOnSuccessListener
			}
			val lat = location.latitude
			val lng = location.longitude
			val coords = String.format("%.6f, %.6f", lat, lng)

			val geocoder = Geocoder(context, Locale.getDefault())
			try {
				val addresses = geocoder.getFromLocation(lat, lng, 1)
				if (!addresses.isNullOrEmpty()) {
					val address = addresses[0]
					val fullAddress = address.getAddressLine(0) ?: "Адрес не найден"
					onSuccess(fullAddress, coords)
				} else {
					onSuccess("Адрес не найден", coords)
				}
			} catch (e: IOException) {
				onError("Ошибка геокодирования: ${e.message}. Проверьте интернет.")
			} catch (e: IllegalArgumentException) {
				onError("Некорректные координаты")
			}
		}
		.addOnFailureListener { exception ->
			onError("Ошибка получения локации: ${exception.message}")
		}
}