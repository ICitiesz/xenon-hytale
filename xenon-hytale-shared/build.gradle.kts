import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.jvm) apply true
    alias(libs.plugins.kotlin.serialization) apply true
    alias(libs.plugins.google.devtool.ksp) apply true
}

group = "com.islandstudio"
version = "1.0-dev"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    /* Core Language Library */
    implementation(libs.kotlin.reflect)

    /* Hytale Server */
    compileOnly("com.hypixel:hytale-server:1.0.0")

    /* Function Library */
    compileOnly(libs.koin.core.jvm)
    compileOnly(libs.koin.annotations.jvm)
    ksp(libs.koin.ksp.compiler)
    implementation(libs.ktoml.core.jvm)
    implementation(libs.ktoml.file.jvm)
}

kotlin {
    jvmToolchain(25)

    sourceSets {
        main {
            kotlin.srcDirs("src/main/kotlin")

            resources.srcDirs("src/main/resources")
            resources.exclude("**")
        }
    }

    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_3)
        languageVersion.set(KotlinVersion.KOTLIN_2_3)
        jvmTarget.set(JvmTarget.JVM_25)
    }
}