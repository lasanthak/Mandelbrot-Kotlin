package com.lasantha.fractal.render.color

class SimpleRBGColorCoder(override val maxN: Int) : ColorCoder {

    private val nonEscapedPointColor = encodeColor(0, 0, 0)

    override fun toRGB(n: Int, rSquare: Double): Int {
        if (n >= maxN) {
            return nonEscapedPointColor
        }

        val m = n % 0x00ffffff
        return if ((n / 0x00ffffff) % 2 == 0) m else 0x00ffffff - m
    }
}