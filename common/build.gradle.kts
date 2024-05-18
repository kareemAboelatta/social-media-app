plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id ("dagger.hilt.android.plugin")

    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")

    id("com.google.devtools.ksp")

}

android {
    namespace = "com.example.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()

    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    kapt {
        correctErrorTypes = true
    }

}

dependencies {

    api(project(":core"))


    //Dagger - Hilt
    api ("com.google.dagger:hilt-android:2.50")
    kapt ("com.google.dagger:hilt-android-compiler:2.50")
    kapt ("androidx.hilt:hilt-compiler:1.2.0")


    // Firebase
    api("com.google.firebase:firebase-analytics:22.0.0")
    api("com.google.android.gms:play-services-auth:21.1.1")
    api(platform("com.google.firebase:firebase-bom:33.0.0"))
    api("com.google.firebase:firebase-auth-ktx:23.0.0")
    api("com.google.firebase:firebase-storage-ktx:21.0.0")
    api("com.google.firebase:firebase-database-ktx:21.0.0")
    api("com.google.firebase:firebase-messaging-ktx:24.0.0")
    // ML Kit
    api("com.google.mlkit:translate:17.0.2")
    api("com.google.mlkit:language-id:17.0.5")


    // Chip Navigation Bar
    api("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
    // ReadMoreTextView
    api("com.borjabravo:readmoretextview:2.1.0")


    // ExoPlayer
    api("com.google.android.exoplayer:exoplayer:2.19.1")
    api("com.google.android.exoplayer:exoplayer-core:2.19.1")
    api("com.google.android.exoplayer:exoplayer-ui:2.19.1")


}