package com.lasantha.fractal.matrix

class DoubleMatrix(
    override val widthInPixels: Int,
    override val heightInPixels: Int,
    override val midX: Double,
    override val midY: Double,
    override val rangePixelSize: Double
) : Matrix<Double, Int> {

    private val topX = midX - rangePixelSize * widthInPixels / 2
    private val topY = midY + rangePixelSize * heightInPixels / 2

    private val data = Array(heightInPixels) { IntArray(widthInPixels) } // data[y][x]

    override fun get(xPixel: Int, yPixel: Int): Int {
        return data[yPixel][xPixel]
    }

    override fun set(xPixel: Int, yPixel: Int, value: Int) {
        data[yPixel][xPixel] = value
    }

    override fun pixelToRange(xPixel: Int, yPixel: Int): MatrixRange<Double> {
        val x1 = topX + (rangePixelSize * xPixel)
        val x2 = x1 + rangePixelSize
        val y1 = topY - (rangePixelSize * yPixel)
        val y2 = y1 - rangePixelSize
        return MatrixRange(x1, x2, y1, y2)
    }
}
