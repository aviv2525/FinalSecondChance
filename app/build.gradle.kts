plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("kotlin-parcelize")
    alias(libs.plugins.androidx.navigation.safe.args)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.secondchance"
    compileSdk = 35

    buildFeatures{
        viewBinding = true
    }
    defaultConfig {
        applicationId = "com.example.secondchance"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

/*
    implementation 'com.github.bumptech.glide:glide:4.16.0';
    kapt 'com.github.bumptech.glide:compiler:4.16.0';
*/
//    implementation(libs.androidx.lifecycle.viewmodel.ktx)
//    implementation(libs.androidx.lifecycle.livedata.ktx)

    //FireBase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.analytics.ktx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.glide)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.databinding.runtime)
    kapt(libs.glide.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(kotlin("reflect"))

}