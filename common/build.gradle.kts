

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id ("kotlin-kapt")
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
        targetCompatibility =  JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()

    }

    buildFeatures {
        viewBinding =  true
        dataBinding =  true
    }
    

    kapt {
        correctErrorTypes =  true
    }



}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.15.1")


    //pickers
    api("io.github.ParkSangGwon:tedimagepicker:1.4.2")
    api("com.github.Shouheng88:compressor:1.6.0")
    api("commons-io:commons-io:2.7")

    // SDP
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation( "com.intuit.ssp:ssp-android:1.1.1")
}