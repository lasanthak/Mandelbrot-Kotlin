package com.lasantha.fractal

import com.lasantha.fractal.matrix.MatrixRange
import kotlin.math.*

class Mandelbrot(private val maxN: Int,
                 private val escapeRadius: Double,
                 private val samplesSqrt: Int) {

    private val twoPI = 2*PI
    private val escapeRSquare = escapeRadius * escapeRadius

    /*
    * Complex number Zc is in the Mandelbrot set if this is bounded for very large n, starting with Z0.
    * Zn+1 = Zn ^ 2 + Zc
    * See https://en.wikipedia.org/wiki/Mandelbrot_set
     */
    fun calcMandelbrotDeep(range: MatrixRange<Double>): Int {
        val values = IntArray(samplesSqrt * samplesSqrt)
        val xPixW = (range.x2 - range.x1) / samplesSqrt
        val yPixH = (range.y1 - range.y2) / samplesSqrt

        val x1Vals = DoubleArray(samplesSqrt + 1)
        val y1Vals = DoubleArray(samplesSqrt + 1)
        x1Vals[0] = range.x1
        y1Vals[0] = range.y1
        for (i in 1..samplesSqrt) { // nSqrt inclusive
            x1Vals[i] = x1Vals[i - 1] + xPixW
            y1Vals[i] = y1Vals[i - 1] - yPixH
        }

        for (i in 0 until samplesSqrt) {
            for (j in 0 until samplesSqrt) {
                val pointRange = MatrixRange(x1Vals[i], x1Vals[i + 1], y1Vals[j], y1Vals[j + 1])
                values[i * samplesSqrt + j] = calcMandelbrot(pointRange)
            }
        }

        return median(values)
    }

    fun calcMandelbrot(range: MatrixRange<Double>): Int {
        // Take the mid point as Zc
        val xc = (range.x1 + range.x2) / 2
        val yc = (range.y1 + range.y2) / 2

        var x = 0.0
        var y = 0.0
        var n = 0
        var xx: Double
        var yy: Double
        var rSquare: Double
        do {
            xx = x * x
            yy = y * y
            y = (2 * x * y) + yc
            x = xx - yy + xc
            rSquare = xx + yy
        } while (++n < maxN && rSquare < escapeRSquare)

        if (n >= maxN) {
            return 0
        }

        val v = ln(rSquare) / 2.0.pow(n)
        n = round(127.5 * (1 + cos(twoPI * ln(v) / 4.9))).toInt()
        return min(maxN, n)
    }

    private fun median(array: IntArray): Int {
        val ls = array.sorted()
        val halfSize = ls.size / 2
        return if (ls.size % 2 == 1) ls[halfSize] else ls[halfSize - 1]
    }

}
