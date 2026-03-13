package ru.mishlak.rksmp_pr1_4

import android.content.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import java.io.*

object JsonLoader {
	private val json = Json { ignoreUnknownKeys = true }

	suspend fun loadPosts(context: Context): List<Post> = withContext(Dispatchers.IO) {
		delay(1500) // имитация сети
		val inputStream: InputStream = context.assets.open("social_posts.json")
		val jsonString = inputStream.bufferedReader().use { it.readText() }
		return@withContext json.decodeFromString<List<Post>>(jsonString)
	}

	suspend fun loadComments(context: Context): List<Comment> = withContext(Dispatchers.IO) {
		delay(2000) // имитация сети
		val inputStream: InputStream = context.assets.open("comments.json")
		val jsonString = inputStream.bufferedReader().use { it.readText() }
		return@withContext json.decodeFromString<List<Comment>>(jsonString)
	}
}