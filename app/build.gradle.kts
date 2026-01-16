plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.stocktracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.stocktracker"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        viewBinding = false // KITA MATIKAN KARENA FULL COMPOSE
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    // --- 1. CORE ANDROID ---
    implementation("androidx.core:core-ktx:1.12.0")
    // appcompat tetap dijaga untuk tema dasar, meskipun UI kita pakai Compose
    implementation("androidx.appcompat:appcompat:1.6.1")

    // --- 2. LIFECYCLE & VIEWMODEL ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // [PENTING] Integrasi ViewModel dengan Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.material)

    // --- 3. ROOM DATABASE ---
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // --- 4. NETWORKING ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // --- 5. COMPOSE UI (WAJAH APLIKASI) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    // Sesuaikan versi 1.5.4 dengan versi compose kamu jika perlu, atau biarkan gradle yang menyarankan.

    // [PENTING] Navigasi antar layar di Compose
    implementation(libs.androidx.navigation.compose)

    // [PENTING] Agar Compose bisa baca LiveData dari Database Room kamu
    implementation(libs.androidx.runtime.livedata)

    // Penyimpanan Setting (DataStore)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // --- TESTING ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}