plugins {
	kotlin("jvm") version "2.3.10"
	id("org.jetbrains.kotlin.plugin.serialization") version "2.3.10"
}

group = "ru.mishlak"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.10.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
}