plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.watchdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.watchdemo"
        minSdk = 30
        targetSdk = 34
        versionCode = 2
        versionName = "1.1-compose"
    }

    signingConfigs {
        // 固定 debug 签名：CI 与本地共用同一把 key。
        // 否则每次云端构建都会生成新的 debug.keystore，覆盖安装会报 INSTALL_FAILED_UPDATE_INCOMPATIBLE。
        // 注意：这是公开的调试证书（密码 android），只能用于开发，不能用于正式发布。
        getByName("debug") {
            storeFile = file("keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = false
            // 需要正式发布时再加 release signingConfig（用 secrets 注入，不要提交 keystore）
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    // Compose for Wear OS（Material 2 版组件，1.4.1 只要求 compileSdk 34）
    implementation("androidx.wear.compose:compose-material:1.4.1")
    implementation("androidx.wear.compose:compose-foundation:1.4.1")

    // setContent / 生命周期
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.13.1")
}

// 源码里有中文（界面文案），显式指定 UTF-8，避免 Windows 本地构建时按 GBK 解码报错。
tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
