@file:Suppress("unused")

package com.lasantha.fractal.matrix

interface Matrix<out R : Number, T> {
    val widthInPixels: Int // Integer width of matrix, > 0
    val heightInPixels: Int // Integer height of matrix, > 0
    val rangeX1: R // X of top left point in the range
    val rangeY1: R // Y of top left point in the range
    val rangePixelSize: R // Single pixel size represented in the range

    /**
     * xPixel: 0 to pixelWidth - 1
     * yPixel: 0 to pixelHeight - 1
     */
    fun get(xPixel: Int, yPixel: Int): T

    /**
     * xPixel: 0 to pixelWidth - 1
     * yPixel: 0 to pixelHeight - 1
     * value: any Int value stored in that location
     */
    fun set(xPixel: Int, yPixel: Int, value: T)

    /**
     * xPixel: 0 to widthInPixels - 1
     * yPixel: 0 to heightInPixels - 1
     * value: any Int value stored in that location
     */
    fun forEach(f: (value: T, xPixel: Int, yPixel: Int) -> Unit) {
        for (y in 0 until heightInPixels) {
            for (x in 0 until widthInPixels) {
                f(get(x, y), x, y)
            }
        }
    }

    fun pixelToRange(xPixel: Int, yPixel: Int): MatrixRange<R>
}

/**
 * x1: smallest possible x value for the range
 * x2: largest possible x value for the range
 * y1: largest possible y value for the range
 * y2: smallest possible y value for the range
 */
data class MatrixRange<out R : Number>(val x1: R, val x2: R, val y1: R, val y2: R)

