package ru.mishlak.rksmp_pr1_4

import kotlinx.serialization.*

@Serializable
data class Comment (
	val postId: Int,
	val id: Int,
	val name: String,
	val body: String
)