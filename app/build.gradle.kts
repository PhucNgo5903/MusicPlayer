plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.musicplayer"
    compileSdk = 34 // Giữ nguyên 34 (Android 14) là chuẩn nhất hiện tại

    defaultConfig {
        applicationId = "com.example.musicplayer"
        minSdk = 24
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

    // Quan trọng: Tắt Compose để không bị xung đột phiên bản
    buildFeatures {
        viewBinding = true
        compose = false
    }
}

dependencies {
    // --- Core Android (Phiên bản ổn định cho SDK 34) ---
    implementation("androidx.core:core-ktx:1.12.0") // Đã hạ từ 1.17 xuống 1.12
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- Navigation ---
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // --- UI Components ---
    implementation("androidx.recyclerview:recyclerview:1.3.2") // Đã hạ từ 1.4.0 xuống 1.3.2
    implementation("androidx.cardview:cardview:1.0.0")

    // --- Room Database ---
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // --- Network (Retrofit & Moshi) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")

    // --- ViewModel & LiveData ---
    val lifecycleVersion = "2.6.2" // Đã hạ từ 2.10 xuống 2.6.2
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

    // LƯU Ý: Tuyệt đối KHÔNG thêm lifecycle-runtime-compose hay activity-compose
    // vì chúng sẽ kéo theo lỗi phiên bản Compose.

    // --- Glide (Image Loader) ---
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // kapt("com.github.bumptech.glide:compiler:4.16.0")

    // --- Work ---
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // --- Testing ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


}