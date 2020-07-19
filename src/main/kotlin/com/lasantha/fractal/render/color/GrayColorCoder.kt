package com.lasantha.fractal.render.color

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round

class GrayColorCoder(override val maxN: Int, private val blendingFactor: Double) : ColorCoder {
    private val twoPI = 2 * PI

    private val nonEscapedPointColor = encodeColor(0, 0, 0)

    override fun toRGB(n: Int, rSquare: Double): Int {
        if (n >= maxN) {
            return nonEscapedPointColor
        }

        val v = ln(ln(rSquare) / 2.0.pow(n)) / blendingFactor
        val gray = round(127.5 * (1 + cos(twoPI * v))).toInt()

        return encodeColor(gray, gray, gray)
    }
}