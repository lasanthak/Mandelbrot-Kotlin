package com.lasantha.fractal

object Mandelbrot {
    const val maxIterations = 30000
    const val excapeRadiusSquare = 100.0 * 100.0

    /*
    * Complex number C is in the Mandelbrot set if this is bounded for very large n, starting with Z0.
    * Zn+1 = Zn ^ 2 + C
    * See https://en.wikipedia.org/wiki/Mandelbrot_set
     */

    fun calcMandelbrotDeep(r: MatrixRange<Double>, nSqrt: Int): Int {
        val values = IntArray(nSqrt * nSqrt)
        val xPixW = (r.x2 - r.x1) / nSqrt.toDouble()
        val yPixH = (r.y2 - r.y1) / nSqrt.toDouble()

        val x1Vals = DoubleArray(nSqrt + 1)
        val y1Vals = DoubleArray(nSqrt + 1)
        x1Vals[0] = r.x1
        y1Vals[0] = r.y1
        for (i in 1..nSqrt /* n inclusive */) {
            x1Vals[i] = x1Vals[i - 1] + xPixW
            y1Vals[i] = y1Vals[i - 1] + yPixH
        }

        for (i in 0 until nSqrt) {
            for (j in 0 until nSqrt) {
                values[i * nSqrt + j] = calcMandelbrot(MatrixRange(x1Vals[i], x1Vals[i + 1], y1Vals[j], y1Vals[j + 1]))
            }
        }

        return median(values)
    }

    fun calcMandelbrot(r: MatrixRange<Double>): Int {
        val xc = mid(r.x1, r.x2)
        val yc = mid(r.y1, r.y2)

        var x = 0.0
        var y = 0.0
        var xx: Double
        var yy: Double
        var iterations = 0
        do {
            xx = x * x
            yy = y * y
            y = (2 * x * y) + yc
            x = xx - yy + xc
        } while (++iterations < maxIterations && (xx + yy) < excapeRadiusSquare)

        return if (iterations >= maxIterations) -1 else iterations
    }

    private fun median(array: IntArray): Int {
        val ls = array.sorted()
        return if (ls.size % 2 == 1)
            ls[ls.size / 2]
        else
            (ls[ls.size / 2] + ls[(ls.size / 2) - 1]) / 2
    }

    private fun mid(a: Double, b: Double) = (a + b) / 2.0

}
