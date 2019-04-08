package com.lasantha.fractal

class IntMatrix(
    override val pixelWidth: Int,
    override val pixelHeight: Int
) : Matrix<Int> {

    private val values = Array(pixelWidth) { IntArray(pixelHeight) }

    override fun get(xPixel: Int, yPixel: Int): Int {
        return values[xPixel][yPixel]
    }

    override fun set(xPixel: Int, yPixel: Int, value: Int) {
        values[xPixel][yPixel] = value
    }

    override fun toRange(xPixel: Int, yPixel: Int): MatrixRange<Int> {
        return MatrixRange(xPixel, xPixel, yPixel, yPixel)
    }
}
