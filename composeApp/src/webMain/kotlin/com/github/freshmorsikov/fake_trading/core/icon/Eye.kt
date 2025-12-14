package com.github.freshmorsikov.fake_trading.core.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Eye: ImageVector by lazy {
    Builder(
        name = "Eye",
        defaultWidth = 24.0.dp,
        defaultHeight = 16.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 16.0f,
    )
        .apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(12.002f, 0.0f)
                curveToRelative(4.265f, 0.0f, 7.257f, 1.927f, 9.164f, 3.834f)
                arcToRelative(15.0f, 15.0f, 0.0f, false, true, 2.086f, 2.615f)
                curveToRelative(0.225f, 0.358f, 0.392f, 0.658f, 0.503f, 0.871f)
                curveToRelative(0.104f, 0.2f, 0.162f, 0.325f, 0.174f, 0.352f)
                lineToRelative(0.001f, 0.002f)
                arcToRelative(0.84f, 0.84f, 0.0f, false, true, 0.0f, 0.653f)
                lineToRelative(-0.001f, 0.003f)
                curveToRelative(-0.012f, 0.027f, -0.07f, 0.152f, -0.174f, 0.351f)
                curveToRelative(-0.111f, 0.214f, -0.278f, 0.513f, -0.503f, 0.87f)
                arcToRelative(15.0f, 15.0f, 0.0f, false, true, -2.086f, 2.615f)
                curveTo(19.259f, 14.073f, 16.266f, 16.0f, 12.002f, 16.0f)
                curveToRelative(-4.266f, 0.0f, -7.258f, -1.927f, -9.165f, -3.834f)
                arcTo(15.0f, 15.0f, 0.0f, false, true, 0.751f, 9.552f)
                arcToRelative(11.0f, 11.0f, 0.0f, false, true, -0.674f, -1.216f)
                lineTo(0.074f, 8.33f)
                lineToRelative(-0.001f, -0.003f)
                curveToRelative(-0.072f, -0.205f, -0.12f, -0.47f, 0.0f, -0.652f)
                lineToRelative(0.001f, -0.003f)
                lineToRelative(0.003f, -0.006f)
                curveToRelative(0.036f, -0.08f, 0.252f, -0.547f, 0.674f, -1.217f)
                arcToRelative(15.0f, 15.0f, 0.0f, false, true, 2.086f, -2.615f)
                curveTo(4.744f, 1.927f, 7.737f, 0.0f, 12.002f, 0.0f)
                moveToRelative(0.0f, 1.6f)
                curveToRelative(-3.734f, 0.0f, -6.34f, 1.673f, -8.034f, 3.366f)
                arcToRelative(13.5f, 13.5f, 0.0f, false, false, -1.863f, 2.336f)
                curveToRelative(-0.178f, 0.283f, -0.312f, 0.522f, -0.407f, 0.699f)
                curveToRelative(0.095f, 0.176f, 0.23f, 0.415f, 0.407f, 0.698f)
                curveToRelative(0.401f, 0.637f, 1.015f, 1.487f, 1.863f, 2.335f)
                curveToRelative(1.693f, 1.693f, 4.3f, 3.366f, 8.034f, 3.366f)
                curveToRelative(3.733f, 0.0f, 6.34f, -1.673f, 8.033f, -3.366f)
                arcTo(13.5f, 13.5f, 0.0f, false, false, 21.898f, 8.7f)
                curveToRelative(0.178f, -0.282f, 0.312f, -0.522f, 0.406f, -0.698f)
                arcToRelative(11.0f, 11.0f, 0.0f, false, false, -0.406f, -0.7f)
                arcToRelative(13.5f, 13.5f, 0.0f, false, false, -1.863f, -2.335f)
                curveToRelative(-1.693f, -1.693f, -4.3f, -3.366f, -8.033f, -3.366f)
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(15.2f, 8.0f)
                arcToRelative(3.2f, 3.2f, 0.0f, true, false, -6.399f, 0.0f)
                arcToRelative(3.2f, 3.2f, 0.0f, false, false, 6.4f, 0.0f)
                moveToRelative(1.6f, 0.0f)
                arcToRelative(4.8f, 4.8f, 0.0f, true, true, -9.599f, 0.001f)
                arcToRelative(4.8f, 4.8f, 0.0f, false, true, 9.6f, 0.0f)
            }
        }
        .build()
}