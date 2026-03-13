plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("org.jetbrains.kotlin.plugin.compose")
	id("org.jetbrains.kotlin.plugin.serialization")

}

android {
	namespace = "ru.mishlak.rksmp_pr1_4"
	compileSdk = 36

	defaultConfig {
		applicationId = "ru.mishlak.rksmp_pr1_4"
		minSdk = 24
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlin {
		compilerOptions {
			jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
		}
	}
	buildFeatures {
		compose = true
	}
}

dependencies {
	implementation(platform("androidx.compose:compose-bom:2026.03.00")) // стабильная версия

	implementation("androidx.core:core-ktx:1.18.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
	implementation("androidx.activity:activity-compose:1.13.0")

	// Compose
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.compose.material:material-icons-extended")
	implementation("androidx.compose.runtime:runtime")

	// Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

	// Serialization
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

	// Coil
	implementation("io.coil-kt.coil3:coil-compose:3.4.0")

	// ViewModel
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
	implementation("io.coil-kt.coil3:coil-network-okhttp:3.4.0")

	// Testing
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.3.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
	androidTestImplementation(platform("androidx.compose:compose-bom:2026.03.00"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")
}