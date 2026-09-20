plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.tumbuhsehat.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tumbuhsehat.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // -- Jetpack Compose (versions managed by the BOM) --
    implementation(platform("androidx.compose:compose-bom:2026.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // -- Core / lifecycle / activity --
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    // -- Navigation Compose --
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // -- Room (local database) --
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    // -- WorkManager (local reminders) --
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // -- Vico (chart library built for Compose) --
    // Check https://github.com/patrykandpatrick/vico for newer releases when you sync this project.
    implementation("com.patrykandpatryk.vico:compose:2.2.0")
    implementation("com.patrykandpatryk.vico:compose-m3:2.2.0")

    // -- JSON parsing for the bundled reference data (jadwal imunisasi, tahapan MPASI, resep) --
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // -- Coroutines --
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // -- Testing --
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
