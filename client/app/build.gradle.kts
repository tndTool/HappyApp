plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.happyapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.happyapp"
        minSdk = 29
        targetSdk = 33
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
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")

    implementation("com.github.GrenderG:Toasty:1.5.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.airbnb.android:lottie:5.2.0")
    implementation("androidx.preference:preference:1.2.0")
    implementation("com.google.android.gms:play-services-location:17.0.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}