import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application").version("8.7.1").apply(false)
    id("com.android.library").version("8.7.1").apply(false)
    id("org.jetbrains.kotlin.android").version("1.9.0").apply(false)
    id("com.google.devtools.ksp").version("1.9.0-1.0.11").apply(false)
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