package com.lasantha.fractal.calculate

import com.lasantha.fractal.matrix.Matrix
import com.lasantha.fractal.matrix.MatrixPoint

enum class FractalType {
    MANDELBROT_SET, JULIA_SET
}

interface Fractal<R : Number> {
    val type: FractalType
    val matrix: Matrix<R, Int>
    val pointCalculator: PointCalculator<R>

    fun calculatePixel(x: Int, y: Int): PointResult<R>


    /**
     * Complex number Zc is in the Mandelbrot set if this is bounded for very large n, starting with Z0.
     * Zn+1 = Zn ^ 2 + Zc
     * See https://en.wikipedia.org/wiki/Mandelbrot_set
     */
//    fun calculateDeep(range: MatrixRange<Double>, cPoint: Pair<Double, Double>?): Result {
//        val samples = createSamples(range)
//        val values = if (cPoint == null) {
//            // Mandelbrot set starting at (0,0) and current point is Zc
//            Array(samples.size) { calculatePoint(Pair(0.0, 0.0), samples[it]) }
//        } else {
//            // Julia set for Zc  starting at current point
//            Array(samples.size) { calculatePoint(samples[it], cPoint) }
//        }
//
//        // Get the median value
//        values.sortBy { it.n }
//        return (values[values.size / 2])
//    }


}

class MandelbrotSet<R : Number>(
    override val matrix: Matrix<R, Int>,
    override val pointCalculator: PointCalculator<R>
) : Fractal<R> {
    override val type: FractalType = FractalType.MANDELBROT_SET

    override fun calculatePixel(x: Int, y: Int): PointResult<R> {
        val subPoints = matrix.pixelToSubPoints(x, y)
        // Mandelbrot set starting at (0,0) and current point is Zc
        val values = Array(subPoints.size) { pointCalculator.calculatePoint(subPoints[it]) }
        // Get the median value
        values.sortBy { it.n }
        val result = values[values.size / 2]
        matrix.set(x, y, result.n)
        return result
    }
}

class JuliaSet<R : Number>(
    override val matrix: Matrix<R, Int>,
    override val pointCalculator: PointCalculator<R>,
    private val c: MatrixPoint<R>
) : Fractal<R> {
    override val type: FractalType = FractalType.JULIA_SET

    override fun calculatePixel(x: Int, y: Int): PointResult<R> {
        val subPoints = matrix.pixelToSubPoints(x, y)
        // Julia set for Zc  starting at current point
        val values = Array(subPoints.size) { pointCalculator.calculatePoint(subPoints[it], c) }
        // Get the median value
        values.sortBy { it.n }
        val result = values[values.size / 2]
        matrix.set(x, y, result.n)
        return result
    }
}
