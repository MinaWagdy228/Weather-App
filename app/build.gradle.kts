import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.wizzar"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.wizzar"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.inputStream())
        }
        val apiKey = properties.getProperty("WEATHER_API_KEY")
            ?: throw GradleException("WEATHER_API_KEY not found in local.properties")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.hilt.work)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Core (already present mostly)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")

    // Retrofit + Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    // WorkManager (Background tasks)
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.appcompat:appcompat:1.7.0") // for locale switching
    implementation("com.google.accompanist:accompanist-flowlayout:0.34.0") // FlowRow in AlertsScreen
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.google.android.gms:play-services-location:21.1.0") // For location services

    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
        // ... your existing dependencies ...

        // MockK: The industry standard for mocking in Kotlin
        testImplementation("io.mockk:mockk:1.13.8")
        androidTestImplementation("io.mockk:mockk-android:1.13.8")

        // Coroutines Testing: For controlling time and testing suspend functions
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
        androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

        // Turbine: The absolute best library for testing Kotlin Flows
        testImplementation("app.cash.turbine:turbine:1.1.0")
        androidTestImplementation("app.cash.turbine:turbine:1.1.0")

        // Architecture Components Core Testing (InstantTaskExecutorRule)
        testImplementation("androidx.arch.core:core-testing:2.2.0")

        // Room Testing: For testing your DAOs locally
        androidTestImplementation("androidx.room:room-testing:2.6.1")

        // Google Truth: For very readable assertions (e.g., assertThat(x).isEqualTo(y))
        testImplementation("com.google.truth:truth:1.4.2")
        androidTestImplementation("com.google.truth:truth:1.4.2")
        testImplementation(kotlin("test"))

}