import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application").version("8.3.0-alpha14").apply(false)
    id("com.android.library").version("8.3.0-alpha14").apply(false)
    id("org.jetbrains.kotlin.android").version("1.9.0").apply(false)
}

allprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}