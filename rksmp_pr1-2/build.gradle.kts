plugins {
	kotlin("jvm") version "2.3.10"
	application
}
group = "ru.mishlak"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}
application {
	mainClass.set("ru.mishlak.MainKt")
}