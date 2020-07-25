package com.lasantha.fractal.calculate

import com.lasantha.fractal.matrix.MatrixRange

class Mandelbrot(private val maxN: Int,
                 private val escapeRadius: Double,
                 private val samplesSqrt: Int) {

    private val escapeRSquare = escapeRadius * escapeRadius

    fun calculateMandelbrotSet(range: MatrixRange<Double>,
                               handler: (result: Result) -> Unit) {
        calculateDeep(range, null, handler)
    }

    fun calculateJuliaSet(range: MatrixRange<Double>,
                          cPoint: Pair<Double, Double>,
                          handler: (result: Result) -> Unit) {
        calculateDeep(range, cPoint, handler)
    }

    /*
    * Complex number Zc is in the Mandelbrot set if this is bounded for very large n, starting with Z0.
    * Zn+1 = Zn ^ 2 + Zc
    * See https://en.wikipedia.org/wiki/Mandelbrot_set
     */
    private fun calculateDeep(range: MatrixRange<Double>,
                              cPoint: Pair<Double, Double>?,
                              handler: (result: Result) -> Unit) {
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
                val index = i * samplesSqrt + j
                val (x1, x2, y1, y2) = MatrixRange(xMarkers[i], xMarkers[i + 1], yMarkers[j], yMarkers[j + 1])
                val midPoint = Pair((x1 + x2) / 2, (y1 + y2) / 2)
                if (cPoint == null) {
                    // Mandelbrot set, starting at Z instead of (0,0) for optimization
                    values[index] = calculatePoint(midPoint, midPoint)
                } else {
                    // Julia set for Zc
                    values[index] = calculatePoint(midPoint, cPoint)
                }
            }
        }

        // Get the median value
        values.sortBy { it!!.n }
        val result = values[values.size / 2]!!
        handler.invoke(result)
    }

    private fun calculatePoint(startingPoint: Pair<Double, Double>, cPoint: Pair<Double, Double>): Result {
        val xc = cPoint.first
        val yc = cPoint.second

        var x = startingPoint.first
        var y = startingPoint.second
        var n = 1
        var rxr = x * x + y * y
        var dx = 1.0
        var dy = 0.0
        while (n < maxN && rxr < escapeRSquare) {
            n++

            val newDx = 2 * (dx * x - dy * y) + 1.0
            dy = 2 * (dx * y + dy * x)
            dx = newDx

            val xx = x * x
            val yy = y * y
            y = 2 * (x * y) + yc
            x = xx - yy + xc
            rxr = xx + yy
        }

        return Result(n, rxr, Pair(x, y), Pair(dx, dy))
    }
}
