package com.lasantha.fractal

import kotlin.math.max

class DoubleMatrix(
    override val pixelWidth: Int,
    override val pixelHeight: Int
) : Matrix<Double> {

    private val values = Array(pixelWidth) { IntArray(pixelHeight) }

    private var rangeX1 = 0.0
    private var rangeY1 = 0.0
    private var rangePixel = 0.0

    override fun get(xPixel: Int, yPixel: Int): Int {
        return values[xPixel][yPixel]
    }

    override fun set(xPixel: Int, yPixel: Int, value: Int) {
        values[xPixel][yPixel] = value
    }

    override fun toRange(xPixel: Int, yPixel: Int): MatrixRange<Double> {
        val x1 = (rangePixel * xPixel.toDouble()) + rangeX1
        val x2 = x1 + rangePixel
        val y1 = (rangePixel * yPixel.toDouble()) + rangeY1
        val y2 = y1 + rangePixel
        return MatrixRange(x1, x2, y1, y2)
    }

    override fun applyRange(r: MatrixRange<Double>): DoubleMatrix {
        val rangePixelWidth = (r.x2 - r.x1) / pixelWidth.toDouble()
        val rangePixelHeight = (r.y2 - r.y1) / pixelHeight.toDouble()
        rangePixel = max(rangePixelWidth, rangePixelHeight)
        rangeX1 = r.x1
        rangeY1 = r.y1

        return this
    }

}
