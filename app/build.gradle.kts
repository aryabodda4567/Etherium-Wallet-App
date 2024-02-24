plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.android.cryptpay"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.cryptpay"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    //noinspection GradleCompatible,GradleCompatible
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //google auth
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    //database
    implementation("com.google.firebase:firebase-database:20.3.0")

    //image rendering ibrary
    implementation ("com.squareup.picasso:picasso:2.71828")

    //WEB3J
    implementation ("org.web3j:core:5.0.0")
    implementation ("org.bouncycastle:bcprov-jdk15on:1.68") // Or the latest version

    //Qr generator
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")





}