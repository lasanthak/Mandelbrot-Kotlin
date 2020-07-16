package com.lasantha.fractal

import com.lasantha.fractal.matrix.MatrixRange

object Mandelbrot {
    /*
    * Complex number Zc is in the Mandelbrot set if this is bounded for very large n, starting with Z0.
    * Zn+1 = Zn ^ 2 + Zc
    * See https://en.wikipedia.org/wiki/Mandelbrot_set
     */

    fun calcMandelbrotDeep(r: MatrixRange<Double>,
                           nSqrt: Int,
                           maxIterations: Int,
                           escapeRadius: Double): Int {
        val escapeRadiusSquare = escapeRadius * escapeRadius
        val values = IntArray(nSqrt * nSqrt)
        val xPixW = (r.x2 - r.x1) / nSqrt
        val yPixH = (r.y1 - r.y2) / nSqrt

        val x1Vals = DoubleArray(nSqrt + 1)
        val y1Vals = DoubleArray(nSqrt + 1)
        x1Vals[0] = r.x1
        y1Vals[0] = r.y1
        for (i in 1..nSqrt) { // nSqrt inclusive
            x1Vals[i] = x1Vals[i - 1] + xPixW
            y1Vals[i] = y1Vals[i - 1] - yPixH
        }

        for (i in 0 until nSqrt) {
            for (j in 0 until nSqrt) {
                val pointRange = MatrixRange(x1Vals[i], x1Vals[i + 1], y1Vals[j], y1Vals[j + 1])
                values[i * nSqrt + j] = calcMandelbrot(pointRange, maxIterations, escapeRadiusSquare)
            }
        }

        return median(values)
    }

    fun calcMandelbrot(r: MatrixRange<Double>, maxIterations: Int, escapeRadiusSquare: Double): Int {
        // Take the mid point as Zc
        val xc = (r.x1 + r.x2) / 2
        val yc = (r.y1 + r.y2) / 2

        var x = 0.0
        var y = 0.0
        var n = 0
        var xx: Double
        var yy: Double
        do {
            xx = x * x
            yy = y * y
            y = (2 * x * y) + yc
            x = xx - yy + xc
        } while (++n < maxIterations && (xx + yy) < escapeRadiusSquare)

        return if (n > maxIterations) maxIterations else n
    }

    private fun median(array: IntArray): Int {
        val ls = array.sorted()
        val halfSize = ls.size / 2
        return if (ls.size % 2 == 1) ls[halfSize] else ls[halfSize - 1]
    }

}
