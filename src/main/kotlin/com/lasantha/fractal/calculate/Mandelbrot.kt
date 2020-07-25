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

    private fun createSamples(range: MatrixRange<Double>): Array<Pair<Double, Double>> {
        val xPixW = (range.x2 - range.x1) / samplesSqrt
        val yPixH = (range.y1 - range.y2) / samplesSqrt

        val xMarkerMidPoints = DoubleArray(samplesSqrt)
        val yMarkerMidPoints = DoubleArray(samplesSqrt)
        xMarkerMidPoints[0] = range.x1 + xPixW / 2
        yMarkerMidPoints[0] = range.y1 - yPixH / 2
        for (a in 1 until samplesSqrt) {
            xMarkerMidPoints[a] = xMarkerMidPoints[a - 1] + xPixW
            yMarkerMidPoints[a] = yMarkerMidPoints[a - 1] - yPixH
        }

        return Array(samplesSqrt * samplesSqrt) {
            val i = it / samplesSqrt
            val j = it % samplesSqrt
            Pair(xMarkerMidPoints[i], yMarkerMidPoints[j])
        }
    }

    /**
     * Complex number Zc is in the Mandelbrot set if this is bounded for very large n, starting with Z0.
     * Zn+1 = Zn ^ 2 + Zc
     * See https://en.wikipedia.org/wiki/Mandelbrot_set
     */
    private fun calculateDeep(range: MatrixRange<Double>,
                              cPoint: Pair<Double, Double>?,
                              handler: (result: Result) -> Unit) {
        val samples = createSamples(range)
        val values = if (cPoint == null) {
            // Mandelbrot set, starting at Z instead of (0,0) for optimization
            Array(samples.size) { calculatePoint(samples[it], samples[it]) }
        } else {
            // Julia set for Zc
            Array(samples.size) { calculatePoint(samples[it], cPoint) }
        }

        // Get the median value
        values.sortBy { it.n }
        handler.invoke(values[values.size / 2])
    }

    private fun calculatePoint(startingPoint: Pair<Double, Double>, cPoint: Pair<Double, Double>): Result {
        val (xc, yc) = cPoint
        var (x, y) = startingPoint

        var n = 1
        var rxr = x * x + y * y
        var dx = 1.0
        var dy = 0.0
        while (n < maxN && rxr < escapeRSquare) {
            n++

            val newDx = 2 * (dx * x - dy * y) + 1
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
