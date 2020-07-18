@file:Suppress("unused")

package com.lasantha.fractal.matrix

interface Matrix<out T : Number> {
    val pixelWidth: Int // Integer width of matrix, > 0
    val pixelHeight: Int // Integer height of matrix, > 0
    val rangeX1: T // X of top left point in the range
    val rangeY1: T // Y of top left point in the range
    val rangePixelSize: T // Single pixel size represented in the range

    /**
     * xPixel: 0 to pixelWidth - 1
     * yPixel: 0 to pixelHeight - 1
     */
    fun get(xPixel: Int, yPixel: Int): Int

    /**
     * xPixel: 0 to pixelWidth - 1
     * yPixel: 0 to pixelHeight - 1
     * value: any Int value stored in that location
     */
    fun set(xPixel: Int, yPixel: Int, value: Int)

    /**
     * xPixel: 0 to pixelWidth - 1
     * yPixel: 0 to pixelHeight - 1
     * value: any Int value stored in that location
     */
    fun foreach(f: (value: Int, xPixel: Int, yPixel: Int) -> Unit) {
        for (y in 0 until pixelHeight) {
            for (x in 0 until pixelWidth) {
                f(get(x, y), x, y)
            }
        }
    }

    fun pixelToRange(xPixel: Int, yPixel: Int): MatrixRange<T>
}

/**
 * x1: smallest possible x value for the range
 * x2: largest possible x value for the range
 * y1: largest possible y value for the range
 * y2: smallest possible y value for the range
 */
data class MatrixRange<out T : Number>(val x1: T, val x2: T, val y1: T, val y2: T)

