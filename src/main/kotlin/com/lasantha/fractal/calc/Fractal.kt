package com.lasantha.fractal.calc

import com.lasantha.fractal.matrix.Matrix
import com.lasantha.fractal.matrix.MapPoint
import com.lasantha.fractal.matrix.MapSquare

enum class FractalType {
    MANDELBROT_SET, JULIA_SET
}

interface Fractal<R : Number> {
    val type: FractalType
    val matrix: Matrix<R, PointResult<R>>
    val pointCalculator: PointCalculator<R>

    fun calculatePixel(x: Int, y: Int, midPointSquare: MapSquare<R>): PointResult<R>
}

class MandelbrotSet<R : Number>(
    override val matrix: Matrix<R, PointResult<R>>,
    override val pointCalculator: PointCalculator<R>
) : Fractal<R> {
    override val type: FractalType = FractalType.MANDELBROT_SET

    override fun calculatePixel(x: Int, y: Int, midPointSquare: MapSquare<R>): PointResult<R> {
        val subPoints = matrix.pixelSubPoints(x, y, midPointSquare)
        // Mandelbrot set starting at (0,0) and current point is Zc
        val values = Array(subPoints.size) { pointCalculator.calculatePoint(subPoints[it]) }
        // Get the median value
        values.sortBy { it.n }
        val result = values[values.size / 2]
        matrix.set(x, y, result)
        return result
    }
}

class JuliaSet<R : Number>(
    override val matrix: Matrix<R, PointResult<R>>,
    override val pointCalculator: PointCalculator<R>,
    private val c: MapPoint<R>
) : Fractal<R> {
    override val type: FractalType = FractalType.JULIA_SET

    override fun calculatePixel(x: Int, y: Int, midPointSquare: MapSquare<R>): PointResult<R> {
        val subPoints = matrix.pixelSubPoints(x, y, midPointSquare)
        // Julia set for Zc  starting at current point
        val values = Array(subPoints.size) { pointCalculator.calculatePoint(subPoints[it], c) }
        // Get the median value
        values.sortBy { it.n }
        val result = values[values.size / 2]
        matrix.set(x, y, result)
        return result
    }
}
