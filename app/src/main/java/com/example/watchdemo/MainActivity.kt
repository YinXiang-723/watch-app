package com.example.watchdemo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText

/**
 * 第二阶段：Kotlin + Compose for Wear OS。
 *
 * 目的：验证「Compose 版 UI 在这台国行小米手表5（Android 14 / 无 GMS）上能编译、能装、能跑」。
 * 注意：androidx.wear.compose 只是 UI 库，不依赖 GMS / Wear OS 系统服务；
 * 磁贴（Tiles）、复杂功能（Complications）、WFF 表盘仍不可用。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Wear Material（M2）的 Scaffold：content 是无参 @Composable，不像 M3 那样给 PaddingValues
                Scaffold(timeText = { TimeText() }) {
                    var count by remember { mutableIntStateOf(0) }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Hello Watch 5",
                            style = MaterialTheme.typography.title2,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Compose for Wear OS 1.4.1",
                            style = MaterialTheme.typography.caption1,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${Build.MANUFACTURER} ${Build.MODEL}",
                            style = MaterialTheme.typography.caption2,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "API ${Build.VERSION.SDK_INT} / Android ${Build.VERSION.RELEASE}",
                            style = MaterialTheme.typography.caption2,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "点击 $count 次",
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { count++ }) {
                            Text("点我")
                        }
                    }
                }
            }
        }
    }
}
