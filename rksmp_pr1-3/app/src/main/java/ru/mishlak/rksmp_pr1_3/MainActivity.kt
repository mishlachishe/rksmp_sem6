package ru.mishlak.rksmp_pr1_3

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.material3.*
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerialName
import ru.mishlak.rksmp_pr1_3.ui.theme.Rksmp_pr13Theme
import java.io.IOException


// Модель данных с правильными именами полей
@Serializable
data class Repo(
	val id: Int,
	@SerialName("full_name") val fullName: String,
	val description: String?,
	@SerialName("stargazers_count") val stargazersCount: Int,
	val language: String?
)

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()

	var query by remember { mutableStateOf("") }
	var searchResults by remember { mutableStateOf<List<Repo>>(emptyList()) }
	var isLoading by remember { mutableStateOf(false) }
	var isInitialLoading by remember { mutableStateOf(true) }
	var errorMessage by remember { mutableStateOf<String?>(null) }

	val allRepos = remember { mutableStateOf<List<Repo>?>(null) }

	LaunchedEffect(Unit) {
		isInitialLoading = true
		errorMessage = null
		try {
			val repos = loadReposFromAssets(context)
			allRepos.value = repos
		} catch (e: Exception) {
			errorMessage = "Ошибка загрузки данных: ${e.message}"
		} finally {
			isInitialLoading = false
		}
	}

	var searchJob by remember { mutableStateOf<Job?>(null) }

	fun onQueryChange(newQuery: String) {
		query = newQuery
		searchJob?.cancel()
		searchJob = coroutineScope.launch {
			delay(500) // debounce
			isLoading = true
			errorMessage = null
			try {
				val results = withContext(Dispatchers.IO) {
					delay(500) // имитация сети
					val repos = allRepos.value ?: emptyList()
					if (newQuery.isBlank()) {
						emptyList()
					} else {
						repos.filter { repo ->
							repo.fullName.contains(newQuery, ignoreCase = true) ||
									repo.description?.contains(newQuery, ignoreCase = true) == true
						}
					}
				}
				searchResults = results
			} catch (e: CancellationException) {
				// отменено — ничего не делаем
			} catch (e: Exception) {
				errorMessage = "Ошибка поиска: ${e.message}"
			} finally {
				isLoading = false
			}
		}
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		OutlinedTextField(
			value = query,
			onValueChange = { onQueryChange(it) },
			label = { Text("Введите название репозитория") },
			modifier = Modifier.fillMaxWidth(),
			enabled = !isInitialLoading
		)

		Spacer(modifier = Modifier.height(16.dp))

		Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
			when {
				isInitialLoading -> CircularProgressIndicator()
				errorMessage != null -> Text(
					text = errorMessage!!,
					color = MaterialTheme.colorScheme.error
				)
				isLoading && searchResults.isEmpty() -> CircularProgressIndicator()
				else -> {
					LazyColumn {
						items(searchResults) { repo ->
							RepoItem(repo = repo)
						}
					}
				}
			}
		}
	}
}

@Composable
fun RepoItem(repo: Repo) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp)
	) {
		Text(
			text = repo.fullName,
			style = MaterialTheme.typography.titleMedium
		)
		repo.description?.let {
			Text(
				text = it,
				style = MaterialTheme.typography.bodyMedium
			)
		}
		Text(
			text = "⭐ ${repo.stargazersCount}  |  ${repo.language ?: "Unknown"}",
			style = MaterialTheme.typography.bodySmall
		)
	}
}

suspend fun loadReposFromAssets(context: Context): List<Repo> = withContext(Dispatchers.IO) {
	val jsonString = try {
		context.assets.open("github_repos.json").bufferedReader().use { it.readText() }
	} catch (e: IOException) {
		throw Exception("Не удалось прочитать файл github_repos.json", e)
	}
	try {
		Json.decodeFromString<List<Repo>>(jsonString)
	} catch (e: Exception) {
		throw Exception("Ошибка парсинга JSON", e)
	}
}
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			Rksmp_pr13Theme() {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					SearchScreen(modifier = Modifier.padding(innerPadding));
				}
			}
		}
	}
}