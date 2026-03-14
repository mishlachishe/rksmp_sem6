package ru.mishlak.rksmp_pr1_14

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CompassViewModel(application: Application) : AndroidViewModel(application) {
	private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
	fun hasRotationVectorSensor(): Boolean {
		return sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null
	}
	fun getAzimuthFlow(): Flow<Float> = callbackFlow {
		val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
			?: throw IllegalStateException("Rotation vector sensor not available")

		val listener = object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent?) {
				event?.let {
					val azimuth = computeAzimuth(it.values)
					trySend(azimuth)
				}
			}
			override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
		}

		sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
		awaitClose {
			sensorManager.unregisterListener(listener)
		}
	}
	private fun computeAzimuth(values: FloatArray): Float {
		val rotationMatrix = FloatArray(9)
		SensorManager.getRotationMatrixFromVector(rotationMatrix, values)
		val orientation = FloatArray(3)
		SensorManager.getOrientation(rotationMatrix, orientation)
		var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
		if (azimuth < 0) azimuth += 360f
		return azimuth
	}
}