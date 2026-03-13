package ru.mishlak.rksmp_pr1_4

sealed interface FeedState {
	data object Loading : FeedState
	data class Success(val posts: List<PostWithDetails>) : FeedState
	data class Error(val message: String) : FeedState
}