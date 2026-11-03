# WatchDemo — 小米手表5（M2501W1）云端编译最小工程

第一阶段目标只有一个：**用 GitHub Actions 编译，本机用 adb 装上手表并跑起来**。

> ✅ **2026-11-03 已在真机跑通三次**
> - `v0.1.0`：**Java + 原生 View**，APK 9,657 B，`Displayed +1s169ms` → [记录](docs/verify-2026-11-03.md) / [截图](docs/screen-0.1.0.png)
> - `v0.2.3`：**Kotlin 2.1.0 + Compose for Wear OS 1.4.1**（debug，未裁剪），APK 20.1 MB，`Displayed +3s143ms` → [记录](docs/verify-compose-2026-11-03.md) / [截图](docs/screen-0.2.3.png)
> - `v0.3.0`：同一份 Compose 代码的 **R8 release 包**，APK **1.28 MB（-93.3%）**，`Displayed +463ms` → [记录](docs/verify-release-2026-11-03.md) / [截图](docs/screen-0.3.0-release.png)
>
> 结论：Compose 的"体积/启动"开销主要来自 debug 不裁剪；开启 R8 后与手写 View 的差距大幅缩小。

## 目录

```
watch-app/
├─ .github/workflows/android.yml   # 云端编译流水线
├─ app/
│  ├─ build.gradle.kts             # compileSdk 34 / minSdk 30 / targetSdk 34
│  ├─ keystore/debug.keystore      # 固定调试签名（CI 与本地同一把 key）
│  └─ src/main/
│     ├─ AndroidManifest.xml
│     └─ java/com/example/watchdemo/MainActivity.java
├─ build.gradle.kts
├─ settings.gradle.kts
├─ gradle.properties
└─ scripts/deploy.ps1              # 下载好的 APK → 装表 → 启动
```

## 为什么不用本地 SDK

本机只装了 `platform-tools`（`..\tools\platform-tools\adb.exe`），没有 Android SDK。
编译交给 GitHub Actions 的 Ubuntu runner（预装 JDK/SDK），产物作为 artifact 下载回来。

## 版本矩阵（刻意保守，先保证绿灯）

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| JDK | 17（Temurin） | CI 里 setup-java 安装 |
| Gradle | 8.9 | CI 里 setup-gradle 安装，仓库不提交 wrapper jar |
| AGP | 8.7.3 | 支持 compileSdk 34/35 |
| compileSdk / targetSdk | 34 / 34 | 与手表 Android 14 对齐 |
| minSdk | 30 | 覆盖面足够，手表不受影响 |
| ABI | 无 native 库 | 手表只有 armeabi-v7a，将来加 .so 时必须给 v7a |

> 为什么用 `gradle` 而不是 `./gradlew`：仓库里没有 Gradle wrapper 的 jar（二进制、本机无 Gradle 无法生成）。
> 等第一次绿灯后，可以在本地 `gradle wrapper --gradle-version 8.9` 生成并提交，之后 CI 改用 `./gradlew` 即可。

## 流水线

`push` 到 `main` 或手动 `workflow_dispatch` 触发：

1. checkout
2. JDK 17 + Android SDK（platform 34、build-tools 34.0.0）
3. Gradle 8.9 构建 `:app:assembleDebug`
4. 上传 artifact `watch-debug-apk`

## 本机装表（编译完之后）

1. 在 GitHub 的 Action 运行页下载 artifact（或在装了 `gh` 的机器上 `gh run download -n watch-debug-apk`）。
2. 解压得到 `app-debug.apk`。
3. 手表连上 adb（推荐固定端口）：

   ```powershell
   ..\tools\platform-tools\adb.exe connect 192.168.0.116:5555
   ```

4. 安装并启动：

   ```powershell
   .\scripts\deploy.ps1 -Apk .\app-debug.apk -Serial 192.168.0.116:5555
   ```

## 已知坑

- **签名**：CI 每次都是新环境，默认 debug keystore 每次都会变，覆盖安装会 `INSTALL_FAILED_UPDATE_INCOMPATIBLE`。
  本工程把 `app/keystore/debug.keystore` 提交进仓库并在 `signingConfigs` 里固定使用，所以云端和本地签名一致。
  这把 key 是公开的调试证书（密码 `android`），**只能用于开发**。
- **ABI**：手表 `ro.product.cpu.abilist = armeabi-v7a,armeabi`，加 native 库时别只打 arm64。
- **圆屏**：480×480 / density 320 → 240dp；内容居中、左右留 12dp。

## 第二阶段（绿灯之后）

- 换 Kotlin + `androidx.wear.compose:compose-material3`（1.7.1）做圆屏 UI
- 引入 `gradle wrapper`，CI 改用 `./gradlew`
- 加 `lint` / 单元测试门禁
- 打 tag 时自动创建 Release 并挂 APK，方便无 `gh` 的机器下载
