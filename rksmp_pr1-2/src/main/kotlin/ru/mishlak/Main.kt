package ru.mishlak

import kotlinx.coroutines.*
import java.io.*
import java.security.*
import kotlin.system.*

fun computeSha256(file: File): String {
	val digest = MessageDigest.getInstance("SHA-256")
	file.inputStream().use { fis ->
		val buffer = ByteArray(8192)
		var bytesRead: Int
		while (fis.read(buffer).also { bytesRead = it } != -1) {
			digest.update(buffer, 0, bytesRead)
		}
	}
	return digest.digest().joinToString("") { "%02x".format(it) }
}

fun main(args: Array<String>) {
	System.setOut(java.io.PrintStream(System.out, true, "UTF-8"))
	val path = args.getOrElse(0) { "./test_files" }
	val timeout = args.getOrElse(1) { "5" }.toLongOrNull() ?: 5
	println("Поиск json в -> $path")
	println("Таймаут -> $timeout сек.")
	runBlocking {
		val result = withTimeoutOrNull(timeout * 1000) {
			findDuplicates(path)
		}
		if (result == null) {
			println("Поиск прерван по таймауту")
		} else {
			printDuplicates(result)
		}
	}
	exitProcess(0)
}

suspend fun findDuplicates(path: String): Map<String, List<String>> = coroutineScope {
	val rootDir = File(path)
	if (!rootDir.exists() || !rootDir.isDirectory) {
		println("Ошибка -> директория не существует или не является папкой")
		return@coroutineScope emptyMap()
	}
	val jsonFiles =
		rootDir.walkTopDown().filter { it.isFile && it.extension.equals("json", ignoreCase = true) }.toList()
	if (jsonFiles.isEmpty()) {
		println("Json файлы не найдены")
		return@coroutineScope emptyMap()
	}
	println("Найдено файлов -> ${jsonFiles.size}")
	val deferredResults = jsonFiles.map { file ->
		async(Dispatchers.IO) {
			try {
				file.absolutePath to computeSha256(file)
			} catch (e: Exception) {
				println("Ошибка чтения файла ${file.name} -> ${e.message}")
				null
			}
		}
	}
	val results = deferredResults.awaitAll().filterNotNull()
	results.groupBy({ it.second }, { it.first }).filterValues { it.size > 1 }
}

fun printDuplicates(duplicates: Map<String, List<String>>) {
	if (duplicates.isEmpty()) {
		println("Дубликаты не найдены")
		return
	}
	println("Найдены дубликаты")
	duplicates.forEach { (hash, paths) ->
		println("Хеш: $hash")
		paths.forEachIndexed { index, path ->
			println("  ${index + 1}. $path")
		}
		println()
	}
}