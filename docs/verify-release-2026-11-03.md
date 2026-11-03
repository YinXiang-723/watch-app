# Release 构建验证：2026-11-03（v0.3.0）

## 结论

同一份 Kotlin + Compose 代码，开启 **R8 + 资源裁剪** 后的 release 包：

- 体积 **20.1 MB → 1.28 MB（-93.3%）**
- 冷启动 **3,143 ms → 463 ms（约 6.8 倍）**
- 安装、运行、界面与 debug 版完全一致

## 数据

| 指标 | debug（v0.2.3） | release（v0.3.0） |
| --- | --- | --- |
| APK 大小 | 20,164,420 B | **1,345,354 B** |
| dex 数量/体积 | 4 个，约 19.2 MB | 由 R8 合并裁剪（体积随代码保留量决定） |
| 冷启动 `Displayed` | +3s143ms | **+463ms** |
| 安装结果 | Success | Success |
| 签名 | `app/keystore/debug.keystore` | 同一把（**仅内测**，正式发布需换正式证书） |

截图：[`screen-0.3.0-release.png`](screen-0.3.0-release.png)

## 配置要点

```kotlin
release {
    isMinifyEnabled = true        // R8 代码裁剪/混淆/优化
    isShrinkResources = true      // 移除未引用的资源
    signingConfig = signingConfigs.getByName("debug")  // 内测：同签名可直接覆盖安装
    proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}
```

- Compose / AndroidX / Kotlin 的 AAR 自带 consumer rules，本项目无需手写 keep 规则。
- `proguard-rules.pro` 只保留了 `SourceFile,LineNumberTable`，方便看崩溃堆栈。

## 已知边界

- 用 debug 证书签的 release 包**不能上架**，也不能与正式签名的历史版本互相覆盖升级。
- R8 后类名被混淆，`adb logcat` 里的堆栈需要 `mapping.txt` 才能还原（构建产物：`app/build/outputs/mapping/release/mapping.txt`）。
