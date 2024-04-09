plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
    id ("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.turtle"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.turtle"
        minSdk = 29
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
        apiVersion="1.9"
        languageVersion="1.9"

    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {

    implementation ("com.google.android.gms:play-services-auth:21.0.0")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-common-ktx:20.4.3")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material:material:1.6.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    implementation ("androidx.compose.ui:ui-tooling:1.1.0-alpha05")

    implementation("com.google.dagger:hilt-android:2.51")
    //implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("com.google.dagger:hilt-android-compiler:2.51")
    //kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.7.0")
    implementation ("androidx.savedstate:savedstate:1.2.1")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    val ktorVersion = "2.3.9"
    implementation ("io.ktor:ktor-client-core:$ktorVersion")
    implementation ("io.ktor:ktor-client-cio:$ktorVersion")
    implementation ("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation ("io.ktor:ktor-client-logging:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")



}
kapt {
    correctErrorTypes = true
}