package com.lasantha.fractal.matrix

interface Matrix<R : Number, T> {
    val width: Int // Integer width of matrix, > 0
    val height: Int // Integer height of matrix, > 0

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
        for (y in 0 until height) {
            for (x in 0 until width) {
                f(get(x, y), x, y)
            }
        }
    }

    fun pixelMidPoint(xPixel: Int, yPixel: Int, midPointSquare: MapSquare<R>): MapPoint<R>

    fun pixelSubPoints(xPixel: Int, yPixel: Int, midPointSquare: MapSquare<R>): Array<MapPoint<R>>
}

/**
 * @param x - X value of the point
 * @param y - Y value of the point
 */
data class MapPoint<out R : Number>(val x: R, val y: R)

/**
 * @param midPoint - Mid-point of the square
 * @param topLeftPoint - Top-left point of the square
 * @param size - Length of a side
 * @param subPixelCountSqrt - Number of sub-pixel to calculate per each direction to reduce antialiasing.
 * This will result in a point count that equals the square of this number.
 * @param subPixelSize - Length of a sub-pixel distance
 */
data class MapSquare<out R : Number>(
    val midPoint: MapPoint<R>,
    val topLeftPoint: MapPoint<R>,
    val size: R,
    val subPixelCountSqrt: Int,
    val subPixelSize: R
)
