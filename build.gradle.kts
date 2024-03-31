// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{
    repositories{
        google()
        mavenCentral()
    }
    dependencies{
        //classpath("com.google.dagger:hilt-android:2.51")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.23")
    }
}
plugins {
    id("com.google.gms.google-services") version "4.4.1" apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false

}