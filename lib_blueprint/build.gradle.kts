plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

apply(from = "../maven.gradle")

android {
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
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

    resourcePrefix = "bp_"

    namespace = "com.xiaoyv.blueprint"
}

dependencies {
    api(project(":lib_widget"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.20")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    compileOnly("androidx.navigation:navigation-fragment-ktx:2.7.7")
    compileOnly("androidx.navigation:navigation-ui-ktx:2.7.7")

    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    api("com.kunminx.arch:unpeek-livedata:7.8.0")

    api("io.github.jeremyliao:live-event-bus-x:1.8.0")

    api("com.blankj:utilcodex:1.31.1")
    api("com.google.code.gson:gson:2.10.1")
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")

    api("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}