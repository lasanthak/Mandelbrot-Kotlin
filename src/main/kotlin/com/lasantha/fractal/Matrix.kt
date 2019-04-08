@file:Suppress("unused")

package kotlin.com.lasantha.fractal


interface Matrix<T> {
    val pixelWidth: Int // width > 0
    val pixelHeight: Int // height > 0

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
     * f: function to generate the Int value stored in that location
     * xPixel: 0 to pixelWidth - 1
     * yPixel: 0 to pixelHeight - 1
     */
    fun transform(f: (xPixel: Int, yPixel: Int) -> Int) {
        for (y in 0 until pixelHeight) {
            for (x in 0 until pixelWidth) {
                set(x, y, f(x, y))
            }
        }
    }

    /**
     * f: function to generate the Int value stored in that location
     * range: The range covered by xPixel and yPixel
     */
    fun transform(f: (range: MatrixRange<T>) -> Int) {
        transform(0, pixelWidth - 1, 0, pixelHeight - 1, f)
    }

    /**
     * xFrom: starting X pixel
     * xTo: ending X pixel (including)
     * yFrom: starting Y pixel
     * yTo: ending Y pixel (including)
     * f: function to generate the Int value stored in that location
     * range: The range covered by xPixel and yPixel
     */
    fun transform(xFrom: Int, xTo: Int, yFrom: Int, yTo: Int, f: (range: MatrixRange<T>) -> Int) {
        for (y in yFrom..yTo) {
            for (x in xFrom..xTo) {
                set(x, y, f(toRange(x, y)))
            }
        }
    }

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

    /**
     * Applies the given range. This will affect the result of toRange function
     */
    fun applyRange(r: MatrixRange<T>): Matrix<T> {
        return this
    }

    fun toRange(xPixel: Int, yPixel: Int): MatrixRange<T>
}

/**
 * x1: smallest possible x value for the range
 * x2: largest possible x value for the range
 * y1: smallest possible y value for the range
 * y2: largest possible y value for the range
 */
data class MatrixRange<T>(val x1: T, val x2: T, val y1: T, val y2: T)

