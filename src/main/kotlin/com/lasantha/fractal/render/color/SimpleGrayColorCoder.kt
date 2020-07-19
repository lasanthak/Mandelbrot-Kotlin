package com.lasantha.fractal.render.color

class SimpleGrayColorCoder(override val maxN: Int) : ColorCoder {
    private val nonEscapedPointColor = encodeColor(0, 0, 0)

    override fun toRGB(n: Int, rSquare: Double): Int {
        if (n >= maxN) {
            return nonEscapedPointColor
        }

        val m = n % 255
        val gray = if ((n / 255) % 2 == 0) m else 255 - m

        return encodeColor(gray, gray, gray)
    }
}