package com.lasantha.fractal.render.color

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

class RGBColorCoder(override val maxN: Int, private val blendingFactor: Double) : ColorCoder {
    private val rFactor = 1 / ln(2.0)
    private val gFactor = 1 / (3 * sqrt(2.0) * ln(2.0))
    private val bFactor = 1 / (7 * 3.0.pow(0.125) * ln(2.0))

    private val nonEscapedPointColor = encodeColor(0, 0, 0)

    override fun toRGB(n: Int, rSquare: Double): Int {
        if (n >= maxN) {
            return nonEscapedPointColor
        }

        val v = ln(ln(rSquare) / 2.0.pow(n)) / blendingFactor
        val r = round(127.5 * (1 - cos(rFactor * v))).toInt()
        val g = round(127.5 * (1 - cos(gFactor * v))).toInt()
        val b = round(127.5 * (1 - cos(bFactor * v))).toInt()

        return encodeColor(r, g, b)
    }
}