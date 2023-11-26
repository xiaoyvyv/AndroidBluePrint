plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
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
    }

    // 限定资源前缀
    resourcePrefix = "ui_"
    namespace = "com.xiaoyv.widget"
}

configurations.configureEach {
    // 移除 support
    exclude(group = "com.android.support")
}

dependencies {
    // api(project(":leveldb"))

    api("androidx.core:core-ktx:1.12.0")
    api("androidx.appcompat:appcompat:1.6.1")
    api("androidx.annotation:annotation:1.7.0")
    api("com.google.android.material:material:1.10.0")
    api("androidx.recyclerview:recyclerview:1.3.2")
    api("androidx.constraintlayout:constraintlayout:2.1.4")
    api("androidx.multidex:multidex:2.0.1")
    api("com.google.android.flexbox:flexbox:3.0.0")

    // Lifecycle
    api("androidx.lifecycle:lifecycle-process:2.6.2")
    api("androidx.lifecycle:lifecycle-common-java8:2.6.2")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Utils
    api("com.blankj:utilcodex:1.31.1")
    // AdapterHelper
    api("io.github.cymchad:BaseRecyclerViewAdapterHelper:4.0.1")
    // StateView
    api("com.github.nukc:StateView:v3.0.0")
    // Bar
    api("com.geyifeng.immersionbar:immersionbar:3.2.2")
    api("com.geyifeng.immersionbar:immersionbar-ktx:3.2.2")
    // 各种选项卡
    api("com.github.hackware1993:MagicIndicator:1.7.0")
    // 表情面板闪屏解决方案
    api("com.github.DSAppTeam:PanelSwitchHelper:1.5.6")
    // 越界回弹
    api("io.github.everythingme:overscroll-decor-android:1.1.1")
    // 圆形图片
    api("de.hdodenhof:circleimageview:3.1.0")
    // BgaBanner
    api("com.github.bingoogolapple:BGABanner-Android:3.0.1")

    // Refresh
    api("io.github.scwang90:refresh-layout-kernel:2.0.5")
    api("io.github.scwang90:refresh-header-classics:2.0.5")
    api("io.github.scwang90:refresh-footer-classics:2.0.5")

    // Glide 图片加载框架
    api("com.github.bumptech.glide:glide:4.15.1")
    api("com.github.bumptech.glide:okhttp3-integration:4.15.1") {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }

    // OkHttp
    api("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    ksp("com.github.bumptech.glide:ksp:4.15.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
