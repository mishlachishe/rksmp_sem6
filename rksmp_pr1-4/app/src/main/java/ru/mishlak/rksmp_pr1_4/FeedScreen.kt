package ru.mishlak.rksmp_pr1_4

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import coil3.request.*

@Composable
fun FeedScreen(viewModel: FeedViewModel) {
	val context = LocalContext.current
	val state by viewModel.feedState.collectAsState()

	LaunchedEffect(Unit) {
		viewModel.loadFeed(context)
	}

	Column {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = "Социальная лента",
				style = MaterialTheme.typography.headlineSmall
			)
			Button(onClick = { viewModel.refresh(context) }) {
				Text("Обновить")
			}
		}
		Box(modifier = Modifier.weight(1f)) {
			when (state) {
				is FeedState.Loading -> {
					CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
				}
				is FeedState.Error -> {
					Text(
						text = "Ошибка: ${(state as FeedState.Error).message}",
						modifier = Modifier.align(Alignment.Center)
					)
				}
				is FeedState.Success -> {
					val posts = (state as FeedState.Success).posts
					LazyColumn {
						items(posts, key = { it.post.id }) { postWithDetails ->
							PostCard(
								postWithDetails = postWithDetails,
								modifier = Modifier.fillMaxWidth()
							)
						}
					}
				}
			}
		}
	}
}

@Composable
fun PostCard(
	postWithDetails: PostWithDetails,
	modifier: Modifier = Modifier
) {
	Card(
		modifier = modifier.padding(8.dp),
		elevation = CardDefaults.cardElevation(4.dp)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Box(modifier = Modifier.size(48.dp)) {
					when {
						postWithDetails.isLoadingAvatar -> {
							CircularProgressIndicator(modifier = Modifier.size(24.dp))
						}
						postWithDetails.isAvatarError -> {
							Icon(
								imageVector = Icons.Default.BrokenImage,
								contentDescription = "Avatar error",
								tint = MaterialTheme.colorScheme.error
							)
						}
						else -> {
							AsyncImage(
								model = postWithDetails.avatarUrl,
								contentDescription = "Avatar",
								modifier = Modifier.size(48.dp),
								error = painterResource(id = R.mipmap.broken),
								onError = { state ->
									println("Coil error for ${postWithDetails.post.id}: ${state.result.throwable}")
								}
							)
						}
					}
				}
				Spacer(modifier = Modifier.width(8.dp))
				Text(text = postWithDetails.post.title, style = MaterialTheme.typography.titleMedium)
			}

			Spacer(modifier = Modifier.height(8.dp))
			Text(text = postWithDetails.post.body, style = MaterialTheme.typography.bodyMedium)

			Spacer(modifier = Modifier.height(8.dp))
			when {
				postWithDetails.isLoadingComments -> {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Text("Загрузка комментариев...")
						CircularProgressIndicator(modifier = Modifier.size(16.dp))
					}
				}
				postWithDetails.isCommentsError -> {
					Text("Не удалось загрузить комментарии", color = MaterialTheme.colorScheme.error)
				}
				else -> {
					Text("Комментарии (${postWithDetails.comments.size}):")
					postWithDetails.comments.take(2).forEach { comment ->
						Text("• ${comment.name}: ${comment.body}", maxLines = 1)
					}
				}
			}
		}
	}
}