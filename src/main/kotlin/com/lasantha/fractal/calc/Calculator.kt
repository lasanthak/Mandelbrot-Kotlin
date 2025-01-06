package com.lasantha.fractal.calc

import com.lasantha.fractal.matrix.MapPoint


interface PointCalculator<R : Number> {
    val maxIterations: Int
    val escapeRadius: R

    /**
     * Starts at zero (eg: for Mandelbrot set)
     */
    fun calculatePoint(c: MapPoint<R>): PointResult<R>

    /**
     * Starts at given point (eg: for Julia set)
     */
    fun calculatePoint(start: MapPoint<R>, c: MapPoint<R>): PointResult<R>
}

class DoublePointCalculator(
    override val maxIterations: Int,
    override val escapeRadius: Double,
) : PointCalculator<Double> {

    override fun calculatePoint(c: MapPoint<Double>): PointResult<Double> {
        return calculatePoint(MapPoint(0.0, 0.0), c)
    }

    /**
     * Complex number Zc is in the Mandelbrot set if this is bounded for very large n, starting with Z0.
     * Zn+1 = Zn ^ 2 + Zc
     * See https://en.wikipedia.org/wiki/Mandelbrot_set
     */
    override fun calculatePoint(start: MapPoint<Double>, c: MapPoint<Double>): PointResult<Double> {
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

        return PointResult(n, rr, MapPoint(x, y), MapPoint(dx, dy))
    }
}

/**
 * @param n Number of iteration
 * @param rr Square of escape radius (for the point when escapes)
 * @param z Escape point
 * @param der Derivative
 */
data class PointResult<R : Number>(val n: Int, val rr: R, val z: MapPoint<R>, val der: MapPoint<R>)
