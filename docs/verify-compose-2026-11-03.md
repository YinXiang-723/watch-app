# Compose 版运行验证：2026-11-03（v0.2.3）

## 结论

**Kotlin + Compose for Wear OS 这条路线在这台国行小米手表5 上可用**：能编译、能安装、能运行，圆屏布局与 Material 组件渲染正常，中文显示正常。

## 技术栈（实测可用）

| 组件 | 版本 |
| --- | --- |
| JDK | Temurin 17 |
| Gradle | 8.9 |
| AGP | 8.7.3 |
| Kotlin | 2.1.0（`org.jetbrains.kotlin.android` + `org.jetbrains.kotlin.plugin.compose`） |
| Compose for Wear OS | `androidx.wear.compose:compose-material:1.4.1`（传递依赖 Compose 1.7.0） |
| compileSdk / targetSdk / minSdk | 34 / 34 / 30 |

> 版本选择依据：`compose-material:1.4.1` 的 AAR 元数据是 `minCompileSdk=34`；
> 而 1.7.1 要求 `minCompileSdk=37` + AGP 9.1，当前工具链不用考虑。

## 运行数据

| 指标 | Java/View 版（v0.1.0） | Kotlin/Compose 版（v0.2.3） |
| --- | --- | --- |
| APK 大小 | 9,657 B | 20,164,420 B（debug，未裁剪 ABI） |
| 启动耗时（Displayed） | +1s169ms | +3s143ms |
| 界面 | 原生 `LinearLayout` | Wear Material `Scaffold` + `TimeText` + 圆形 `Button` |

截图：[`screen-0.2.3.png`](screen-0.2.3.png)

## 编译期踩的两个坑（已修）

1. **`kotlinOptions { jvmTarget = "17" }` 在 Kotlin 2.x 下不生效**，导致 Java 17 与 Kotlin 1.8 目标不一致。
   改用 `kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }`。
2. **Wear Material（M2）的 `Scaffold` 与 M3 签名不同**：M2 的 `content` 是 `@Composable () -> Unit`，**没有 `PaddingValues` 参数**。
   误按 M3 写法会报 `@Composable invocations can only happen from the context of a @Composable function`。

## 调试通道：失败日志怎么拿

GitHub Actions 的 step 日志匿名不可读（403）。本仓库的做法：

- 构建步骤用 `gradle ... 2>&1 | tee build.log`；
- 失败时把日志写到 `docs/ci-build.txt` 并强制推到 `ci-logs` 分支（`*.log` 被 `.gitignore` 忽略，所以必须用 `.txt` + `git add -f`）；
- 本地 `git fetch origin ci-logs && git show origin/ci-logs:docs/ci-build.txt` 即可查看，无需登录。

## 后续可优化

- **APK 瘦身**：加 `ndk { abiFilters += "armeabi-v7a" }`（手表只有 32 位 ABI），debug 包可从 20 MB 降到个位数 MB。
- 冷启动 3.1s 偏慢，可在 release 构建开 R8 后再测。
