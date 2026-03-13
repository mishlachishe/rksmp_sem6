package ru.mishlak.rksmp_pr1_4

import kotlinx.serialization.*

@Serializable
data class Post(
	val id: Int,
	val userId: Int,
	val title: String,
	val body: String,
	val avatarUrl: String
)