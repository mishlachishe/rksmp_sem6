package ru.mishlak.rksmp_pr1_4

data class PostWithDetails(
	val post: Post,
	val avatarUrl: String? = null,
	val comments: List<Comment> = emptyList(),
	val isLoadingAvatar: Boolean = true,
	val isLoadingComments: Boolean = true,
	val isAvatarError: Boolean = false,
	val isCommentsError: Boolean = false
)