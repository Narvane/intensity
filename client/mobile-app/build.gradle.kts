import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation(project(":intensity-contracts"))
                implementation(project(":mobile-shared"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation("androidx.activity:activity-compose:1.10.1")
                implementation("io.ktor:ktor-client-okhttp:3.1.3")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:3.1.3")
            }
        }
    }
}

android {
    namespace = "com.intensity.mobile.app2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.intensity.mobile.app2"
        resValue("string", "app_name", "Intensity2")
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            // API na sua rede local (mesmo IP no emulador com -redir ou aparelho fAsico na LAN).
            buildConfigField(
                "String",
                "INTENSITY_API_BASE_URL",
                "\"http://192.168.0.210:8080/intensity2/api/v1\""
            )
        }
        create("prod") {
            dimension = "environment"
            buildConfigField(
                "String",
                "INTENSITY_API_BASE_URL",
                "\"https://narvane.com.br/intensity2/api/v1\""
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
