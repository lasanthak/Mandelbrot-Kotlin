package com.lasantha.fractal.matrix

class DoubleMatrix(
    override val widthInPixels: Int,
    override val heightInPixels: Int,
    override val midX: Double,
    override val midY: Double,
    override val rangePixelSize: Double,
    override val subPixelCountSqrt: Int
) : Matrix<Double, Int> {

    private val topX = midX - rangePixelSize * widthInPixels / 2
    private val topY = midY + rangePixelSize * heightInPixels / 2

    private val data = Array(heightInPixels) { IntArray(widthInPixels) } // data[y][x]

    override fun get(xPixel: Int, yPixel: Int): Int {
        return data[yPixel][xPixel]
    }

    override fun set(xPixel: Int, yPixel: Int, value: Int) {
        data[yPixel][xPixel] = value
    }

    override fun pixelToRange(xPixel: Int, yPixel: Int): MatrixRange<Double> {
        val x1 = topX + (rangePixelSize * xPixel)
        val x2 = x1 + rangePixelSize
        val y1 = topY - (rangePixelSize * yPixel)
        val y2 = y1 - rangePixelSize
        return MatrixRange(x1, x2, y1, y2)
    }

    override fun pixelToSubPoints(xPixel: Int, yPixel: Int): Array<MatrixPoint<Double>> {
        val range = pixelToRange(xPixel, yPixel)

        val xPixW = (range.x2 - range.x1) / subPixelCountSqrt
        val yPixH = (range.y1 - range.y2) / subPixelCountSqrt

        val xMarkerMidPoints = DoubleArray(subPixelCountSqrt)
        val yMarkerMidPoints = DoubleArray(subPixelCountSqrt)
        xMarkerMidPoints[0] = range.x1 + xPixW / 2.0
        yMarkerMidPoints[0] = range.y1 - yPixH / 2.0
        for (a in 1 until subPixelCountSqrt) {
            xMarkerMidPoints[a] = xMarkerMidPoints[a - 1] + xPixW
            yMarkerMidPoints[a] = yMarkerMidPoints[a - 1] - yPixH
        }

        return Array(subPixelCountSqrt * subPixelCountSqrt) {
            val i = it / subPixelCountSqrt
            val j = it % subPixelCountSqrt
            MatrixPoint(xMarkerMidPoints[i], yMarkerMidPoints[j])
        }
    }
}
