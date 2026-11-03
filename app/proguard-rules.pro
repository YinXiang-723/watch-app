# R8 / ProGuard 规则
#
# Compose、AndroidX、Kotlin 的 AAR 里都自带 consumer rules，通常不需要手写。
# 这里先留空；如果后续用反射（Gson/Moshi/序列化）或 JNI，再按需添加。

# 崩溃日志里保留可读的类名/行号（会略增体积，可自行删掉）
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
