import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

android {
    namespace = "com.yorgo.tetris.shared"
    compileSdk = 35
    defaultConfig {
        minSdk = 29
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "yetris.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
        }
    }
}

// Use Node from PATH (CI: setup-node). Avoids resolving org.nodejs:node from Maven when settings repos are centralized.
rootProject.plugins.withType<NodeJsRootPlugin>().configureEach {
    rootProject.extensions.getByType<NodeJsRootExtension>().download.set(false)
}
