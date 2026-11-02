plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.watchdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.watchdemo"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        // 第二阶段加 Compose/AndroidX 后，如需 native 库再启用：
        // ndk { abiFilters += "armeabi-v7a" }   // 实测手表只有 32 位 ABI
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        // 第一版不因 lint 失败而中断 CI
        abortOnError = false
    }
}

// 第一阶段刻意不引任何依赖：零 AndroidX、零 Kotlin、零 Compose，
// 目的是先让「云端编译 → 装表 → 启动」这条链路稳定跑通。
dependencies {
}

// 源码里有中文（界面文案），显式指定 UTF-8，避免 Windows 本地构建时按 GBK 解码报错。
tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
