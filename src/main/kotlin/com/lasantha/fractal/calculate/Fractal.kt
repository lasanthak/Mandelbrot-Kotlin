package com.lasantha.fractal.calculate

import com.lasantha.fractal.matrix.MatrixRange

enum class FractalType {
    MANDELBROT_SET, JULIA_SET
}

interface Fractal {
    val type: FractalType
    val maxIterations: Int
    val escapeRadius: Double
    val subPixelCountSqrt: Int

    fun calculatePoint(range: MatrixRange<Double>, handler: (result: Result) -> Unit)

    fun createSamples(range: MatrixRange<Double>): Array<Pair<Double, Double>> {
        val xPixW = (range.x2 - range.x1) / subPixelCountSqrt
        val yPixH = (range.y1 - range.y2) / subPixelCountSqrt

        val xMarkerMidPoints = DoubleArray(subPixelCountSqrt)
        val yMarkerMidPoints = DoubleArray(subPixelCountSqrt)
        xMarkerMidPoints[0] = range.x1 + xPixW / 2
        yMarkerMidPoints[0] = range.y1 - yPixH / 2
        for (a in 1 until subPixelCountSqrt) {
            xMarkerMidPoints[a] = xMarkerMidPoints[a - 1] + xPixW
            yMarkerMidPoints[a] = yMarkerMidPoints[a - 1] - yPixH
        }

        return Array(subPixelCountSqrt * subPixelCountSqrt) {
            val i = it / subPixelCountSqrt
            val j = it % subPixelCountSqrt
            Pair(xMarkerMidPoints[i], yMarkerMidPoints[j])
        }
    }

    /**
     * Complex number Zc is in the Mandelbrot set if this is bounded for very large n, starting with Z0.
     * Zn+1 = Zn ^ 2 + Zc
     * See https://en.wikipedia.org/wiki/Mandelbrot_set
     */
    fun calculateDeep(
        range: MatrixRange<Double>,
        cPoint: Pair<Double, Double>?,
        handler: (result: Result) -> Unit
    ) {
        val samples = createSamples(range)
        val values = if (cPoint == null) {
            // Mandelbrot set starting at (0,0) and current point is Zc
            Array(samples.size) { calculatePoint(Pair(0.0, 0.0), samples[it]) }
        } else {
            // Julia set for Zc  starting at current point
            Array(samples.size) { calculatePoint(samples[it], cPoint) }
        }

        // Get the median value
        values.sortBy { it.n }
        handler.invoke(values[values.size / 2])
    }

    private fun calculatePoint(start: Pair<Double, Double>, c: Pair<Double, Double>): Result {
        val escapeRR = escapeRadius * escapeRadius
        val (xc, yc) = c
        var (x, y) = start

        var n = 0
        var xx = x * x
        var yy = y * y
        var rr: Double
        var dx = 1.0
        var dy = 0.0
        do {
            val newDx = 2.0 * (dx * x - dy * y) + 1.0
            dy = 2.0 * (dx * y + dy * x)
            dx = newDx

            n++

            y = 2.0 * (x * y) + yc
            x = xx - yy + xc

            xx = x * x
            yy = y * y
            rr = xx + yy
        } while (n < maxIterations && rr < escapeRR)

        return Result(n, rr, Pair(x, y), Pair(dx, dy))
    }
}

class MandelbrotSet(
    override val maxIterations: Int,
    override val escapeRadius: Double,
    override val subPixelCountSqrt: Int
) : Fractal {
    override val type: FractalType = FractalType.MANDELBROT_SET

    override fun calculatePoint(range: MatrixRange<Double>, handler: (result: Result) -> Unit) {
        calculateDeep(range, null, handler)
    }
}

class JuliaSet(
    private val c: Pair<Double, Double>,
    override val maxIterations: Int,
    override val escapeRadius: Double,
    override val subPixelCountSqrt: Int
) : Fractal {
    override val type: FractalType = FractalType.JULIA_SET

    override fun calculatePoint(range: MatrixRange<Double>, handler: (result: Result) -> Unit) {
        calculateDeep(range, c, handler)
    }
}
