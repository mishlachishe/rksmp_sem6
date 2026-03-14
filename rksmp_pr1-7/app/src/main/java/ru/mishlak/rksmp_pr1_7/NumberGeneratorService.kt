package ru.mishlak.rksmp_pr1_7

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

class NumberGeneratorService : Service() {

	private val binder = LocalBinder()
	private var listener: NumberListener? = null
	private val handler = Handler(Looper.getMainLooper())
	private val generateRunnable = object : Runnable {
		override fun run() {
			val number = Random.nextInt(0, 101)
			listener?.onNumberGenerated(number)
			handler.postDelayed(this, 1000)
		}
	}

	fun interface NumberListener {
		fun onNumberGenerated(number: Int)
	}

	inner class LocalBinder : Binder() {
		fun getService(): NumberGeneratorService = this@NumberGeneratorService
	}

	override fun onBind(intent: Intent): IBinder = binder

	fun registerListener(listener: NumberListener) {
		this.listener = listener
	}

	fun unregisterListener() {
		this.listener = null
	}

	override fun onCreate() {
		super.onCreate()
		handler.post(generateRunnable)
	}

	override fun onDestroy() {
		handler.removeCallbacks(generateRunnable)
		super.onDestroy()
	}
}