import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension

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
            // Stable folder for CI/GitHub Pages (default varies by KGP version).
            distribution {
                outputDirectory.set(layout.buildDirectory.dir("dist/pages"))
            }
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

rootProject.extensions.configure<NodeJsRootExtension>(NodeJsRootExtension.EXTENSION_NAME) {
    download = false
}
