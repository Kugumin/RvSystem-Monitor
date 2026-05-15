package com.rve.systemmonitor.ui.components.shape

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

class LShape(val cornerRadius: Dp, val cutXPercent: Float = 0.78f, val cutYPercent: Float = 0.35f) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = with(density) { cornerRadius.toPx() }
        val cutX = size.width * cutXPercent
        val cutY = size.height * cutYPercent

        val path = Path().apply {
            moveTo(r, 0f)

            lineTo(cutX - r, 0f)
            quadraticTo(cutX, 0f, cutX, r)

            lineTo(cutX, cutY - r)
            quadraticTo(cutX, cutY, cutX + r, cutY)

            lineTo(size.width - r, cutY)
            quadraticTo(size.width, cutY, size.width, cutY + r)

            lineTo(size.width, size.height - r)
            quadraticTo(size.width, size.height, size.width - r, size.height)

            lineTo(r, size.height)
            quadraticTo(0f, size.height, 0f, size.height - r)

            lineTo(0f, r)
            quadraticTo(0f, 0f, r, 0f)

            close()
        }
        return Outline.Generic(path)
    }
}
