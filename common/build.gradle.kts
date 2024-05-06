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

    api("androidx.core:core-ktx:1.13.1")
    api("androidx.appcompat:appcompat:1.6.1")
    api("com.google.android.material:material:1.12.0")
    api("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Material Design
    api("com.google.android.material:material:1.12.0")




    api("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Fragment KTX
    api("androidx.fragment:fragment-ktx:1.7.0")


    // Lifecycle Components
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    api("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.7.0")
    api ("androidx.lifecycle:lifecycle-common-java8:2.7.0")

    // Activity KTX for viewModels()
    api("androidx.activity:activity-ktx:1.9.0")

    // Navigation Components
    api("androidx.navigation:navigation-fragment-ktx:2.7.7")
    api("androidx.navigation:navigation-ui-ktx:2.7.7")


    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    //Dagger - Hilt
    api ("com.google.dagger:hilt-android:2.50")
    kapt ("com.google.dagger:hilt-android-compiler:2.50")
    kapt ("androidx.hilt:hilt-compiler:1.2.0")


    // Retrofit
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.okhttp3:logging-interceptor:4.9.3")



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




    //pickers
    api("io.github.ParkSangGwon:tedimagepicker:1.4.2")
    api("com.github.Shouheng88:compressor:1.6.0")
    api("commons-io:commons-io:2.13.0")



    /**Design*/
    // Glide
    api("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.15.1")

    // SDP
    api("com.intuit.sdp:sdp-android:1.1.1")
    api("com.intuit.ssp:ssp-android:1.1.1")

    // Shimmer
    api("com.facebook.shimmer:shimmer:0.5.0")

    // CircleImageView
    api("de.hdodenhof:circleimageview:3.1.0")
    // Chip Navigation Bar
    api("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
    // ReadMoreTextView
    api("com.borjabravo:readmoretextview:2.1.0")



    // ExoPlayer
    api("com.google.android.exoplayer:exoplayer:2.19.1")
    api("com.google.android.exoplayer:exoplayer-core:2.19.1")
    api("com.google.android.exoplayer:exoplayer-ui:2.19.1")


}