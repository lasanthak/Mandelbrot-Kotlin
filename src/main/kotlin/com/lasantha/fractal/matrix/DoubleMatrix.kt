package com.lasantha.fractal.matrix

class DoubleMatrix(
    override val widthInPixels: Int,
    override val heightInPixels: Int,
    override val rangeX1: Double,
    override val rangeY1: Double,
    override val rangePixelSize: Double
) : Matrix<Double, Int> {

    private val data = Array(heightInPixels) { IntArray(widthInPixels) } // data[y][x]

    override fun get(xPixel: Int, yPixel: Int): Int {
        return data[yPixel][xPixel]
    }

    override fun set(xPixel: Int, yPixel: Int, value: Int) {
        data[yPixel][xPixel] = value
    }

    override fun pixelToRange(xPixel: Int, yPixel: Int): MatrixRange<Double> {
        val x1 = rangeX1 + (rangePixelSize * xPixel)
        val x2 = x1 + rangePixelSize
        val y1 = rangeY1 - (rangePixelSize * yPixel)
        val y2 = y1 - rangePixelSize
        return MatrixRange(x1, x2, y1, y2)
    }

}
