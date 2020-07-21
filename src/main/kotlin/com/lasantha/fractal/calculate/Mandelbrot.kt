package com.lasantha.fractal.calculate

import com.lasantha.fractal.matrix.MatrixRange

class Mandelbrot(private val maxN: Int,
                 private val escapeRadius: Double,
                 private val samplesSqrt: Int) {

    private val escapeRSquare = escapeRadius * escapeRadius

    /*
    * Complex number Zc is in the Mandelbrot set if this is bounded for very large n, starting with Z0.
    * Zn+1 = Zn ^ 2 + Zc
    * See https://en.wikipedia.org/wiki/Mandelbrot_set
     */
    fun calculate(range: MatrixRange<Double>, handler: (n: Int, rSquare: Double) -> Unit) {
        val values = Array<Result?>(samplesSqrt * samplesSqrt) { null }
        val xPixW = (range.x2 - range.x1) / samplesSqrt
        val yPixH = (range.y1 - range.y2) / samplesSqrt

        val xMarkers = DoubleArray(samplesSqrt + 1)
        val yMarkers = DoubleArray(samplesSqrt + 1)
        xMarkers[0] = range.x1
        yMarkers[0] = range.y1
        for (i in 1..samplesSqrt) { // nSqrt inclusive
            xMarkers[i] = xMarkers[i - 1] + xPixW
            yMarkers[i] = yMarkers[i - 1] - yPixH
        }

        for (i in 0 until samplesSqrt) {
            for (j in 0 until samplesSqrt) {
                val rangeForPoint = MatrixRange(xMarkers[i], xMarkers[i + 1], yMarkers[j], yMarkers[j + 1])
                values[i * samplesSqrt + j] = calculatePoint(rangeForPoint)
            }
        }

        // Get the median value
        values.sortBy { it!!.n }
        val result = values[values.size / 2]!!

        handler.invoke(result.n, result.rSquare)
    }

    private fun calculatePoint(range: MatrixRange<Double>): Result {
        // Take the mid point as Zc
        val xc = (range.x1 + range.x2) / 2
        val yc = (range.y1 + range.y2) / 2

        var x = 0.0
        var y = 0.0
        var n = 0
        var xx: Double
        var yy: Double
        var rxr: Double
        do {
            n++
            xx = x * x
            yy = y * y
            y = (2 * x * y) + yc
            x = xx - yy + xc
            rxr = xx + yy
        } while (n < maxN && rxr < escapeRSquare)

        return Result(n, rxr)
    }

    private data class Result(val n: Int, val rSquare: Double)

}
