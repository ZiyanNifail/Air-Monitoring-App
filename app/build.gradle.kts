plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.testingapp1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.testingapp1"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Google Maps
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    // AndroidX
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation(libs.play.services.location)
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.play.services.location.v2101)
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    dependencies {
        // Location
        implementation ("com.google.android.gms:play-services-location:21.0.1")

        // Retrofit
        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

        // GSON
        implementation ("com.google.code.gson:gson:2.10.1")
        implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    }
}