package com.github.freshmorsikov.fake_trading.core.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Like: ImageVector by lazy {
    Builder(
        name = "Like",
        defaultWidth = 24.0.dp,
        defaultHeight = 21.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 21.0f,
    )
        .apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = EvenOdd,
            ) {
                moveTo(12.844f, 20.672f)
                curveToRelative(-0.118f, 0.074f, -0.238f, 0.15f, -0.363f, 0.204f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, -0.962f, 0.0f)
                lineToRelative(-0.504f, -0.292f)
                arcToRelative(35.674f, 35.674f, 0.0f, false, true, -5.177f, -3.774f)
                curveTo(3.063f, 14.359f, 0.0f, 10.752f, 0.0f, 6.613f)
                lineToRelative(0.007f, -0.287f)
                arcToRelative(6.56f, 6.56f, 0.0f, false, true, 1.522f, -3.912f)
                arcTo(6.74f, 6.74f, 0.0f, false, true, 5.404f, 0.128f)
                arcTo(6.82f, 6.82f, 0.0f, false, true, 9.87f, 0.771f)
                arcTo(6.7f, 6.7f, 0.0f, false, true, 12.0f, 2.523f)
                arcTo(6.7f, 6.7f, 0.0f, false, true, 14.13f, 0.77f)
                arcToRelative(6.82f, 6.82f, 0.0f, false, true, 4.466f, -0.643f)
                arcToRelative(6.74f, 6.74f, 0.0f, false, true, 3.875f, 2.286f)
                arcTo(6.55f, 6.55f, 0.0f, false, true, 24.0f, 6.614f)
                curveToRelative(0.0f, 4.138f, -3.063f, 7.745f, -5.838f, 10.196f)
                arcToRelative(35.7f, 35.7f, 0.0f, false, true, -5.177f, 3.774f)
                close()
                moveTo(18.21f, 2.09f)
                arcToRelative(4.82f, 4.82f, 0.0f, false, false, -3.154f, 0.453f)
                arcToRelative(4.66f, 4.66f, 0.0f, false, false, -2.136f, 2.299f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, -1.62f, 0.322f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, -0.218f, -0.324f)
                arcToRelative(4.66f, 4.66f, 0.0f, false, false, -2.138f, -2.297f)
                arcToRelative(4.82f, 4.82f, 0.0f, false, false, -3.154f, -0.453f)
                arcToRelative(4.74f, 4.74f, 0.0f, false, false, -2.727f, 1.605f)
                arcTo(4.55f, 4.55f, 0.0f, false, false, 2.0f, 6.614f)
                curveToRelative(0.0f, 3.203f, 2.437f, 6.29f, 5.162f, 8.697f)
                arcTo(33.7f, 33.7f, 0.0f, false, false, 12.0f, 18.842f)
                arcToRelative(33.665f, 33.665f, 0.0f, false, false, 4.838f, -3.531f)
                curveTo(19.563f, 12.904f, 22.0f, 9.817f, 22.0f, 6.614f)
                lineToRelative(-0.005f, -0.199f)
                arcToRelative(4.55f, 4.55f, 0.0f, false, false, -1.059f, -2.719f)
                arcToRelative(4.74f, 4.74f, 0.0f, false, false, -2.726f, -1.605f)
            }
        }
        .build()
}