package ru.mishlak.rksmp_pr1_4

import android.content.*
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.*

class FeedViewModel : ViewModel() {

	private val _feedState = MutableStateFlow<FeedState>(FeedState.Loading)
	val feedState: StateFlow<FeedState> = _feedState.asStateFlow()

	private var currentLoadingJob: Job? = null

	fun loadFeed(context: Context) {
		currentLoadingJob?.cancel()
		currentLoadingJob = viewModelScope.launch {
			_feedState.value = FeedState.Loading
			try {
				val posts = JsonLoader.loadPosts(context)
				val initialPosts = posts.map { PostWithDetails(post = it) }
				_feedState.value = FeedState.Success(initialPosts)
				posts.forEachIndexed { index, post ->
					launch {
						supervisorScope {
							val avatarDeferred = async {
								delay(1200)
								if (Random.nextBoolean()) {
									post.avatarUrl
								} else {
									throw Exception("Avatar load failed")
								}
							}
							val commentsDeferred = async {
								delay(1000)
								JsonLoader.loadComments(context).filter { it.postId == post.id }
							}
							val avatarResult = runCatching { avatarDeferred.await() }
							avatarResult.onSuccess { url ->
								println("Avatar loaded for post ${post.id}: $url");
							}.onFailure {
								println("Avatar failed for post ${post.id}: ${it.message}")
							}
							val commentsResult = runCatching { commentsDeferred.await() }
							_feedState.update { state ->
								when (state) {
									is FeedState.Success -> {
										val updatedPosts = state.posts.toMutableList()
										updatedPosts[index] = state.posts[index].copy(
											avatarUrl = avatarResult.getOrNull(),
											comments = commentsResult.getOrElse { emptyList() },
											isLoadingAvatar = false,
											isLoadingComments = false,
											isAvatarError = avatarResult.isFailure,
											isCommentsError = commentsResult.isFailure
										)
										FeedState.Success(updatedPosts)
									}
									else -> state
								}
							}
						}
					}
				}
			} catch (e: Exception) {
				_feedState.value = FeedState.Error(e.message ?: "Unknown error")
			}
		}
	}

	fun refresh(context: Context) {
		loadFeed(context)
	}
}