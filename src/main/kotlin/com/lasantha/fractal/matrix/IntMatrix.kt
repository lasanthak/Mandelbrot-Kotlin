package com.lasantha.fractal.matrix

class IntMatrix(
    override val pixelWidth: Int,
    override val pixelHeight: Int,
    override val rangeX1: Int = pixelWidth,
    override val rangeY1: Int = pixelHeight,
    override val rangePixelSize: Int  = 1
) : Matrix<Int> {

    private val data = Array(pixelHeight) { IntArray(pixelWidth) } // data[y][x]

    override fun get(xPixel: Int, yPixel: Int): Int {
        return data[yPixel][xPixel]
    }

    override fun set(xPixel: Int, yPixel: Int, value: Int) {
        data[yPixel][xPixel] = value
    }

    override fun pixelToRange(xPixel: Int, yPixel: Int): MatrixRange<Int> {
        return MatrixRange(xPixel, xPixel, yPixel, yPixel)
    }
}
