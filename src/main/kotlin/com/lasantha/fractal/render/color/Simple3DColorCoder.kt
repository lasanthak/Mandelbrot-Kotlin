package com.lasantha.fractal.render.color

import com.lasantha.fractal.calculate.Result
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Simple3DColorCoder(override val maxN: Int):ColorCoder {
    private val h2 = 1.4
    private val h2PlusOne = h2 + 1
    private val angle = 35 * PI / 180
    private val vx = cos(angle)
    private val vy = sin(angle)

    override fun toRGB(r:Result): Int {
        if (r.n >= maxN) {
            return ColorCoder.INSIDE_COLOR
        }

        val u = div(r.z, r.der)
        val modU = modulus(u)
        val wx = u.first / modU
        val wy = u.second / modU
        val t = (wx * vx + wy * vy + h2) / h2PlusOne
        val gray = if (t < 0) 0 else (256 * t).toInt()
        return ColorCoder.encodeColor(gray, gray, gray)
    }

    /**
     * a/b = ((a.x * b.x + a.y * b.y) + (b.x * a.y - a.x * b.y)) / (b.x^2 + b.y^2)
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
