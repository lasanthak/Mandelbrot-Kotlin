package com.lasantha.fractal.render.color

import com.lasantha.fractal.calculate.Result
import kotlin.math.*

class Simple3DColorCoder(override val maxN: Int) : ColorCoder {
    private val angleD = 120.0
    private val angleR = angleD * PI / 180.0
    private val h2 = 1.4
    private val h2PlusOne = h2 + 1
    private val vx = cos(angleR)
    private val vy = sin(angleR)

    override fun toRGB(res: Result): Int {
        if (res.n >= maxN) {
            return ColorCoder.INSIDE_COLOR
        }

        val u = div(res.z, res.der)
        val modU = modulus(u)
        val wx = u.first / modU
        val wy = u.second / modU
        val t = (wx * vx + wy * vy + h2) / h2PlusOne
//        return interpolateColor(t, Triple(255, 234, 128)) // gold
//        return interpolateColor(t, Triple(230,255,204)) // light green
        return interpolateColor(t)
    }

    private fun interpolateColor(t: Double, baseColorRGB: Triple<Int, Int, Int>? = null): Int =
        if (baseColorRGB != null) {
            val r = if (t < 0) 0 else round(baseColorRGB.first * t).toInt()
            val g = if (t < 0) 0 else round(baseColorRGB.second * t).toInt()
            val b = if (t < 0) 0 else round(baseColorRGB.third * t).toInt()
            ColorCoder.encodeColor(r, g, b)
        } else {
            val grey = if (t < 0) 0 else round(255 * t).toInt()
            ColorCoder.encodeColor(grey, grey, grey)
        }


    /**
     * a/b = ((a.x * b.x + a.y * b.y) + i(b.x * a.y - a.x * b.y)) / (b.x^2 + b.y^2)
     */
    private fun div(a: Pair<Double, Double>, b: Pair<Double, Double>): Pair<Double, Double> {
        val d = b.first * b.first + b.second * b.second
        val x = (a.first * b.first + a.second * b.second) / d
        val y = (b.first * a.second - a.first * b.second) / d
        return Pair(x, y)
    }

    /**
     * |a| = sqrt(a.x^2 + a.y^2)
     */
    private fun modulus(a: Pair<Double, Double>): Double {
        return sqrt(a.first * a.first + a.second * a.second)
    }
}
