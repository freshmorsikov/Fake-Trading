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

val Comment: ImageVector by lazy {
    Builder(
        name = "Comment",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f,
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
                moveTo(12.75f, 0.023f)
                arcToRelative(12.001f, 12.001f, 0.0f, false, true, 1.487f, 23.767f)
                curveToRelative(-2.74f, 0.52f, -5.568f, 0.067f, -8.005f, -1.27f)
                lineToRelative(-3.455f, 0.989f)
                arcToRelative(1.849f, 1.849f, 0.0f, false, true, -2.285f, -2.283f)
                lineToRelative(0.987f, -3.457f)
                arcTo(11.999f, 11.999f, 0.0f, false, true, 12.749f, 0.023f)
                moveToRelative(-0.116f, 1.844f)
                arcToRelative(10.15f, 10.15f, 0.0f, false, false, -10.607f, 8.24f)
                curveToRelative(-0.46f, 2.43f, -0.02f, 4.945f, 1.24f, 7.072f)
                arcToRelative(0.92f, 0.92f, 0.0f, false, true, 0.097f, 0.726f)
                lineTo(2.27f, 21.732f)
                lineToRelative(3.825f, -1.093f)
                arcToRelative(0.93f, 0.93f, 0.0f, false, true, 0.726f, 0.095f)
                arcToRelative(10.15f, 10.15f, 0.0f, false, false, 13.195f, -2.505f)
                arcToRelative(10.156f, 10.156f, 0.0f, false, false, -7.382f, -16.362f)
            }
        }
        .build()
}
