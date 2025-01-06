package com.lasantha.fractal.matrix

import com.lasantha.fractal.calc.PointResult

class DoubleMatrix(override val width: Int, override val height: Int) : Matrix<Double, PointResult<Double>> {

    companion object {
        fun toMapSquare(midPoint: MapPoint<Double>, size: Double, subPixelCountSqrt: Int = 1): MapSquare<Double> {
            val halfSize = size / 2.0
            val topLeftPoint = MapPoint(midPoint.x - halfSize, midPoint.y + halfSize)
            val subPixSize = size / subPixelCountSqrt
            return MapSquare(midPoint, topLeftPoint, size, subPixelCountSqrt, subPixSize)
        }
    }

    private val halfWidth = width / 2.0
    private val halfHeight = height / 2.0

    private val dummyResult = PointResult(0, 0.0, MapPoint(0.0, 0.0), MapPoint(0.0, 0.0))
    private val data = Array(height) { Array(width) { dummyResult } } // data[y][x]

    override fun get(xPixel: Int, yPixel: Int): PointResult<Double> {
        return data[yPixel][xPixel]
    }

    override fun set(xPixel: Int, yPixel: Int, value: PointResult<Double>) {
        data[yPixel][xPixel] = value
    }

    override fun pixelMidPoint(xPixel: Int, yPixel: Int, midPointSquare: MapSquare<Double>) = MapPoint(
        x = midPointSquare.midPoint.x - midPointSquare.size * (halfWidth - xPixel.toDouble()),
        y = midPointSquare.midPoint.y + midPointSquare.size * (halfHeight - yPixel.toDouble())
    )

    override fun pixelSubPoints(xPixel: Int, yPixel: Int, midPointSquare: MapSquare<Double>): Array<MapPoint<Double>> {
        val xOffset = midPointSquare.topLeftPoint.x - midPointSquare.size * (halfWidth - xPixel.toDouble())
        val yOffset = midPointSquare.topLeftPoint.y + midPointSquare.size * (halfHeight - yPixel.toDouble())
        val n = midPointSquare.subPixelCountSqrt
        val d = midPointSquare.subPixelSize
        val halfD = midPointSquare.subPixelSize / 2.0

        val xMarkerMidPoints = DoubleArray(n)
        val yMarkerMidPoints = DoubleArray(n)
        xMarkerMidPoints[0] = xOffset + halfD
        yMarkerMidPoints[0] = yOffset - halfD
        for (a in 1 until n) {
            xMarkerMidPoints[a] = xMarkerMidPoints[a - 1] + d
            yMarkerMidPoints[a] = yMarkerMidPoints[a - 1] - d
        }

        return Array(n * n) { MapPoint(xMarkerMidPoints[it / n], yMarkerMidPoints[it % n]) }
    }
}
