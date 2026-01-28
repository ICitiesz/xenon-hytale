import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

val hytaleServerJarPath = (findProperty("hytale_server_jar_path") ?: "server/HytaleServer.jar") as String
val pluginShadedJarName = "xenon-shaded.jar"

group = "com.islandstudio"
version = "1.0-dev"

plugins {
    alias(libs.plugins.kotlin.jvm) apply true
    alias(libs.plugins.kotlin.serialization) apply true
    alias(libs.plugins.gradle.shadow) apply true
}

repositories {
    mavenCentral()
}

dependencies {
    /* Core Language Library */
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    /* Hytale Server */
    compileOnly(files(hytaleServerJarPath))

    /* Function Library */
    implementation(libs.koin.core.jvm)
    implementation(libs.koin.annotations.jvm)
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

/* Tasks */
tasks.named<ProcessResources>("processResources") {
    var replaceProperties = mapOf(
        "plugin_group" to findProperty("plugin_group"),
        "plugin_maven_group" to project.group,
        "plugin_name" to project.name.uppercaseFirstChar(),
        "plugin_version" to project.version,
        "server_version" to findProperty("server_version"),

        "plugin_description" to findProperty("plugin_description"),
        "plugin_website" to findProperty("plugin_website"),

        "plugin_main_entrypoint" to findProperty("plugin_main_entrypoint"),
        "plugin_author" to findProperty("plugin_author")
    )

    filesMatching("manifest.json") {
        expand(replaceProperties)
    }

    inputs.properties(replaceProperties)

    from("src/main/resources") {
        exclude("manifest.json")
    }

    from(file("src/main/resources/manifest.json"))
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set(pluginShadedJarName)

    minimize {
        exclude(hytaleServerJarPath)
    }
}